package com.example.demo.config.cron;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.GasStationWorkerRepository;
import com.example.demo.dao.SurveyRepository;
import com.example.demo.dao.TopGasStationWorkerRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.Survey;
import com.example.demo.domain.TopGasStationWorker;
import com.example.demo.domain.dto.TopGasStationWorkerResponse;
import com.example.demo.enums.Rating;
import com.example.demo.enums.TopType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class TopWorkerScheduler {

    @Autowired
    private GasStationRepository gasStationRepository;

    @Autowired
    private GasStationWorkerRepository gasStationWorkerRepository;

    @Autowired
    private TopGasStationWorkerRepository topGasStationWorkerRepository;

    @Autowired
    private SurveyRepository surveyRepository;


    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Transactional
    //@Scheduled(cron = "0 * * * * ?") //cada minuto
    @Scheduled(cron = "@monthly")
    public void calculateTopWorkers() {



// Hora actual (primer día del mes a las 00:00:00, porque el cron lo asegura)
        LocalDateTime currentTime = LocalDateTime.now();

// Hora del primer día del mes anterior a las 00:00:00
        LocalDateTime previousMonth = currentTime.minusMonths(1);

        System.out.println("FECHA UN MES ANTES (INICIO): " + previousMonth.format(formatter));
        System.out.println("FECHA ACTUAL  (CIERRE): " + currentTime.format(formatter));

//        // Obtener la hora un minuto después (si se requiere)
//        LocalDateTime oneMinuteLater = currentTime.plusMinutes(1);
//        System.out.println("Hora un minuto después: " + oneMinuteLater.format(formatter));

        List<GasStation> gasStations = gasStationRepository.findAll();

        if (gasStations.isEmpty()) {
            System.out.println("No hay gasolineras");
            throw new RuntimeException("No hay gasolineras");
        }

        // Recorrer todas las gasolineras
        for (GasStation gs : gasStations) {

            List<GasStationWorker> gasStationWorkers = gasStationWorkerRepository.findByGasStationId(gs.getId());

            if (gasStationWorkers.isEmpty()) {
                System.out.println("No hay trabajadores en la gasolinera " + gs.getName());
                continue;
            }

            // Inicializar las variables para la comparación
            Double highestAverage = -Double.MAX_VALUE; ;
            GasStationWorker topWorker = null;
            Double highestPerformance = -Double.MAX_VALUE; ;
            Integer badResponses = 0;


            Double highestAverage2 = -Double.MAX_VALUE; ;
            GasStationWorker topWorker2 = null;
            Double highestPerformance2 = -Double.MAX_VALUE; ;
            Integer badResponses2 = 0;

            // Recorrer todos los trabajadores de la gasolinera
            for (GasStationWorker gsw : gasStationWorkers) {

                if (gsw.getSurveys().isEmpty()) {
                    System.out.println("El trabajador con nombre " + gsw.getName() + " no tiene surveys a nivel general");
                    continue;
                }

                // Obtener las encuestas en el rango de fechas
                List<Survey> surveys = surveyRepository.findSurveysBetweenDatesAndWorkerId(previousMonth, currentTime, gsw.getId());

                if (surveys.isEmpty()) {
                    System.out.println("No hay surveys en las fechas de corte para " + gsw.getName());
                    continue;
                }

                // Calcular la puntuación total
                Double totalScoreAverage = 0.0;
                Double highestPerformanceForWorker = 0.0;
                Integer badResponsesForWorker = 0;


                Double totalScoreAverage2 = 0.0;
                Double highestPerformanceForWorker2 = 0.0;
                Integer badResponsesForWorker2 = 0;

                for (Survey survey : surveys) {
                    System.out.println(survey.getGasStationWorker().getName());
                    switch (survey.getRating()) {
                        case BAD -> {
                            totalScoreAverage -= 2.5;
                            highestPerformanceForWorker -= 1;
                            badResponsesForWorker += 1;

                            totalScoreAverage2 -= 2.5;
                            highestPerformanceForWorker2 -= 1;
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
                            highestPerformanceForWorker2 += 1;


                        }
                    }
                }

                // Promedio de la puntuación
                double averageScore = totalScoreAverage / surveys.size();
                double averageScore2 = totalScoreAverage2 / surveys.size();

                System.out.println("Trabajador: " + gsw.getName() +
                        ", Encuestas: " + surveys.size() +
                        ", Promedio: " + averageScore +
                        ", Rendimiento_"+ highestPerformanceForWorker +
                        ", Bad scores: " + badResponsesForWorker);

                // Comparar con el trabajador con el mejor puntaje
                if (averageScore > highestAverage) {
                    highestAverage = averageScore;
                    topWorker = gsw;
                    highestPerformance = highestPerformanceForWorker;
                    badResponses = badResponsesForWorker;
                }

                if(highestPerformanceForWorker2 > highestPerformance2){
                    highestPerformance2 = highestPerformanceForWorker2;
                    topWorker2 = gsw;
                    highestAverage2 = averageScore2;
                    badResponses2 = badResponsesForWorker2;
                }
            }

            // Si se ha encontrado el mejor trabajador
            if (topWorker != null) {
                TopGasStationWorker topGasStationWorker1 = new TopGasStationWorker();
                topGasStationWorker1.setGasStation(gs);
                topGasStationWorker1.setWorker(topWorker);
                topGasStationWorker1.setAverageScore(highestAverage);
                topGasStationWorker1.setPerformanceScore(highestPerformance);
                topGasStationWorker1.setBadScores(badResponses);
                topGasStationWorker1.setMonth(getPreviousMonth());
                topGasStationWorker1.setTopType(TopType.AVERAGE);

                System.out.println("Se agregó el trabajador: " + topGasStationWorker1.getWorker().getName() + "con promedio de: " + topGasStationWorker1.getAverageScore()+"y rendimiento de "+ topGasStationWorker1.getPerformanceScore());

                // Guardar en la base de datos
                topGasStationWorkerRepository.save(topGasStationWorker1);


            }

            if(topWorker2 != null){
                TopGasStationWorker topGasStationWorker2 = new TopGasStationWorker();
                topGasStationWorker2.setGasStation(gs);
                topGasStationWorker2.setWorker(topWorker2);
                topGasStationWorker2.setAverageScore(highestAverage2);
                topGasStationWorker2.setPerformanceScore(highestPerformance2);
                topGasStationWorker2.setBadScores(badResponses2);
                topGasStationWorker2.setMonth(getPreviousMonth());
                topGasStationWorker2.setTopType(TopType.PERFORMANCESCORE);

                System.out.println("Se agregó el trabajador: " + topGasStationWorker2.getWorker().getName() + "con promedio de: " + topGasStationWorker2.getAverageScore()+"y rendimiento de "+ topGasStationWorker2.getPerformanceScore());
                // Guardar en la base de datos
                topGasStationWorkerRepository.save(topGasStationWorker2);


            }


        }


    }


    private String getPreviousMonth() {
        LocalDate now = LocalDate.now();
        LocalDate previousMonth = now.minusMonths(1);
        return previousMonth.format(DateTimeFormatter.ofPattern("MMMM")).toUpperCase();
    }
}
