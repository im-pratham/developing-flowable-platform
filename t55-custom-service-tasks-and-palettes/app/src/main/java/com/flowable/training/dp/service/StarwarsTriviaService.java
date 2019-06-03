package com.flowable.training.dp.service;

import java.util.List;

import com.flowable.training.dp.model.Films;
import com.flowable.training.dp.model.People;
import com.flowable.training.dp.model.Planets;
import com.flowable.training.dp.model.Species;
import com.flowable.training.dp.model.Starships;
import com.flowable.training.dp.model.Vehicles;

public interface StarwarsTriviaService {

    List<People> getPeople();
    List<Films> getFilms();
    List<Starships> getStarships();
    List<Vehicles> getVehicles();
    List<Species> getSpecies();
    List<Planets> getPlanets();

    People getPerson(long id);
    Films getFilm(long id);
    Starships getStarship(long id);
    Vehicles getVehicle(long id);
    Planets getPlanet(long id);

    List<?> getTriviaByType(String triviaType);
    List<?> getTriviaByClass(Class<?> triviaClass);
}
