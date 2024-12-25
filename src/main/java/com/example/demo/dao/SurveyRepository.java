package com.example.demo.dao;

import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Page<Survey> findByGasStationWorker(GasStationWorker gasStationWorker, Pageable pageable);

    Page<Survey> findByGasStation(GasStation gasStation, Pageable pageable);

//    @Query("SELECT s FROM Survey s WHERE s.id_gas_station_worker = :workerId AND s.dateTime BETWEEN :start AND :end")
//    List<Survey> findByWorkerIdAndDateBetween(@Param("workerId") Long workerId,
//                                            @Param("start") LocalDateTime start,
//                                            @Param("end") LocalDateTime end);


    @Query("SELECT s FROM Survey s WHERE s.dateTime BETWEEN :startDate AND :endDate AND s.gasStationWorker.id = :workerId ORDER BY s.id ASC")
    List<Survey> findSurveysBetweenDatesAndWorkerId(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("workerId") Long workerId);

    @Query("SELECT s FROM Survey s WHERE s.dateTime BETWEEN :startDate AND :endDate AND s.gasStation.id = :gasStationId ORDER BY s.id ASC")
    List<Survey> findSurveysBetweenDatesAndGasStationId(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("gasStationId") Long workerId);



}



