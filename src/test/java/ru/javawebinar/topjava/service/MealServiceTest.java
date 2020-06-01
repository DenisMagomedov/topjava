package ru.javawebinar.topjava.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal mealGetFromBD = service.get(USER_BREAKFAST_1_ID, USER_ID);
        // mealGetFromBD.setCalories(1); // если поменять, то с assertEquals() тест ПРОЙДЕТ, а с теми - ЗАВАЛИТСЯ
        // assertEquals() сравнивает по Иквалс (тут только по Id)
        // а эта хрень сравнивает объекты по всем полям:
        Assertions.assertThat(mealGetFromBD).isEqualToComparingFieldByField(USER_BREAKFAST_1);
        // а эта - игнорируя указанные поля:
        Assertions.assertThat(mealGetFromBD)
                .isEqualToIgnoringGivenFields(USER_BREAKFAST_1, "description");

    }

    @Test(expected = NotFoundException.class)
    public void delete() {
        service.delete(USER_BREAKFAST_1_ID, USER_ID);
        service.get(USER_BREAKFAST_1_ID, USER_ID);
    }

    @Test
    public void getBetweenHalfOpen() {
        LocalDateTime ldt = LocalDateTime.parse("2000-01-01T07:00");

        List<Meal> expectedMeals = new ArrayList<>();

        expectedMeals.add(
                service.create(getNewMeal(ldt.plusDays(2)), 511)); // 2000-01-03
        expectedMeals.add(
                service.create(getNewMeal(ldt.plusDays(1)), 511)); // 2000-01-02

        service.create(getNewMeal(ldt), 511);  // 2000-01-01
        service.create(getNewMeal(ldt.plusDays(3)), 511);  // 2000-01-04

        List<Meal> actualMeals = service.getBetweenHalfOpen(
                LocalDate.parse("2000-01-02"), LocalDate.parse("2000-01-03"), 511);

        // Сравнивает Листы по элементно и каждый элемент по полям:
        Assertions.assertThat(actualMeals).usingFieldByFieldElementComparator().isEqualTo(expectedMeals);

    }

    @Test
    public void getAll() {
        Meal newMeal = service.create(getNewMeal(LocalDateTime.now()), USER_ID);
        List<Meal> actualList = service.getAll(USER_ID);
        List<Meal> expectedList = new ArrayList<>(Arrays.asList(newMeal, USER_BREAKFAST_1));
        // Сравнивает Листы по элементно и каждый элемент по полям:
        Assertions.assertThat(actualList).usingFieldByFieldElementComparator().isEqualTo(expectedList);
    }

    @Test
    public void update() {
        Meal updated = getUpdatedMeal();
        service.update(updated, USER_ID);
        Assertions.assertThat(updated).isEqualToComparingFieldByField(service.get(USER_BREAKFAST_1_ID, USER_ID));
    }

    @Test
    public void create() {
        Meal newMeal = getNewMeal();
        Meal created = service.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        Assertions.assertThat(created).isEqualToComparingFieldByField(newMeal);
        Assertions.assertThat(service.get(newId, USER_ID)).isEqualToComparingFieldByField(newMeal);

    }

    @Test(expected = NotFoundException.class)
    public void deleteForeign(){
        service.delete(USER_BREAKFAST_1_ID, ADMIN_ID);
    }
    @Test(expected = NotFoundException.class)
    public void getForeign(){
        service.get(USER_BREAKFAST_1_ID, ADMIN_ID);
    }
    @Test(expected = NotFoundException.class)
    public void updateForeign(){
        service.update(getUpdatedMeal(), ADMIN_ID);
    }
}