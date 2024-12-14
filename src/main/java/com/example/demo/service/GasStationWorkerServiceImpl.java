package com.example.demo.service;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.GasStationWorkerRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.dto.GasStationWorkerRequest;
import com.example.demo.domain.dto.GasStationWorkerResponse;
import com.example.demo.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class GasStationWorkerServiceImpl {

    @Autowired
    private GasStationWorkerRepository gasStationWorkerRepository;

    @Autowired
    private GasStationRepository gasStationRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional
    public GasStationWorkerResponse addGasStationWorker(GasStationWorker worker) {

        if (worker == null) {
            System.out.println("el trabajador no puede ser null");
            return null;

        }
        // Verificar si la gasolinera existe y está activa
        Optional<GasStation> optionalGasStation = gasStationRepository.findById(worker.getGasStation().getId());
        if (optionalGasStation.isEmpty() || optionalGasStation.get().getState() != State.ACTIVE) {
            System.out.println("IllegalStateException(La gasolinera no existe o no está activa.");
            return null;

        }

        GasStation gasStation = optionalGasStation.get();

        // Verificar si el trabajador ya existe en la base de datos con estado ACTIVE
        Optional<GasStationWorker> existingWorker = gasStationWorkerRepository
                .findByIdentification(worker.getIdentification());
        if (existingWorker.isPresent() && existingWorker.get().getState() == State.ACTIVE) {

            System.out.println("IllegalStateException(El trabajador ya existe y está activo.");
            return null;
        }

        // Verificar si el trabajador ya existe en la lista de trabajadores de la
        // gasolinera
        Optional<GasStationWorker> existingInStation = gasStation.getWorkers().stream()
                .filter(w -> w.getIdentification().equals(worker.getIdentification()))
                .findFirst();

        if (existingInStation.isPresent()) {
            GasStationWorker existingWorkerInStation = existingInStation.get();
            if (existingWorkerInStation.getState() == State.ACTIVE) {
                // El trabajador ya está activo en la gasolinera, no se agrega
                System.out.println("IllegalStateException(El trabajador ya está asignado y activo en la gasolinera.");
                return null;

            } else if (existingWorkerInStation.getState() == State.INACTIVE) {
                // El trabajador está inactivo, se actualiza a activo
                existingWorkerInStation.setState(State.ACTIVE);
                existingWorkerInStation.setName(worker.getName());
                existingWorkerInStation.setPhone(worker.getPhone());
                existingWorkerInStation.setImage(worker.getImage());

                // Guardar el trabajador actualizado
                gasStationWorkerRepository.save(existingWorkerInStation);

                return new GasStationWorkerResponse(existingWorkerInStation.getId(),
                        existingWorkerInStation.getIdentification(),
                        existingWorkerInStation.getName(),
                        existingWorkerInStation.getPhone(),
                        existingWorkerInStation.getImage(),
                        existingWorkerInStation.getGasStation().getName());
            }
        }

        // Si no existe el trabajador en la gasolinera, se agrega un nuevo trabajador
        worker.setState(State.ACTIVE);

        worker.setGasStation(gasStation);
        GasStationWorker savedWorker = gasStationWorkerRepository.save(worker);

        return new GasStationWorkerResponse(savedWorker.getId(),
                savedWorker.getIdentification(),
                savedWorker.getName(),
                savedWorker.getPhone(),
                savedWorker.getImage(),
                savedWorker.getGasStation().getName());
    }

    public GasStationWorkerResponse getGasStationWorkerById(String identification) {

        if (identification == null) {
            throw new RuntimeException("Identification no pueden ser nulos");

        }

        Optional<GasStationWorker> gasStationWorker = gasStationWorkerRepository.findByIdentification(identification);

        if (gasStationWorker.isPresent()) {

            GasStationWorker gsw = gasStationWorker.get();

            if (gsw.getState() == State.ACTIVE) {

                GasStationWorkerResponse responsef = new GasStationWorkerResponse(gsw.getId(), gsw.getIdentification(),
                        gsw.getName(), gsw.getPhone(), gsw.getImage(), gsw.getGasStation().getName());

                return responsef;
            }

        }

        throw new RuntimeException("No existe un trabajador con esta identificación");

    }

    public List<GasStationWorkerResponse> getAllWorkersByIdGasStation(Long gasStationId, Pageable pageable) {

        if (gasStationId == null) {
            throw new RuntimeException("La id no puede estar vacía");

        }

        // Verifica si la gasolinera existe
        if (!gasStationRepository.existsById(gasStationId)) {
            throw new RuntimeException("La gasolinera no existe");
        }

        // Obtiene los trabajadores activos de la gasolinera
        Page<GasStationWorkerResponse> workersPage = gasStationWorkerRepository
                .findWorkersByGasStationIdAndState(gasStationId, State.ACTIVE, pageable);

        List<GasStationWorkerResponse> workers = workersPage.getContent();


        if (workers.isEmpty()) {
            throw new RuntimeException("No hay trabajadores en esta gasolinera");

            // Retorna la lista (puede estar vacía)

        }

        return workers;

    }

    // public GasStationWorkerResponse
    // updateGasStationWorker(GasStationWorkerRequest gasStationWorkerRequest) {
    //
    //
    // if (gasStationWorkerRequest.idGasStationWorker() == null ||
    // gasStationWorkerRequest.idGasStation() == null) {
    // System.out.println("Las ids del worker y de la estación no pueden ser nulas
    // ");
    // }
    //
    // Optional<GasStationWorker> worker =
    // gasStationWorkerRepository.findByIdentification(gasStationWorkerRequest.identification());
    // Object idGasStation = gasStationWorkerRequest.idGasStation();
    // Long idGas;
    //
    // if (idGasStation instanceof Long) {
    // idGas = (Long) idGasStation;
    // } else if (idGasStation instanceof Integer) {
    // idGas = ((Integer) idGasStation).longValue(); // Convertir Integer a Long
    // } else {
    // throw new IllegalArgumentException("El tipo de idGasStation no es compatible:
    // " + idGasStation.getClass());
    // }
    // Optional<GasStation> gs = gasStationRepository.findById(idGas);
    //
    // if (worker.isPresent() && gs.isPresent()) {
    //
    // GasStationWorker w = worker.get();
    // GasStation gas = gs.get();
    //
    // if (w.getState() == State.ACTIVE) {
    //
    // w.setId(gasStationWorkerRequest.idGasStationWorker());
    // w.setName(gasStationWorkerRequest.name());
    // w.setIdentification(gasStationWorkerRequest.identification());
    // w.setPhone(gasStationWorkerRequest.phone());
    // w.setGasStation(gas);
    // gasStationWorkerRepository.save(w);
    // gasStationRepository.save(gas);
    //
    // return new GasStationWorkerResponse(w.getId(), w.getIdentification(),
    // w.getName(), w.getPhone(), w.getImage(), gas.getName());
    //
    // }
    //
    // System.out.println("Trabajador inactivo ");
    // return null;
    //
    // }
    //
    // System.out.println("Este worker no existe o no hay gasStation ");
    // return null;
    //
    // }

    public GasStationWorkerResponse deleteGasStationWorkerById(Long idGasolinera, Long idTrabajador) {

        Optional<GasStationWorker> worker = gasStationWorkerRepository.findById(idTrabajador);
        Optional<GasStation> gas = gasStationRepository.findById(idGasolinera);

        if (worker.isPresent() && gas.isPresent()) {

            GasStationWorker gsw = worker.get();

            if (gsw.getState() == State.ACTIVE) {
                gsw.setState(State.INACTIVE);
                gasStationWorkerRepository.save(gsw);
                return new GasStationWorkerResponse(gsw.getId(), gsw.getIdentification(), gsw.getName(),
                        gsw.getPhone(), gsw.getImage(), gsw.getGasStation().getName());

            }
            throw new RuntimeException("Este usuario ya ha sido eliminado anteriormente");
        }

        throw new RuntimeException("No existe un trabajador con esta ID");

    }

    public GasStationWorkerResponse addWorkerWithImage(
            @SuppressWarnings("rawtypes") GasStationWorkerRequest gasStationWorkerRequest,
            Long idGasolinera, MultipartFile image) throws IOException {

        // Validar datos de entrada
        if (gasStationWorkerRequest == null || idGasolinera == null) {
            throw new RuntimeException("El trabajador no puede ser null");

        }
        // Verificar si la gasolinera existe y está activa
        Optional<GasStation> optionalGasStation = gasStationRepository.findById(idGasolinera);
        if (optionalGasStation.isEmpty() || optionalGasStation.get().getState() != State.ACTIVE) {
            throw new RuntimeException("La gasolinera no existe o no está activa.");

        }

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("La imagen no puede ser nula o vacía");

        }

        GasStation gasStation = optionalGasStation.get();

        // Verificar si el trabajador ya existe con estado ACTIVE
        Optional<GasStationWorker> existingWorker = gasStationWorkerRepository
                .findByIdentification(gasStationWorkerRequest.identification());
        if (existingWorker.isPresent() && existingWorker.get().getState() == State.ACTIVE) {
            throw new RuntimeException("El trabajador ya existe y está activo.");

        }

        // Verificar si el trabajador ya existe en la gasolinera
        Optional<GasStationWorker> existingInStation = gasStation.getWorkers().stream()
                .filter(w -> w.getIdentification().equals(gasStationWorkerRequest.identification()))
                .findFirst();

        if (existingInStation.isPresent()) {
            GasStationWorker existingWorkerInStation = existingInStation.get();

            if (existingWorkerInStation.getState() == State.ACTIVE) {
                throw new RuntimeException("El trabajador ya está asignado y activo en la gasolinera.");
            } else if (existingWorkerInStation.getState() == State.INACTIVE) {
                // Actualizar trabajador inactivo a activo
                existingWorkerInStation.setState(State.ACTIVE);
                existingWorkerInStation.setName(gasStationWorkerRequest.name());
                existingWorkerInStation.setPhone(gasStationWorkerRequest.phone());
                existingWorkerInStation.setImage(cloudinaryService.uploadImage(image));

                // Guardar cambios
                return saveAndConvertResponse(existingWorkerInStation);
            }
        }

        // Crear un nuevo trabajador
        String imageUrl = cloudinaryService.uploadImage(image);
        GasStationWorker newWorker = new GasStationWorker(
                null,
                gasStationWorkerRequest.identification(),
                gasStationWorkerRequest.name(),
                gasStationWorkerRequest.phone(),
                imageUrl,
                State.ACTIVE,
                gasStation);

        return saveAndConvertResponse(newWorker);
    }

    public GasStationWorkerResponse updateGasStationWorkerWithImage(
            @SuppressWarnings("rawtypes") GasStationWorkerRequest gasStationWorkerRequest,
            MultipartFile image, Long idGasolinera, Long idTrabajador) throws IOException {

        if (idGasolinera == null || idTrabajador == null) {
            throw new RuntimeException("Las ids del worker y de la estación no pueden ser nulas ");
        }

        // Verificar si la gasolinera existe y está activa
        Optional<GasStation> optionalGasStation = gasStationRepository.findById(idGasolinera);
        if (optionalGasStation.isEmpty() || optionalGasStation.get().getState() != State.ACTIVE) {
            throw new RuntimeException("La gasolinera no existe o no está activa.");

        }

        Optional<GasStationWorker> worker = gasStationWorkerRepository.findById(idTrabajador);
        Object idGasStation = idGasolinera;
        Long idGas;

        if (idGasStation instanceof Long) {
            idGas = (Long) idGasStation;
        } else if (idGasStation instanceof Integer) {
            idGas = ((Integer) idGasStation).longValue(); // Convertir Integer a Long
        } else {
            throw new RuntimeException("El tipo de idGasStation no es compatible: " + idGasStation.getClass());
        }
        Optional<GasStation> gs = gasStationRepository.findById(idGas);

        if (image == null || image.isEmpty() && !gasStationWorkerRequest.name().equals("") && !gasStationWorkerRequest.identification().equals("") && !gasStationWorkerRequest.phone().equals("")) {

            if (worker.isPresent() && gs.isPresent() && worker.get().getId() == idGasolinera) {

                GasStationWorker w = worker.get();
                GasStation gas = gs.get();

                if (w.getState() == State.ACTIVE) {

                    w.setId(idGasolinera);
                    w.setName(gasStationWorkerRequest.name());
                    w.setIdentification(gasStationWorkerRequest.identification());
                    w.setPhone(gasStationWorkerRequest.phone());
                    w.setGasStation(gas);
                    //w.setImage(cloudinaryService.uploadImage(image));
                    gasStationWorkerRepository.save(w);
                    gasStationRepository.save(gas);
                    return new GasStationWorkerResponse(w.getId(), w.getIdentification(), w.getName(), w.getPhone(),
                            w.getImage(), gas.getName());
                }

                throw new RuntimeException("Trabajador inactivo");

            }

        }

        if(gasStationWorkerRequest.name().equals("") || gasStationWorkerRequest.identification().equals("") || gasStationWorkerRequest.phone().equals("") || image.isEmpty()){
            throw new RuntimeException("Ingresa todos los datos");
        }

        if(image == null || image.isEmpty()){
            throw new RuntimeException("La imagen no puede ser estar nula o vacía");
        }




        if (worker.isPresent() && gs.isPresent() && worker.get().getId() == idGasolinera) {

            GasStationWorker w = worker.get();
            GasStation gas = gs.get();

            if (w.getState() == State.ACTIVE) {

                w.setId(idGasolinera);
                w.setName(gasStationWorkerRequest.name());
                w.setIdentification(gasStationWorkerRequest.identification());
                w.setPhone(gasStationWorkerRequest.phone());
                w.setGasStation(gas);
                w.setImage(cloudinaryService.uploadImage(image));
                gasStationWorkerRepository.save(w);
                gasStationRepository.save(gas);
                return new GasStationWorkerResponse(w.getId(), w.getIdentification(), w.getName(), w.getPhone(),
                        w.getImage(), gas.getName());
            }

            throw new RuntimeException("Trabajador inactivo");

        }
        throw new RuntimeException("Este worker no existe o no hay gasStation ");

    }

    private GasStationWorkerResponse saveAndConvertResponse(GasStationWorker worker) {
        GasStationWorker savedWorker = gasStationWorkerRepository.save(worker);
        return new GasStationWorkerResponse(
                savedWorker.getId(),
                savedWorker.getIdentification(),
                savedWorker.getName(),
                savedWorker.getPhone(),
                savedWorker.getImage(),
                savedWorker.getGasStation().getName());
    }

}
