package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MealDAO {
    private static AtomicInteger autoId = new AtomicInteger(-1);
    private static Map<Integer, Meal> mealBase = new ConcurrentHashMap<>();
    static {
        addMeal(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
    }

    public static List<Meal> getMeals(){
        return new ArrayList<>(mealBase.values());
    }

    public static void addMeal(Meal meal){
        int id = autoId.incrementAndGet();
        meal.setId(id);
        mealBase.put(id, meal);
    }

    public static void addMeal(Meal... meals){
        for (Meal meal : meals) {
            addMeal(meal);
        }
    }

    public static void deleteMeal(int id) {
        mealBase.remove(id);
    }

    public static void editMeal (int id, Meal meal){
        meal.setId(id);
        mealBase.replace(id, meal);
    }

    public static Meal getMealById (int id){
        return mealBase.get(id);
    }


}
