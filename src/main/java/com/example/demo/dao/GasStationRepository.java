package com.example.demo.dao;

import com.example.demo.domain.GasStation;
import com.example.demo.domain.Owner;
import com.example.demo.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GasStationRepository extends JpaRepository<GasStation, Long> {

    boolean existsByIdAndNameAndAddress(Long id, String name, String address);

    boolean existsByNameAndAddress(String name, String address);

    GasStation findByNameAndAddress(String name, String address);

    boolean existsByOwnerAndNameAndAddress(Owner owner, String name, String address);

    Page<GasStation> findByOwnerIdAndState(Long idOwner, State state, Pageable pageable);

    Page<GasStation> findGasStationsByState(State state, Pageable pageable);

    GasStation findGasStationByNameAndAddressAndState(String name, String address, State state);

    GasStation findByOwnerAndNameAndAddress(Owner owner, String name, String address);

    @Query("SELECT COUNT(g) FROM GasStation g WHERE g.state = com.example.demo.enums.State.ACTIVE")
    Long countAllGasStations();
}
