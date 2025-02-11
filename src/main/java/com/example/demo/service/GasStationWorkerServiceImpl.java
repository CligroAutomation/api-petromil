package com.example.demo.service;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.GasStationWorkerRepository;
import com.example.demo.dao.SurveyRepository;
import com.example.demo.dao.TopGasStationWorkerRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.Survey;
import com.example.demo.domain.TopGasStationWorker;
import com.example.demo.domain.dto.GasStationWorkerRequest;
import com.example.demo.domain.dto.GasStationWorkerResponse;
import com.example.demo.domain.dto.TopGasStationWorkerResponse;

import com.example.demo.enums.State;
import com.example.demo.enums.TopType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class GasStationWorkerServiceImpl {

    @Autowired
    private GasStationWorkerRepository gasStationWorkerRepository;

    @Autowired
    private GasStationRepository gasStationRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private TopGasStationWorkerRepository topGasStationWorkerRepository;

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

        if (image == null || image.isEmpty() && !gasStationWorkerRequest.name().equals("")
                && !gasStationWorkerRequest.identification().equals("")
                && !gasStationWorkerRequest.phone().equals("")) {

            if (worker.isPresent() && gs.isPresent() && worker.get().getId() == idTrabajador) {

                GasStationWorker w = worker.get();
                GasStation gas = gs.get();

                if (w.getState() == State.ACTIVE) {

                    w.setId(idTrabajador);
                    w.setName(gasStationWorkerRequest.name());
                    w.setIdentification(gasStationWorkerRequest.identification());
                    w.setPhone(gasStationWorkerRequest.phone());
                    w.setGasStation(gas);
                    // w.setImage(cloudinaryService.uploadImage(image));
                    gasStationWorkerRepository.save(w);
                    gasStationRepository.save(gas);
                    return new GasStationWorkerResponse(w.getId(), w.getIdentification(), w.getName(), w.getPhone(),
                            w.getImage(), gas.getName());
                }

                throw new RuntimeException("Trabajador inactivo");

            }

        }

        if (gasStationWorkerRequest.name().equals("") || gasStationWorkerRequest.identification().equals("")
                || gasStationWorkerRequest.phone().equals("") || image.isEmpty()) {
            throw new RuntimeException("Ingresa todos los datos");
        }

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("La imagen no puede ser estar nula o vacía");
        }

        if (worker.isPresent() && gs.isPresent() && worker.get().getId() == idTrabajador) {

            GasStationWorker w = worker.get();
            GasStation gas = gs.get();

            if (w.getState() == State.ACTIVE) {

                w.setId(idTrabajador);
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


    public List<TopGasStationWorkerResponse> getBestWorkerInMonthProvitional(Long idGasStation, Principal principal) {


        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime firstDayOfMonth = currentTime.withDayOfMonth(1).toLocalDate().atStartOfDay();


        // Obtener el nombre del mes en español (o cualquier otro idioma)
        String month = firstDayOfMonth.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

        System.out.println("El mes de firstDayOfMonth es: " + month);


        Optional<GasStation> gasStationOptional = gasStationRepository.findById(idGasStation);

        if (gasStationOptional.isEmpty()) {
            throw new RuntimeException("Gasolinera no encontrada");
        }

        GasStation gasStation = gasStationOptional.get();

        System.out.println(principal.getName());
        System.out.println(gasStation.getOwner().getUser().getEmail());

        if(!gasStation.getOwner().getUser().getEmail().equals(principal.getName())){
            throw new RuntimeException("No eres el dueño de la gasolinera");
        }

        List<GasStationWorker> gasStationWorkers = gasStationWorkerRepository.findByGasStationId(gasStation.getId());



        if (gasStationWorkers.isEmpty()) {
            throw new RuntimeException("No hay trabajadores en la gasolinera");
        }

        //Trabajador por prom
        double highestAverage1 = -Double.MAX_VALUE; // Inicializar con un valor bajo
        double highestperformanceScore1 = -Double.MAX_VALUE;
        Integer badScores1 = 0;

        //Trabajador por Performance
        double highestAverage2 = -Double.MAX_VALUE; // Inicializar con un valor bajo
        double highestperformanceScore2 = -Double.MAX_VALUE;
        Integer badScores2 = 0;

        GasStationWorker topWorker = null;
        GasStationWorker topWorker2 = null;


        for (GasStationWorker gasStationWorker : gasStationWorkers) {

            if (gasStationWorker.getState() == State.ACTIVE) {

                // Obtener encuestas para el trabajador en el rango de fechas
                List<Survey> surveys = surveyRepository.findSurveysBetweenDatesAndWorkerId(firstDayOfMonth, currentTime, gasStationWorker.getId());

                if (surveys.isEmpty()) {
                    System.out.println("Trabajador: " + gasStationWorker.getName() + " no tiene encuestas en el rango.");
                    continue; // Si no hay encuestas, pasamos al siguiente trabajador
                }

                // Calcular promedio de puntaje para el trabajador actual
                double totalScore1 = 0.0;
                double performanceScore1 = 0.0;
                Integer badScoresw1 = 0;

                double totalScore2 = 0.0;
                double performanceScore2 = 0.0;
                Integer badScoresw2 = 0;


                for (Survey survey : surveys) {
                    switch (survey.getRating()) {
                        case BAD ->{

                            badScoresw1 += 1;
                            totalScore1 -= 2.5;
                            performanceScore1 -= 1;

                            badScoresw2 += 1;
                            totalScore2 -= 2.5;
                            performanceScore2 -= 1;


                        }
                        case REGULAR ->{

                            totalScore1 += 2.5;
                            performanceScore1  += 0.5;

                            totalScore2 += 2.5;
                            performanceScore2  += 0.5;

                        }
                        case EXCELLENT -> {
                            totalScore1 += 5;
                            performanceScore1+= 1;

                            totalScore2 += 5;
                            performanceScore2+= 1;


                        }
                    }
                }

                double averageScore1 = totalScore1 / surveys.size();
                double averageScore2 = totalScore2 / surveys.size();


                // Imprimir datos para depuración
                System.out.println("Trabajador: " + gasStationWorker.getName() +
                        ", Encuestas: " + surveys.size() +
                        ", Promedio: " + averageScore1 +
                        ", Rendimiento_"+ performanceScore1 +
                        ", Bad scores: " + badScoresw1);

                // Actualizar el mejor trabajador si el promedio es mayor
                if (averageScore1 > highestAverage1) {
                    highestAverage1 = averageScore1;
                    highestperformanceScore1 = performanceScore1;
                    badScores1 = badScoresw1;
                    topWorker = gasStationWorker;
                }

                if(performanceScore2 > highestperformanceScore2){
                    highestperformanceScore2 = performanceScore2;
                    badScores2 = badScoresw2;
                    highestAverage2 = averageScore2;
                    topWorker2 = gasStationWorker;
                }



            } else {
                System.out.println("Trabajador: " + gasStationWorker.getName() + " está inactivo.");
            }
        }

        List<TopGasStationWorkerResponse> response = new ArrayList<>();


        if (topWorker != null) {
            System.out.println("Mejor trabajador PROM: " + topWorker.getName() + ", Promedio: " + highestAverage1);
            TopGasStationWorkerResponse worker1 = new TopGasStationWorkerResponse(null,highestAverage1,badScores1,"",
                    highestperformanceScore1, gasStation.getId(), topWorker.getId(),  TopType.AVERAGE);
            response.add(worker1);
        }

        if (topWorker2 != null) {
            System.out.println("Mejor trabajador PERFORMANCE: " + topWorker2.getName() + ", Performance: " + highestperformanceScore2);
            TopGasStationWorkerResponse worker2 = new TopGasStationWorkerResponse(null,highestAverage2,badScores2,"",
                    highestperformanceScore2, gasStation.getId(), topWorker2.getId(),  TopType.PERFORMANCESCORE);
            response.add(worker2);
        }

        if(topWorker2 == null & topWorker == null){
            throw new RuntimeException("a ningún trabajador le han hecho encuestas en lo que va del mes");

        }
        return response;

    }

    public TopGasStationWorkerResponse getTopGasStationWorkerByIdGasStationAndMonthAndTopType(Long idGasStation, String month, TopType topType, Principal principal) {


        Optional<GasStation> gasStation = gasStationRepository.findById(idGasStation);
        if(gasStation.isEmpty()){
            throw new RuntimeException("No se encontro la gasolinera");
        }
        GasStation gas = gasStation.get();

        if(!gas.getOwner().getUser().getEmail().equals(principal.getName())){
            throw new RuntimeException("No puedes acceder a una gasolinera que no es tuya");
        }
        Optional<TopGasStationWorker> topGasStationWorker = topGasStationWorkerRepository.findByGasStationIdAndMonthIgnoreCaseAndTopType(idGasStation, month, topType);

        if(topGasStationWorker.isEmpty()){
            throw new RuntimeException("No hay mejor trabajador en la gasolinera de nombre+ "+gas.getName() +" para el mes de "+month+" con el top de "+topType);
        }
        TopGasStationWorker top = topGasStationWorker.get();
        return new TopGasStationWorkerResponse(

                top.getId(),
                top.getAverageScore(),
                top.getBadScores(),
                top.getCommentsHighlighted(),
                top.getPerformanceScore(),
                top.getGasStation().getId(),
                top.getWorker().getId(),
                top.getTopType()
        );








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
