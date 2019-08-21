package com.flowable.training.dp.service.impl;

import java.util.List;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.flowable.training.dp.model.Films;
import com.flowable.training.dp.model.People;
import com.flowable.training.dp.model.Planets;
import com.flowable.training.dp.model.Species;
import com.flowable.training.dp.model.Starships;
import com.flowable.training.dp.model.StarwarsDataResponse;
import com.flowable.training.dp.model.Vehicles;
import com.flowable.training.dp.service.StarwarsTriviaService;

@Transactional
@Service
public class StarwarsTriviaServiceImpl implements StarwarsTriviaService {

    private static final String SWAPI_BASE_URL = "https://swapi.co/api/";

    private final RestTemplate restTemplate;
    private final HttpEntity<List<?>> httpEntity;

    public StarwarsTriviaServiceImpl() {
        this.restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        this.httpEntity = new HttpEntity<>(headers);
    }

    @Override
    public List<People> getPeople() {
         return restTemplate.exchange(SWAPI_BASE_URL + "people", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<StarwarsDataResponse<People>>(){}).getBody().getResults();
    }

    @Override
    public List<Films> getFilms() {
        return restTemplate.exchange(SWAPI_BASE_URL + "films", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<StarwarsDataResponse<Films>>(){}).getBody().getResults();
    }

    @Override
    public List<Starships> getStarships() {
        return restTemplate.exchange(SWAPI_BASE_URL + "starships", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<StarwarsDataResponse<Starships>>(){}).getBody().getResults();
    }

    @Override
    public List<Vehicles> getVehicles() {
        return restTemplate.exchange(SWAPI_BASE_URL + "vehicles", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<StarwarsDataResponse<Vehicles>>(){}).getBody().getResults();
    }

    @Override
    public List<Species> getSpecies() {
        return restTemplate.exchange(SWAPI_BASE_URL + "species", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<StarwarsDataResponse<Species>>(){}).getBody().getResults();
    }

    @Override
    public List<Planets> getPlanets() {
        return restTemplate.exchange(SWAPI_BASE_URL + "planets", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<StarwarsDataResponse<Planets>>() {}).getBody().getResults();
    }
    @Override
    public People getPerson(long id) {
        return restTemplate.exchange(SWAPI_BASE_URL + "people" + "/" + id, HttpMethod.GET, httpEntity, People.class).getBody();
    }
    @Override
    public Films getFilm(long id) {
        return restTemplate.exchange(SWAPI_BASE_URL + "films" + "/" + id, HttpMethod.GET, httpEntity, Films.class).getBody();
    }
    @Override
    public Starships getStarship(long id) {
        return restTemplate.exchange(SWAPI_BASE_URL + "starships" + "/" + id, HttpMethod.GET, httpEntity, Starships.class).getBody();
    }
    @Override
    public Vehicles getVehicle(long id) {
        return restTemplate.exchange(SWAPI_BASE_URL + "vehicles" + "/" + id, HttpMethod.GET, httpEntity, Vehicles.class).getBody();
    }
    @Override
    public Planets getPlanet(long id) {
        return restTemplate.exchange(SWAPI_BASE_URL + "planets" + "/" + id, HttpMethod.GET, httpEntity, Planets.class).getBody();
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
