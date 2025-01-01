package com.example.demo.config.cron;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.SurveyRepository;
import com.example.demo.dao.TopGasStationRepository;
import com.example.demo.dao.TopGasStationWorkerRepository;
import com.example.demo.domain.*;
import com.example.demo.enums.TopType;
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

    //@Scheduled(cron = "0 * * * * ?")
    @Scheduled(cron = "@monthly")
    public void calculateTopGasStations(){

        List<GasStation> gasStationList = gasStationRepository.findAll();

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime previousMonth = currentTime.minusMonths(1);

        if(gasStationList.isEmpty()){
            System.out.println("No hay gasolineras");
            return;
        }

        Double highestAverage = -Double.MAX_VALUE;
        Double highestPerformance = -Double.MAX_VALUE;
        Integer badResponses = 0;
        GasStation topGasStation = null;


        Double highestAverage2 = -Double.MAX_VALUE;
        Double highestPerformance2 = -Double.MAX_VALUE;
        Integer badResponses2 = 0;
        GasStation topGasStation2 = null;



        for (GasStation gs: gasStationList){

            List<Survey> surveys = surveyRepository.findSurveysBetweenDatesAndGasStationId(previousMonth, currentTime, gs.getId());

            if(surveys.isEmpty()){
                System.out.println("No hay encuestas en el rango");
                continue;
            }

            Double totalScoreAverage = 0.0;
            Double highestPerformanceForWorker = 0.0;
            Integer badResponsesForWorker = 0;


            Double totalScoreAverage2 = 0.0;
            Double highestPerformanceForWorker2 = 0.0;
            Integer badResponsesForWorker2 = 0;



            for (Survey survey : surveys) {
                System.out.println(survey.getGasStation().getName());
                switch (survey.getRating()) {
                    case BAD -> {
                        totalScoreAverage -= 2.5;
                        highestPerformanceForWorker -= 1;
                        badResponsesForWorker += 1;

                        totalScoreAverage2 -= 2.5;
                        highestPerformanceForWorker2  -= 1;
                        badResponsesForWorker2 += 1;

                    }
                    case REGULAR -> {
                        totalScoreAverage += 2.5;
                        highestPerformanceForWorker += 0.5;

                        totalScoreAverage2 += 2.5;
                        highestPerformanceForWorker2 += 0.5;
                    }
                    case EXCELLENT -> {
                        totalScoreAverage += 5;
                        highestPerformanceForWorker += 1;

                        totalScoreAverage2 += 5;
                        highestPerformanceForWorker2  += 1;
                    }
                }
            }

            // Promedio de la puntuación
            double averageScore = totalScoreAverage / surveys.size();
            double averageScore2 = totalScoreAverage2 / surveys.size();

            System.out.println("Gasolinera " + gs.getName() + " promedio: " + averageScore + ", performance: " + highestPerformanceForWorker + ", bad responses: " + badResponsesForWorker);

            // Comparar con el trabajador con el mejor puntaje
            if (averageScore > highestAverage) {
                highestAverage = averageScore;
                topGasStation = gs;
                highestPerformance = highestPerformanceForWorker;
                badResponses = badResponsesForWorker;
            }

            if(highestPerformanceForWorker2 > highestPerformance2){
                highestPerformance2 = highestPerformanceForWorker2;
                topGasStation2 = gs;
                highestAverage2 = averageScore2;
                badResponses2 = badResponsesForWorker2;
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
            topGasStationNew.setOwner(topGasStation.getOwner());
            topGasStationNew.setTopType(TopType.AVERAGE);
            System.out.println("Se agregó La gasolinera: " + topGasStationNew.getGasStation().getName() + " para el mes de "+getPreviousMonth() +" con la puntuación de "+highestAverage);
            topGasStationRepository.save(topGasStationNew);
        }

        if(topGasStation2 != null){
            TopGasStation topGasStationNew2 = new TopGasStation();
            topGasStationNew2.setGasStation(topGasStation2);
            topGasStationNew2.setAverageScore(highestAverage2);
            topGasStationNew2.setPerformanceScore(highestPerformance2);
            topGasStationNew2.setBadScores(badResponses2);
            topGasStationNew2.setMonth(getPreviousMonth());
            topGasStationNew2.setOwner(topGasStation2.getOwner());
            topGasStationNew2.setTopType(TopType.PERFORMANCESCORE);
            System.out.println("Se agregó La gasolinera CON MEJOR PERFORMAMANCE: " + topGasStationNew2.getGasStation().getName() + " para el mes de "+getPreviousMonth() +" con la puntuación de "+highestPerformance2);
            topGasStationRepository.save(topGasStationNew2);
        }
    }

    private String getPreviousMonth() {
        LocalDate now = LocalDate.now();
        LocalDate previousMonth = now.minusMonths(1);
        return previousMonth.format(DateTimeFormatter.ofPattern("MMMM")).toUpperCase();
    }


}
