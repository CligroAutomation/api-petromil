package com.example.demo.config.cron;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.GasStationWorkerRepository;
import com.example.demo.dao.SurveyRepository;
import com.example.demo.dao.TopGasStationWorkerRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.Survey;
import com.example.demo.domain.TopGasStationWorker;
import com.example.demo.enums.Rating;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
    //@Scheduled(cron = "0 * * * * ?")
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
            return;
        }

        // Recorrer todas las gasolineras
        for (GasStation gs : gasStations) {

            List<GasStationWorker> gasStationWorkers = gasStationWorkerRepository.findByGasStationId(gs.getId());

            if (gasStationWorkers.isEmpty()) {
                System.out.println("No hay trabajadores en la gasolinera " + gs.getName());
                continue;
            }

            // Inicializar las variables para la comparación
            Double highestAverage = 0.0;
            GasStationWorker topWorker = null;
            Double highestPerformance = 0.0;
            Integer badResponses = 0;

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

                for (Survey survey : surveys) {
                    System.out.println(survey.getGasStationWorker().getName());
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
                    topWorker = gsw;
                    highestPerformance = highestPerformanceForWorker;
                    badResponses = badResponsesForWorker;
                }
            }

            // Si se ha encontrado el mejor trabajador
            if (topWorker != null) {
                TopGasStationWorker topGasStationWorker = new TopGasStationWorker();
                topGasStationWorker.setGasStation(gs);
                topGasStationWorker.setWorker(topWorker);
                topGasStationWorker.setAverageScore(highestAverage);
                topGasStationWorker.setPerformanceScore(highestPerformance);
                topGasStationWorker.setBadScores(badResponses);
                topGasStationWorker.setMonth(getPreviousMonth());

                System.out.println("Se agregó el trabajador: " + topGasStationWorker.getWorker().getName());

                // Guardar en la base de datos
                topGasStationWorkerRepository.save(topGasStationWorker);
            }
        }
    }


    private String getPreviousMonth() {
        LocalDate now = LocalDate.now();
        LocalDate previousMonth = now.minusMonths(1);
        return previousMonth.format(DateTimeFormatter.ofPattern("MMMM")).toUpperCase();
    }
}
