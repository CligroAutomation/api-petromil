package com.example.demo.dao;

import com.example.demo.domain.TopGasStationWorker;
import com.example.demo.enums.TopType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopGasStationWorkerRepository extends JpaRepository<TopGasStationWorker, Long> {

    Optional<TopGasStationWorker> findByGasStationIdAndMonthIgnoreCaseAndTopType(Long gasStationId, String month, TopType topType);
}
