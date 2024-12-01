package com.example.demo.service;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.OwnerRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.Owner;
import com.example.demo.domain.dto.GasStationResponse;
import com.example.demo.domain.dto.GasStationsByOwnerResponse;
import com.example.demo.enums.State;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GasStationServiceImpl {

    @Autowired
    private GasStationRepository gasStationRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Transactional
    public GasStationResponse createGasStation(GasStationResponse gasStationResponse) {
        // Validar que el idOwner no sea nulo
        if (gasStationResponse.idOwner() == null) {
            throw new IllegalArgumentException("El ID del dueño no puede ser null.");
        }

        // Buscar al Owner, valido que exista
        Owner owner = ownerRepository.findById(gasStationResponse.idOwner())
                .orElse(null);

        if (owner == null) {
            return null;
        }

        if(owner.getUser().getState() == State.INACTIVE){
            return null;
        }

        // Validar que la gasolinera no exista en la tabla de GasStation
        boolean exists = gasStationRepository.existsByIdAndNameAndAddress(gasStationResponse.idGasStation(), gasStationResponse.name(), gasStationResponse.address());
        if (exists) {

            GasStation gs = gasStationRepository.findById(gasStationResponse.idGasStation()).get();

            if(gs.getState() == State.INACTIVE){

                boolean ownerHasGasStation = gasStationRepository.existsByOwnerAndNameAndAddress(owner, gasStationResponse.name(), gasStationResponse.address());

                if (ownerHasGasStation) {

                    // Crear la nueva GasStation y asociarla con el Owner

                    gs.setName(gasStationResponse.name());
                    gs.setAddress(gasStationResponse.address());
                    gs.setOwner(owner); // Asociar al dueño
                    gs.setState(State.ACTIVE);
                    // Guardar la nueva gasolinera en la base de datos
                    gs = gasStationRepository.save(gs);

                    // Si quieres mantener la relación bidireccional, puedes añadir la GasStation al conjunto de gasStations del Owner
                    owner.getGasStations().add(gs);
                    ownerRepository.save(owner); // Guardar al Owner con la nueva gasolinera

                    // Retornar el DTO de GasStation con los datos requeridos
                    return new GasStationResponse(gs.getId(), gs.getName(), gs.getAddress(), owner.getId());

                }

            }

        }
        // Verificar si el Owner ya tiene la misma GasStation usando una consulta en la base de datos
        boolean ownerHasGasStation = gasStationRepository.existsByOwnerAndNameAndAddress(owner, gasStationResponse.name(), gasStationResponse.address());

        if (ownerHasGasStation) {
            return null;
        }

        // Crear la nueva GasStation y asociarla con el Owner
        GasStation gasStation = new GasStation();
        gasStation.setName(gasStationResponse.name());
        gasStation.setAddress(gasStationResponse.address());
        gasStation.setOwner(owner); // Asociar al dueño
        gasStation.setState(State.ACTIVE);

        // Guardar la nueva gasolinera en la base de datos
        gasStation = gasStationRepository.save(gasStation);

        // Si quieres mantener la relación bidireccional, puedes añadir la GasStation al conjunto de gasStations del Owner
        owner.getGasStations().add(gasStation);
        ownerRepository.save(owner); // Guardar al Owner con la nueva gasolinera

        // Retornar el DTO de GasStation con los datos requeridos
        return new GasStationResponse(gasStation.getId(), gasStation.getName(), gasStation.getAddress(), owner.getId());
    }


    public List<GasStationsByOwnerResponse> getGasStationByOwner(Long id) {
        // Busca el dueño por su ID
        Optional<Owner> optionalOwner = ownerRepository.findById(id);
        // Si no existe el dueño, retorna null
        if (optionalOwner.isEmpty()) {
            return null;
        }


        // Obtén el dueño
        Owner owner = optionalOwner.get();

        if(owner.getUser().getState() == State.INACTIVE){
            return null;
        }

        // Busca las gasolineras del dueño
        List<GasStation> gasStations = gasStationRepository.findByOwnerIdAndState(owner.getId(), State.ACTIVE);

        // Si el dueño no tiene gasolineras, retorna null
        if (gasStations == null || gasStations.isEmpty()) {
            return null;
        }

        // Construye la respuesta si hay gasolineras
        List<GasStationsByOwnerResponse> gasStationsResponse = new ArrayList<>();
        for (GasStation gs : gasStations) {
            gasStationsResponse.add(
                    new GasStationsByOwnerResponse(
                            gs.getId(),
                            gs.getName(),
                            gs.getAddress(),
                            gs.getOwner().getName()
                    )
            );
        }

        return gasStationsResponse;
    }

    public  List<GasStationsByOwnerResponse> getAllGasStation(){

        // Obtén las estaciones de servicio activas
        List<GasStation> gasStations = gasStationRepository.findGasStationsByState(State.ACTIVE);

        // Verifica si la lista está vacía o es nula
        if (gasStations == null || gasStations.isEmpty()) {
            return null; // Si no hay gasolineras activas, retorna null
        }

        // Si hay estaciones de servicio, construye la respuesta
        List<GasStationsByOwnerResponse> gasStationsByOwnerResponses = new ArrayList<>();
        for (GasStation gs : gasStations) {
            gasStationsByOwnerResponses.add(
                    new GasStationsByOwnerResponse(
                            gs.getId(),
                            gs.getName(),
                            gs.getAddress(),
                            gs.getOwner().getName()
                    )
            );
        }

        return gasStationsByOwnerResponses;

    }

    public GasStationsByOwnerResponse updateGasStation(GasStationResponse gasStationResponse){

        //
        if(gasStationResponse.idGasStation() == null || gasStationResponse.idOwner() == null){
            return null;
        }
        Optional<Owner> own = ownerRepository.findById(gasStationResponse.idOwner());

        //Verificamos si Owner existe
        if(!own.isPresent()){
            return null;
        }

        Owner own2 = own.get();

        //Verificamos si el owner tiene el estado activo
        if(own2.getUser().getState() == State.INACTIVE){
            return null;
        }

        //Verificamos si el owner ya tiene la nueva gasStation que se guiere editar
        boolean ownerHasGasStation = gasStationRepository.existsByOwnerAndNameAndAddress(own2, gasStationResponse.name(), gasStationResponse.address());

        if (ownerHasGasStation) {
            return null;
        }
        List<GasStation> gasStations = gasStationRepository.findGasStationsByState(State.ACTIVE);

        GasStation gs = new GasStation(gasStationResponse.idGasStation(), gasStationResponse.name(), gasStationResponse.address(), own2, State.ACTIVE);

        GasStationsByOwnerResponse response = new GasStationsByOwnerResponse(gs.getId(), gs.getName(), gs.getAddress(), gs.getOwner().getName());
        gasStationRepository.save(gs);
        ownerRepository.save(own2);

        return response;

    }


    public GasStationsByOwnerResponse deleteGasStation(Long id){

        Optional<GasStation> gasStation = gasStationRepository.findById(id);
        if(gasStation.isPresent()){
            GasStation gs = gasStation.get();
            if(gs.getState() == State.ACTIVE){

                gs.setState(State.INACTIVE);
                gasStationRepository.save(gs);

                GasStationsByOwnerResponse response = new GasStationsByOwnerResponse(gs.getId(),
                        gs.getName(), gs.getAddress(),
                        gs.getName());
                return response;

            }
        }

        return null;
    }



}


