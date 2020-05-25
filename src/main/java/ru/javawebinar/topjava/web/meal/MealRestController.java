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

        // ВОТ ТУТ Я ЕБАНУЛСЯ (так и знал, когда делал)!
        // Вместо всей этой хуиты достаточно просто было сделать нормально метод проверки дат:
        /*
        public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, @Nullable T start, @Nullable T end) {
        return (start == null || value.compareTo(start) >= 0) && (end == null || value.compareTo(end) < 0);
        }
         */
        // НО ТУТ ЕЩЕ ПРОВЕРКА НА ПУСТОЕ ЗНАЧЕНИЕ....
        // МОЖТ я и не ебанулся... хз хз..
        // НО ГРОМОЗДКО как-то


        // [НОВОЕ]
        // Лучше не использовать значения MAX и MIN из-за возможных проблем при работе с базой данных
        // (слишком маленькие и слишком большие значения)
        // Идеально будет создать свои переменные:
        //      LocalDateTime MIN_LDT = LocalDateTime.of(1,1,1,0,0)
        //      LocalDateTime MAX_LDT = LocalDateTime.of(3000,1,1,0,0) (3000 год явно максимальный для этой приложули)

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