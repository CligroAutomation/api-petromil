package com.example.demo.service;

import com.example.demo.dao.GasStationRepository;
import com.example.demo.dao.GasStationWorkerRepository;
import com.example.demo.dao.SurveyRepository;
import com.example.demo.domain.GasStation;
import com.example.demo.domain.GasStationWorker;
import com.example.demo.domain.Survey;
import com.example.demo.domain.dto.SurveyGasStationResponse;
import com.example.demo.domain.dto.SurveyGasStationWorkerResponse;
import com.example.demo.domain.dto.SurveyRequest;
import com.example.demo.enums.Rating;
import com.example.demo.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveyServiceImpl {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private GasStationRepository gasStationRepository;

    @Autowired
    private GasStationWorkerRepository gasStationWorkerRepository;

    public SurveyRequest createSurvey(SurveyRequest surveyRequest){

        System.out.println("Gas"+surveyRequest.idGasStation() + "Worker: "
                +surveyRequest.idGasStationWorker());

        if(surveyRequest.idGasStation() == null || surveyRequest.idGasStationWorker() == null){
            throw new RuntimeException("las ids no pueden ser nulas");

        }

        Optional<GasStation> gasStationOptional = gasStationRepository.findById(surveyRequest.idGasStation());
        Optional<GasStationWorker> gasStationWorkerOptional = gasStationWorkerRepository.findById(surveyRequest.idGasStationWorker());

        if(!gasStationOptional.isPresent()){
            throw new RuntimeException("La gasolinera no existe");
        }
        GasStation gs = gasStationOptional.get();

        if(!gasStationWorkerOptional.isPresent()){
            throw new RuntimeException("El trabajador no existe");

        }

        GasStationWorker gsw = gasStationWorkerOptional.get();

        if(gsw.getGasStation().getId() != gs.getId()){
            throw new RuntimeException("El trabajador al cual le quieres hacer la encuesta, no está asociado a la gasolinería descrita");


        }


        Survey survey = new Survey();
        survey.setGasStationWorker(gsw);
        survey.setGasStation(gs);
        survey.setRating(surveyRequest.rating());
        survey.setComment(surveyRequest.comment());

        surveyRepository.save(survey);
        return surveyRequest;

    }


    public SurveyGasStationWorkerResponse getSurveyByIdGasStationWorker(Long idGasStationWorker){

        Optional<GasStationWorker> gasStationWorkerOptional = gasStationWorkerRepository.findById(idGasStationWorker);
        if(gasStationWorkerOptional.isPresent()){
             GasStationWorker gasStationWorker = gasStationWorkerOptional.get();

             List<Survey> surveyList = surveyRepository.findByGasStationWorker(gasStationWorker);

             if(gasStationWorker.getState() == State.INACTIVE){
                 throw new RuntimeException("Este trabajador está inactiva");

             }

             if(surveyList.isEmpty()){
                 throw new RuntimeException("Este trabajador no tiene  encuestas");

             }

            SurveyGasStationWorkerResponse.WorkerDTO workerDTO = new SurveyGasStationWorkerResponse.WorkerDTO(
                    gasStationWorker.getId(),
                    gasStationWorker.getName()
            );

            List<SurveyGasStationWorkerResponse.SurveyDTO> surveyDTO = gasStationWorker.getSurveys()
                    .stream()
                    .map(survey -> new SurveyGasStationWorkerResponse.SurveyDTO(
                            survey.getId(),
                            survey.getRating(),
                            survey.getComment(),
                            survey.getDateTime()
                    ))
                    .collect(Collectors.toList());

            return new SurveyGasStationWorkerResponse(workerDTO, surveyDTO);

        }

        throw new RuntimeException("Este trabajador no existe");


    }

    public SurveyGasStationResponse getSurveyByIdGasStation(Long idGasStation){

        Optional<GasStation> gasStationWorkerOptional = gasStationRepository.findById(idGasStation);
        if(gasStationWorkerOptional.isPresent()){

            GasStation gasStation = gasStationWorkerOptional.get();

            if(gasStation.getState() == State.INACTIVE){
                throw new RuntimeException("Este gasolinera está inactiva");

            }

            List<Survey> surveyList = surveyRepository.findByGasStation(gasStation);

            if(surveyList.isEmpty()){
                throw new RuntimeException("Este gasolinera no tiene  encuestas");

            }

            SurveyGasStationResponse.GasStationDTO GasStationDTO = new SurveyGasStationResponse.GasStationDTO(
                    gasStation.getId(),
                    gasStation.getName()
            );

            List<SurveyGasStationResponse.SurveyDTO> surveyDTO = gasStation.getSurveys()
                    .stream()
                    .map(survey -> new SurveyGasStationResponse.SurveyDTO(
                            survey.getId(),
                            survey.getRating(),
                            survey.getComment(),
                            survey.getDateTime()
                    ))
                    .collect(Collectors.toList());

            return new SurveyGasStationResponse(GasStationDTO, surveyDTO);

        }

        throw new RuntimeException("Esta gasolinera no existe");


    }









}
