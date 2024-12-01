package com.example.demo.dao;

import com.example.demo.domain.Owner;
import com.example.demo.enums.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends CrudRepository<Owner, Long> {




    Owner findOwnerByName(String name);

    List<Owner> findByUserState(State state);

    boolean existsByGasStationsId(Long gasStationId);

    @Query("SELECT COUNT(g) > 0 FROM Owner o JOIN o.gasStations g WHERE o.id = :ownerId AND g.id = :gasStationId")
    boolean existsByGasStationsIdAndId(@Param("gasStationId") Long gasStationId, @Param("ownerId") Long ownerId);




}
