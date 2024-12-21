package com.example.demo.config.cron;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.SurveyRepository;
import com.example.demo.dao.TopGasStationRepository;
import com.example.demo.dao.TopGasStationWorkerRepository;
import com.example.demo.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TopGasStationScheduler {

    @Autowired
    private GasStationRepository gasStationRepository;

    @Autowired
    private TopGasStationRepository topGasStationRepository;

    @Autowired
    private SurveyRepository surveyRepository;



    @Transactional
    @Scheduled(cron = "@monthly")
    public void calculateTopGasStations(){

        List<GasStation> gasStationList = gasStationRepository.findAll();

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime previousMonth = currentTime.minusMonths(1);

        if(gasStationList.isEmpty()){
            System.out.println("No hay gasolineras");
            return;
        }

        Double highestAverage = 0.0;
        GasStation topGasStation = null;
        Double highestPerformance = 0.0;
        Integer badResponses = 0;

        for (GasStation gs: gasStationList){

            List<Survey> surveys = surveyRepository.findSurveysBetweenDatesAndGasStationId(previousMonth, currentTime, gs.getId());

            if(surveys.isEmpty()){
                System.out.println("No hay encuestas en el rango");
                continue;
            }

            Double totalScoreAverage = 0.0;
            Double highestPerformanceForWorker = 0.0;
            Integer badResponsesForWorker = 0;

            for (Survey survey : surveys) {
                System.out.println(survey.getGasStation().getName());
                switch (survey.getRating()) {
                    case BAD -> {
                        totalScoreAverage -= 2.5;
                        highestPerformanceForWorker -= 1;
                        badResponsesForWorker += 1;
                    }
                    case REGULAR -> {
                        totalScoreAverage += 2.5;
                        highestPerformanceForWorker += 0.5;
                    }
                    case EXCELLENT -> {
                        totalScoreAverage += 5;
                        highestPerformanceForWorker += 0.5;
                    }
                }
            }

            // Promedio de la puntuación
            double averageScore = totalScoreAverage / surveys.size();

            // Comparar con el trabajador con el mejor puntaje
            if (averageScore > highestAverage) {
                highestAverage = averageScore;
                topGasStation = gs;
                highestPerformance = highestPerformanceForWorker;
                badResponses = badResponsesForWorker;
            }
        }

        // Si se ha encontrado la gasolinera con el mejor puntaje
        if (topGasStation != null) {
            TopGasStation topGasStationNew = new TopGasStation();
            topGasStationNew.setGasStation(topGasStation);
            topGasStationNew.setAverageScore(highestAverage);
            topGasStationNew.setPerformanceScore(highestPerformance);
            topGasStationNew.setBadScores(badResponses);
            topGasStationNew.setMonth(getPreviousMonth());

            System.out.println("Se agregó La gasolinera: " + topGasStationNew.getGasStation().getName());

            // Guardar en la base de datos solo la gasolinera con el mejor puntaje
            topGasStationRepository.save(topGasStationNew);
        }
    }



    private String getPreviousMonth() {
        LocalDate now = LocalDate.now();
        LocalDate previousMonth = now.minusMonths(1);
        return previousMonth.format(DateTimeFormatter.ofPattern("MMMM")).toUpperCase();
    }


}
