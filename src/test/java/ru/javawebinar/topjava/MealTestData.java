package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;

public class MealTestData {
    public static final int USER_BREAKFAST_1_ID = 100_002;
    public static final int ADMIN_BREAKFAST_1_ID = 100_011;

    public static final Meal USER_BREAKFAST_1 =
            new Meal(USER_BREAKFAST_1_ID, LocalDateTime.parse("2020-05-02T07:00"), "завтрак1", 1000);
    public static final Meal ADMIN_BREAKFAST_1 =
            new Meal(ADMIN_BREAKFAST_1_ID, LocalDateTime.parse("2020-05-02T07:00"), "А_завтрак1", 1000);

    public static Meal getNewMeal(){
        return getNewMeal(LocalDateTime.now());
    }

    public static Meal getNewMeal(LocalDateTime ldt){
        return new Meal(ldt, "НОВЫЙ", 1000);
    }

    public static Meal getUpdatedMeal() {
        Meal m = new Meal(USER_BREAKFAST_1);
        m.setDescription("О_Б_Н_О_В_Л_Е_Н_О");
        m.setCalories(15);
        return m;
    }

}
