package com.example.demo.dao;

import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.Survey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends CrudRepository<Survey, Long> {

    List<Survey> findByGasStationWorker(GasStationWorker gasStationWorker);
    List<Survey> findByGasStation(GasStation gasStation);


}
