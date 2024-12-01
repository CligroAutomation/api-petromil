package com.example.demo.dao;

import com.example.demo.domain.GasStation;
import com.example.demo.domain.Owner;
import com.example.demo.enums.State;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GasStationRepository extends CrudRepository<GasStation, Long> {

    boolean existsByIdAndNameAndAddress(Long id, String name, String address);

    boolean existsByOwnerAndNameAndAddress(Owner owner, String name, String address);

    List<GasStation> findByOwnerIdAndState(Long idOwner, State state);

    List<GasStation> findGasStationsByState( State state);

}
