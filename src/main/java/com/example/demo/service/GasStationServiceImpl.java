package com.example.demo.service;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.OwnerRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.Owner;
import com.example.demo.domain.dto.GasStationRequest;
import com.example.demo.domain.dto.GasStationResponse;
import com.example.demo.domain.dto.GasStationsByOwnerResponse;
import com.example.demo.enums.State;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GasStationServiceImpl {

    @Autowired
    private GasStationRepository gasStationRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional
    public GasStationResponse createGasStation(GasStationRequest gasStationRequest, Long idOwner, MultipartFile logo, MultipartFile banner, String hexadecimalColor) throws IOException {
        // Validar que el idOwner no sea nulo

        if(logo == null || banner == null || hexadecimalColor == null){
            throw new RuntimeException("Hay imagenes que faltan por adjuntar");
        }

        if (idOwner == null) {
            throw new RuntimeException("El ID del dueño no puede ser null.");
        }

        if (logo.isEmpty() || logo == null || banner.isEmpty() || banner == null || hexadecimalColor == null || hexadecimalColor == "") {
            throw new RuntimeException("Faltan configuraciones de diseño, por favor registra todos los datos");
        }

        // Buscar al Owner, valido que exista
        Owner owner = ownerRepository.findById(idOwner)
                .orElse(null);

        if (owner == null) {
            throw new RuntimeException("La id del propietario no existe, no existe el propietario en la base de datos");
        }

        if (owner.getUser().getState() == State.INACTIVE) {
            throw new RuntimeException("La id del propietario no existe, no existe owner con esa id || Inactivo");

        }

        // Validar que la gasolinera no exista en la tabla de GasStation
        boolean exists = gasStationRepository.existsByNameAndAddress(
                gasStationRequest.name(), gasStationRequest.address());

        if (exists) {

            GasStation gs = gasStationRepository.findByNameAndAddress(gasStationRequest.name(), gasStationRequest.address());

            if (gs.getState() == State.INACTIVE) {

                boolean ownerHasGasStation = gasStationRepository.existsByOwnerAndNameAndAddress(owner,
                        gasStationRequest.name(), gasStationRequest.address());

                if (ownerHasGasStation) {

                    // Crear la nueva GasStation y asociarla con el Owner

                    gs.setName(gasStationRequest.name());
                    gs.setAddress(gasStationRequest.address());
                    gs.setOwner(owner); // Asociar al dueño
                    gs.setState(State.ACTIVE);
                    gs.setLogo(cloudinaryService.uploadImage(logo));
                    gs.setBanner(cloudinaryService.uploadImage(banner));
                    gs.setHexadecimalColor(hexadecimalColor);

                    // Guardar la nueva gasolinera en la base de datos
                    gs = gasStationRepository.save(gs);

                    // Si quieres mantener la relación bidireccional, puedes añadir la GasStation al
                    // conjunto de gasStations del Owner
                    owner.getGasStations().add(gs);
                    ownerRepository.save(owner); // Guardar al Owner con la nueva gasolinera

                    String logeImagen = gs.getLogo();
                    String bannerImage = gs.getBanner();

                    // Retornar el DTO de GasStation con los datos requeridos
                    return new GasStationResponse(gs.getId(), gs.getName(), gs.getAddress(), logeImagen, bannerImage, hexadecimalColor, gs.getOwner().getId());

                }

            }

        }
        // Verificar si el Owner ya tiene la misma GasStation usando una consulta en la
        // base de datos
        boolean ownerHasGasStation = gasStationRepository.existsByOwnerAndNameAndAddress(owner,
                gasStationRequest.name(), gasStationRequest.address());

        if (ownerHasGasStation) {

            GasStation gs = gasStationRepository.findByOwnerAndNameAndAddress(owner, gasStationRequest.name(),
                    gasStationRequest.address());

            if (gs.getState() == State.ACTIVE) {

                throw new RuntimeException("Este propietario ya tiene esta gasolinera asociada");

            }

            gs.setState(State.ACTIVE);
            ownerRepository.save(owner); // Guardar al Owner con la nueva gasolinera
            return new GasStationResponse(gs.getId(), gs.getName(), gs.getAddress(), gs.getLogo(), gs.getBanner(), gs.getHexadecimalColor(), owner.getId());

        }

        // Crear la nueva GasStation y asociarla con el Owner
        GasStation gasStation = new GasStation();
        gasStation.setName(gasStationRequest.name());
        gasStation.setAddress(gasStationRequest.address());
        gasStation.setOwner(owner); // Asociar al dueño
        gasStation.setState(State.ACTIVE);
        gasStation.setLogo(cloudinaryService.uploadImage(logo));
        gasStation.setBanner(cloudinaryService.uploadImage(banner));
        gasStation.setHexadecimalColor(hexadecimalColor);

        //String logeImagen = gasStation.getLogo();
        //String bannerImage = gasStation.getBanner();



        // Guardar la nueva gasolinera en la base de datos
        gasStation = gasStationRepository.save(gasStation);

        // Si quieres mantener la relación bidireccional, puedes añadir la GasStation al
        // conjunto de gasStations del Owner
        owner.getGasStations().add(gasStation);
        ownerRepository.save(owner); // Guardar al Owner con la nueva gasolinera

        // Retornar el DTO de GasStation con los datos requeridos
        return new GasStationResponse(gasStation.getId(), gasStation.getName(), gasStation.getAddress(),
                gasStation.getLogo(), gasStation.getBanner(), gasStation.getHexadecimalColor(),  owner.getId());
    }

    public List<GasStationsByOwnerResponse> getGasStationByOwner(Long id, Pageable pageable) {
        Optional<Owner> optionalOwner = ownerRepository.findById(id);
        // Si no existe el dueño, retorna null
        if (optionalOwner.isEmpty()) {
            throw new RuntimeException("El propietario no existe");
        }

        // Obtén el dueño
        Owner owner = optionalOwner.get();

        if (owner.getUser().getState() == State.INACTIVE) {
            throw new RuntimeException("El propietario no existe || inactivo");

        }

        // Busca las gasolineras del dueño
        Page<GasStation> gasStationsPage = gasStationRepository.findByOwnerIdAndState(owner.getId(), State.ACTIVE, pageable);

        List<GasStation> gasStations = gasStationsPage.getContent();
        // Si el dueño no tiene gasolineras, retorna null
        if (gasStations == null || gasStations.isEmpty()) {
            throw new RuntimeException("El propietario no tiene gasolineras");
        }

        // Construye la respuesta si hay gasolineras
        List<GasStationsByOwnerResponse> gasStationsResponse = new ArrayList<>();
        for (GasStation gs : gasStations) {

            gasStationsResponse.add(
                    new GasStationsByOwnerResponse(
                            gs.getId(),
                            gs.getName(),
                            gs.getAddress(),
                            gs.getOwner().getName(),
                            gs.getOwner().getId()));
        }

        return gasStationsResponse;
    }

    public List<GasStationsByOwnerResponse> getAllGasStation(Pageable pageable) {

        // Obtén las estaciones de servicio activas
        Page<GasStation> gasStationsPage = gasStationRepository.findGasStationsByState(State.ACTIVE, pageable);

        List<GasStation> gasStations = gasStationsPage.getContent();

        // Verifica si la lista está vacía o es nula
        if (gasStations == null || gasStations.isEmpty()) {
            throw new RuntimeException("No hay gasolineras activas");
            // Si no hay gasolineras activas, retorna null
        }

        // Si hay estaciones de servicio, construye la respuesta
        List<GasStationsByOwnerResponse> gasStationsByOwnerResponses = new ArrayList<>();
        for (GasStation gs : gasStations) {
            gasStationsByOwnerResponses.add(
                    new GasStationsByOwnerResponse(
                            gs.getId(),
                            gs.getName(),
                            gs.getAddress(),
                            gs.getOwner().getName(),
                            gs.getOwner().getId()));
        }

        return gasStationsByOwnerResponses;

    }

    public GasStationsByOwnerResponse updateGasStation(GasStationRequest gasStationRequest, MultipartFile logo,
                                                       MultipartFile banner, String hexadecimalColor,
                                                       Long idPropietario, Long idGasolinera) throws IOException {

        // Validación de entradas iniciales
        if (idPropietario == null || idGasolinera == null) {
            throw new RuntimeException("Las IDs no pueden ser nulas");
        }

        if (gasStationRequest.name() == null || gasStationRequest.name().trim().isEmpty() ||
                gasStationRequest.address() == null || gasStationRequest.address().trim().isEmpty()) {
            throw new RuntimeException("Ingresa al menos el nombre y la dirección");
        }

        // Obtener propietario y validar su existencia y estado
        Owner owner = ownerRepository.findById(idPropietario)
                .orElseThrow(() -> new RuntimeException("No existe el propietario"));

        if (owner.getUser().getState() == State.INACTIVE) {
            throw new RuntimeException("El propietario está inactivo");
        }

        // Obtener gasolinera y validar existencia
        GasStation existingGasStation = gasStationRepository.findById(idGasolinera)
                .orElseThrow(() -> new RuntimeException("Esta gasolinera no existe"));

        if (existingGasStation.getState() == State.INACTIVE) {
            throw new RuntimeException("La gasolinera está inactiva");
        }

        // Verificar si otra gasolinera con el mismo nombre y dirección ya existe para el propietario
        boolean ownerHasGasStation = gasStationRepository.existsByOwnerAndNameAndAddress(owner,
                gasStationRequest.name(), gasStationRequest.address());

        if (ownerHasGasStation && !existingGasStation.getId().equals(idGasolinera)) {
            throw new RuntimeException("Ya existe una gasolinera con el mismo nombre y dirección para este propietario");
        }

        // Actualización de campos
        existingGasStation.setName(gasStationRequest.name());
        existingGasStation.setAddress(gasStationRequest.address());
        existingGasStation.setOwner(owner);

        if (logo != null && !logo.isEmpty()) {
            String logoImage = cloudinaryService.uploadImage(logo);
            existingGasStation.setLogo(logoImage);
        }

        if (banner != null && !banner.isEmpty()) {
            String bannerImage = cloudinaryService.uploadImage(banner);
            existingGasStation.setBanner(bannerImage);
        }

        if (hexadecimalColor != null && !hexadecimalColor.trim().isEmpty()) {
            existingGasStation.setHexadecimalColor(hexadecimalColor);
        }

        existingGasStation.setState(State.ACTIVE);

        // Guardar actualizaciones en la base de datos
        gasStationRepository.save(existingGasStation);

        // Crear respuesta
        return new GasStationsByOwnerResponse(
                existingGasStation.getId(),
                existingGasStation.getName(),
                existingGasStation.getAddress(),
                owner.getName(),
                owner.getId()
        );
    }


    public GasStationsByOwnerResponse deleteGasStation(Long idPropietario, Long idGasolinera) {

        if (idGasolinera == null | idPropietario == null) {
            throw new RuntimeException("Los ids no pueden ser nulos");

        }
        System.out.println("Id Gasolinera" + idGasolinera);
        Optional<GasStation> gasStation = gasStationRepository.findById(idGasolinera);
        if (gasStation.isPresent()) {
            GasStation gs = gasStation.get();
            if (gs.getState() == State.ACTIVE) {

                gs.setState(State.INACTIVE);
                gasStationRepository.save(gs);

                GasStationsByOwnerResponse response = new GasStationsByOwnerResponse(gs.getId(),
                        gs.getName(), gs.getAddress(),
                        gs.getOwner().getName(), gs.getOwner().getId());
                return response;

            }
        }

        throw new RuntimeException("Gasolinera ya está inactiva");
    }

}
