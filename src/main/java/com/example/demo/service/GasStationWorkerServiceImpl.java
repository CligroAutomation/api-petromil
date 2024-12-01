package com.example.demo.service;


import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.GasStationWorkerRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.dto.GasStationWorkerRequest;
import com.example.demo.domain.dto.GasStationWorkerResponse;
import com.example.demo.domain.dto.GasStationWorkerResponseImg;
import com.example.demo.enums.State;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<GasStationWorker> existingWorker = gasStationWorkerRepository.findByIdentification(worker.getIdentification());
        if (existingWorker.isPresent() && existingWorker.get().getState() == State.ACTIVE) {

            System.out.println("IllegalStateException(El trabajador ya existe y está activo.");
            return null;
        }

        // Verificar si el trabajador ya existe en la lista de trabajadores de la gasolinera
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
            System.out.println("la identificación no puede estar vacía");
            return null;

        }

        Optional<GasStationWorker> gasStationWorker = gasStationWorkerRepository.findByIdentification(identification);

        if (gasStationWorker.isPresent()) {

            GasStationWorker gsw = gasStationWorker.get();

            if (gsw.getState() == State.ACTIVE) {

                GasStationWorkerResponse responsef = new GasStationWorkerResponse(gsw.getId(), gsw.getIdentification(), gsw.getName(), gsw.getPhone(), gsw.getImage(), gsw.getGasStation().getName());

                return responsef;
            }


        }

        return null;

    }

    public List<GasStationWorkerResponse> getAllWorkersByIdGasStation(Long gasStationId) {

        if (gasStationId == null) {
            System.out.println("La id no puede estar vacía");
            return null;
        }

        // Verifica si la gasolinera existe
        if (!gasStationRepository.existsById(gasStationId)) {
            System.out.println("EntityNotFoundException(\"La gasolinera con ID \" + gasStationId + \" no existe.\");");
        }

        // Obtiene los trabajadores activos de la gasolinera
        List<GasStationWorkerResponse> workers = gasStationWorkerRepository.findWorkersByGasStationIdAndState(gasStationId, State.ACTIVE);

        if (workers.isEmpty()) {
            System.out.println("No hay trabajadores en esta gasolinera");

            return null;
            // Retorna la lista (puede estar vacía)

        }

        return workers;

    }

    public GasStationWorkerResponse updateGasStationWorker(GasStationWorkerRequest gasStationWorkerRequest) {


        if (gasStationWorkerRequest.idGasStationWorker() == null || gasStationWorkerRequest.idGasStation() == null) {
            System.out.println("Las ids del worker y de la estación no pueden ser nulas ");
        }

        Optional<GasStationWorker> worker = gasStationWorkerRepository.findByIdentification(gasStationWorkerRequest.identification());
        Object idGasStation = gasStationWorkerRequest.idGasStation();
        Long idGas;

        if (idGasStation instanceof Long) {
            idGas = (Long) idGasStation;
        } else if (idGasStation instanceof Integer) {
            idGas = ((Integer) idGasStation).longValue(); // Convertir Integer a Long
        } else {
            throw new IllegalArgumentException("El tipo de idGasStation no es compatible: " + idGasStation.getClass());
        }
        Optional<GasStation> gs = gasStationRepository.findById(idGas);

        if (worker.isPresent() && gs.isPresent()) {

            GasStationWorker w = worker.get();
            GasStation gas = gs.get();

            if (w.getState() == State.ACTIVE) {

                w.setId(gasStationWorkerRequest.idGasStationWorker());
                w.setName(gasStationWorkerRequest.name());
                w.setIdentification(gasStationWorkerRequest.identification());
                w.setPhone(gasStationWorkerRequest.phone());
                w.setImage(gasStationWorkerRequest.image());
                w.setGasStation(gas);
                gasStationWorkerRepository.save(w);
                gasStationRepository.save(gas);

                return new GasStationWorkerResponse(w.getId(), w.getIdentification(), w.getName(), w.getPhone(), w.getImage(), gas.getName());

            }

            System.out.println("Trabajador inactivo ");
            return null;

        }

        System.out.println("Este worker no existe o no hay gasStation ");
        return null;

    }

    public GasStationWorkerResponse deleteGasStationWorkerById(Long id) {
        Optional<GasStationWorker> worker = gasStationWorkerRepository.findById(id);

        if (worker.isPresent()) {

            GasStationWorker gsw = worker.get();

            if (gsw.getState() == State.ACTIVE) {
                gsw.setState(State.INACTIVE);
                gasStationWorkerRepository.save(gsw);
                return new GasStationWorkerResponse(gsw.getId(), gsw.getIdentification(), gsw.getName(),
                        gsw.getPhone(), gsw.getImage(), gsw.getGasStation().getName());

            }
            System.out.println("Este usuario ya ha sido eliminado anteriormente");
        }

        System.out.println("No existe un trabajador con esta ID");
        return null;


    }

    public GasStationWorkerResponse addWorkerWithImage(GasStationWorkerRequest gasStationWorkerRequest, MultipartFile image) throws IOException {

        // Validar datos de entrada
        if (gasStationWorkerRequest == null) {
            System.out.println("El trabajador no puede ser null");
            return null;
        }

        if (image == null || image.isEmpty()) {
            System.out.println("La imagen no puede ser nula o vacía");
            return null;
        }

        // Verificar si la gasolinera existe y está activa
        Optional<GasStation> optionalGasStation = gasStationRepository.findById(gasStationWorkerRequest.idGasStation());
        if (optionalGasStation.isEmpty() || optionalGasStation.get().getState() != State.ACTIVE) {
            System.out.println("La gasolinera no existe o no está activa.");
            return null;
        }

        GasStation gasStation = optionalGasStation.get();

        // Verificar si el trabajador ya existe con estado ACTIVE
        Optional<GasStationWorker> existingWorker = gasStationWorkerRepository.findByIdentification(gasStationWorkerRequest.identification());
        if (existingWorker.isPresent() && existingWorker.get().getState() == State.ACTIVE) {
            System.out.println("El trabajador ya existe y está activo.");
            return null;
        }

        // Verificar si el trabajador ya existe en la gasolinera
        Optional<GasStationWorker> existingInStation = gasStation.getWorkers().stream()
                .filter(w -> w.getIdentification().equals(gasStationWorkerRequest.identification()))
                .findFirst();

        if (existingInStation.isPresent()) {
            GasStationWorker existingWorkerInStation = existingInStation.get();

            if (existingWorkerInStation.getState() == State.ACTIVE) {
                throw new IllegalStateException("El trabajador ya está asignado y activo en la gasolinera.");
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
                gasStation
        );

        return saveAndConvertResponse(newWorker);
    }




    public GasStationWorkerResponse updateGasStationWorkerWithImage(GasStationWorkerRequest gasStationWorkerRequest, MultipartFile image) throws IOException {

        if (image == null || image.isEmpty()) {
            System.out.println("La imagen no puede ser nula o vacía");
            return null;
        }

        if (gasStationWorkerRequest.idGasStationWorker() == null || gasStationWorkerRequest.idGasStation() == null) {
            System.out.println("Las ids del worker y de la estación no pueden ser nulas ");
        }

        Optional<GasStationWorker> worker = gasStationWorkerRepository.findByIdentification(gasStationWorkerRequest.identification());
        Object idGasStation = gasStationWorkerRequest.idGasStation();
        Long idGas;

        if (idGasStation instanceof Long) {
            idGas = (Long) idGasStation;
        } else if (idGasStation instanceof Integer) {
            idGas = ((Integer) idGasStation).longValue(); // Convertir Integer a Long
        } else {
            throw new IllegalArgumentException("El tipo de idGasStation no es compatible: " + idGasStation.getClass());
        }
        Optional<GasStation> gs = gasStationRepository.findById(idGas);

        if (worker.isPresent() && gs.isPresent() && worker.get().getId() == gasStationWorkerRequest.idGasStationWorker()) {

            GasStationWorker w = worker.get();
            GasStation gas = gs.get();

            if (w.getState() == State.ACTIVE) {

                w.setId(gasStationWorkerRequest.idGasStationWorker());
                w.setName(gasStationWorkerRequest.name());
                w.setIdentification(gasStationWorkerRequest.identification());
                w.setPhone(gasStationWorkerRequest.phone());
                w.setImage(gasStationWorkerRequest.image());
                w.setGasStation(gas);
                w.setImage(cloudinaryService.uploadImage(image));
                gasStationWorkerRepository.save(w);
                gasStationRepository.save(gas);
                return new GasStationWorkerResponse(w.getId(), w.getIdentification(), w.getName(), w.getPhone(), w.getImage(), gas.getName());
            }

            System.out.println("Trabajador inactivo ");
            return null;
        }
        System.out.println("Este worker no existe o no hay gasStation ");
        return null;
    }

    private GasStationWorkerResponse saveAndConvertResponse(GasStationWorker worker) {
        GasStationWorker savedWorker = gasStationWorkerRepository.save(worker);
        return new GasStationWorkerResponse(
                savedWorker.getId(),
                savedWorker.getIdentification(),
                savedWorker.getName(),
                savedWorker.getPhone(),
                savedWorker.getImage(),
                savedWorker.getGasStation().getName()
        );
    }




}




