package com.example.demo.dao;

import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.dto.GasStationWorkerResponse;
import com.example.demo.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GasStationWorkerRepository extends JpaRepository<GasStationWorker, Long> {

        // Verifica que una gas Station tenga a un trabajador en especifico
        boolean existsByGasStationAndIdentification(GasStation gasStation, String identification);

        Optional<GasStationWorker> findByIdentification(String identification);

        List<GasStationWorker> findByGasStationId(Long gasStationId);

        @Query("SELECT new com.example.demo.domain.dto.GasStationWorkerResponse(w.id, w.identification, w.name, w.phone, w.image, w.gasStation.name) "
                        +
                        "FROM GasStationWorker w WHERE w.gasStation.id = :gasStationId AND w.state = :state")
        Page<GasStationWorkerResponse> findWorkersByGasStationIdAndState(@Param("gasStationId") Long gasStationId,
                        @Param("state") State state, Pageable pageable);

        @Query("SELECT COUNT(w) FROM GasStationWorker w WHERE w.gasStation.id = :gasStationId AND w.state = 0")
        Long countAllWorkers(@Param("gasStationId") Long gasStationId);

}
