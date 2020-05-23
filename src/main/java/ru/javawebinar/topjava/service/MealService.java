package ru.javawebinar.topjava.service;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {
    protected final Logger log = LoggerFactory.getLogger(MealService.class);


    private MealRepository repository;

    @Autowired
    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int ownerId) {
        return repository.save(meal, ownerId);
    }

    public void delete(int id, int ownerId) {
        checkNotFoundWithId(repository.delete(id, ownerId), id);
    }

    public Meal get(int id, int ownerId) {
        return checkNotFoundWithId(repository.get(id, ownerId), id);
    }

    public List<Meal> getAll(int ownerId) {
        return new ArrayList<>(repository.getAll(ownerId));
    }

    public List<Meal> getAll(LocalDate start, LocalDate end, int owner) {
        return new ArrayList<>(repository.getByDate(start, end, owner));
    }

    public void update(Meal meal, int ownerId) {
        checkNotFoundWithId(repository.save(meal, ownerId), meal.getId());
    }
}