package com.example.demo.dao;

import com.example.demo.domain.Owner;
import com.example.demo.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Owner findOwnerByName(String name);

    Page<Owner> findByUserState(State state, Pageable pageable);

    boolean existsByGasStationsId(Long gasStationId);

    @Query("SELECT COUNT(g) > 0 FROM Owner o JOIN o.gasStations g WHERE o.id = :ownerId AND g.id = :gasStationId")
    boolean existsByGasStationsIdAndId(@Param("gasStationId") Long gasStationId, @Param("ownerId") Long ownerId);

    @Query("SELECT o.id FROM Owner o WHERE o.user.email = :email")
    Long getOwnerIdByEmail(@Param("email") String email);

    @Query("SELECT COUNT(o) FROM Owner o WHERE o.user.state = :state")
    Long countActiveOwners(@Param("state") State state);

}
