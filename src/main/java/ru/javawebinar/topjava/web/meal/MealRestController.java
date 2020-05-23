package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.inmemory.InMemoryMealRepository;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {

    private MealService service;

    @Autowired
    private MealRestController(MealService mealService) {
        this.service = mealService;
    }

    public void save (Meal meal, int id){
        meal.setOwnerId(SecurityUtil.authUserId());
        if(meal.getId() == null){
            create(meal);
        }
        else update(meal, id);
    }

    public Meal create(Meal meal) {
        checkNew(meal);
        return service.create(meal, SecurityUtil.authUserId());
    }

    public void update(Meal meal, int id) {
        ValidationUtil.assureIdConsistent(meal, id);
        service.update(meal, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        service.delete(id, SecurityUtil.authUserId());
    }

    public Meal get(int id) {
        return service.get(id, SecurityUtil.authUserId());
    }

    public List<MealTo> getAll() {
        return MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getAll(String startDateString, String endDateString, String startTimeString, String endTimeString) {

        LocalDate startDate = isDateTimeOk(startDateString) ? LocalDate.parse(startDateString) : LocalDate.MIN;
        LocalDate endDate = isDateTimeOk(endDateString) ? LocalDate.parse(endDateString) : LocalDate.MAX;
        LocalTime startTime = isDateTimeOk(startTimeString) ? LocalTime.parse(startTimeString) : LocalTime.MIN;
        LocalTime endTime = isDateTimeOk(endTimeString) ? LocalTime.parse(endTimeString) : LocalTime.MAX;

        return MealsUtil.getFilteredTos(service.getAll(startDate, endDate, SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay(),
                startTime, endTime);
    }

    private boolean isDateTimeOk(String dateString){
        return dateString != null &&
                (dateString.matches("\\d{4}-\\d{2}-\\d{2}")
                        || dateString.matches("\\d{2}:\\d{2}"));
    }
}