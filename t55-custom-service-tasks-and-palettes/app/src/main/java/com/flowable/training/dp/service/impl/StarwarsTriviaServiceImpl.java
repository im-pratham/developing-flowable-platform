package com.flowable.training.dp.service.impl;

import java.util.List;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.flowable.training.dp.model.Films;
import com.flowable.training.dp.model.People;
import com.flowable.training.dp.model.Planets;
import com.flowable.training.dp.model.Species;
import com.flowable.training.dp.model.Starships;
import com.flowable.training.dp.model.Vehicles;
import com.flowable.training.dp.service.StarwarsTriviaService;

@Transactional
@Service
public class StarwarsTriviaServiceImpl implements StarwarsTriviaService {

    private static final String SWAPI_BASE_URL = "https://swapi.com/";

    private final RestTemplate restTemplate;

    public StarwarsTriviaServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<People> getPeople() {
        return restTemplate.exchange(SWAPI_BASE_URL + "people", HttpMethod.GET, null, new ParameterizedTypeReference<List<People>>(){}).getBody();
    }

    @Override
    public List<Films> getFilms() {
        return restTemplate.exchange(SWAPI_BASE_URL + "films", HttpMethod.GET, null, new ParameterizedTypeReference<List<Films>>(){}).getBody();
    }

    @Override
    public List<Starships> getStarships() {
        return restTemplate.exchange(SWAPI_BASE_URL + "starships", HttpMethod.GET, null, new ParameterizedTypeReference<List<Starships>>(){}).getBody();
    }

    @Override
    public List<Vehicles> getVehicles() {
        return restTemplate.exchange(SWAPI_BASE_URL + "vehicles", HttpMethod.GET, null, new ParameterizedTypeReference<List<Vehicles
            >>(){}).getBody();
    }

    @Override
    public List<Species> getSpecies() {
        return restTemplate.exchange(SWAPI_BASE_URL + "species", HttpMethod.GET, null, new ParameterizedTypeReference<List<Species>>(){}).getBody();
    }

    @Override
    public List<Planets> getPlanets() {
        return restTemplate.exchange(SWAPI_BASE_URL + "planets", HttpMethod.GET, null, new ParameterizedTypeReference<List<Planets>>(){}).getBody();
    }

    @Override
    public List<?> getTriviaByType(String triviaType) {
        if (triviaType.equals("people")) {
            return getPeople();
        } else if (triviaType.equals("films")) {
            return getFilms();
        } else if (triviaType.equals("starships")) {
            return getStarships();
        } else if (triviaType.equals("vehicles")) {
            return getVehicles();
        } else if (triviaType.equals("species")) {
            return getSpecies();
        } else if (triviaType.equals("planets")) {
            return getPlanets();
        } else {
            throw new FlowableIllegalArgumentException("The provided trivia type " + triviaType + " does not exist.");
        }

    }

    public List<?> getTriviaByClass(Class<?> triviaClass){
        if (triviaClass.equals(People.class)) {
            return getPeople();
        } else if (triviaClass.equals(Films.class)) {
            return getFilms();
        } else if (triviaClass.equals(Starships.class)) {
            return getStarships();
        } else if (triviaClass.equals(Vehicles.class)) {
            return getVehicles();
        } else if (triviaClass.equals(Species.class)) {
            return getSpecies();
        } else if (triviaClass.equals(Planets.class)) {
            return getPlanets();
        } else {
            throw new FlowableIllegalArgumentException("The provided trivia class " + triviaClass.getCanonicalName() + " does not exist.");
        }
    }



}
