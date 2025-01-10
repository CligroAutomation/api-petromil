package com.example.demo.dao;

import com.example.demo.domain.TopGasStation;
import com.example.demo.enums.TopType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopGasStationRepository extends JpaRepository<TopGasStation, Long> {

    Optional<TopGasStation> findByOwnerIdAndMonthIgnoreCaseAndTopType(Long ownerId, String month, TopType topType);


}
