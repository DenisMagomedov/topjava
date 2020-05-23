package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface MealRepository {
    // null if not found, when updated
    Meal save(Meal meal, int ownerId);

    // false if not found
    boolean delete(int id, int ownerId);

    // null if not found
    Meal get(int id, int ownerId);

    Collection<Meal> getAll(int ownerId);

    public List<Meal> getByDate(LocalDate start, LocalDate end, int userId);
}
