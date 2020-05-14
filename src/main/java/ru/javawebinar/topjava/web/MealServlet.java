package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDAO;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);

    // чтобы фильтр по времени не сбрасывался, значения берутся отсюда
    private LocalTime startTime = LocalTime.MIN;
    private LocalTime endTime = LocalTime.parse("23:59"); // если использовать LocalTime.MAX то отображается неправильно.. хз блин почему


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to users");

        // будет кидаться на страницу, чтобы было понятно, будет ли она использоваться для редактирования
        boolean editingAttribute = false;

        // получаю параметр "action", если он есть:
        String action = request.getParameter("action");

        // Это не очень красиво, но прямо тут обрабатываю запрос на удаление по АйДишнику:
        if(action != null && action.equals("delete")){
            int id = Integer.parseInt(request.getParameter("id"));
            MealDAO.deleteMeal(id);
        }

        // И это не очень красиво, прямо тут обрабатываю запрос на редактирование по АйДишнику:
        // меняю атрибут "редактирование" на ТРУ и ложу в запрос редактируемый Meal
        if(action != null && action.equals("edit")){
            editingAttribute = true;
            int id = Integer.parseInt(request.getParameter("id"));
            Meal mealToEdit = MealDAO.getMealById(id);
            request.setAttribute("mealToEdit", mealToEdit);
        }

        // ловлю параметры и если они есть, и они не 00:00 - парсю и использую для фильтрации mealsWithExcess по такому времени
        String startTimeString = request.getParameter("starttime");
        String endTimeString = request.getParameter("endtime");

        // если строки со временем пустые, то использую старые значяения
        startTime =
                (startTimeString == null || startTimeString.equals(""))
                        ? startTime : LocalTime.parse(startTimeString);
        endTime =
                (endTimeString == null || endTimeString.equals("00:00") || endTimeString.equals(""))
                        ? endTime : LocalTime.parse(endTimeString);


        List<MealTo> mealsWithExcess = MealsUtil.filteredByStreams(MealDAO.getMeals(), startTime, endTime, 2000);

        // плюс всегда отправляю время для фильтра на страницу, чтобы всегда стояли актуальные значения в фрме
        request.setAttribute("starttimez", startTime);
        request.setAttribute("endtimez", endTime);

        request.setAttribute("editing", editingAttribute);

        request.setAttribute("mealz", mealsWithExcess);
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // [РЕШЕНИЕ!!!] руская кодировка в POST
        request.setCharacterEncoding("UTF-8");
        // лень писать кучу сервлетов, буду использовать один, проверяя параметр "postAction"

        String action = request.getParameter("postAction");

        if(action != null && action.equals("add")){
            LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("datetime"));
            String description =  request.getParameter("description");
            int calories =  Integer.parseInt(request.getParameter("calories"));

            Meal newMeal = new Meal(dateTime, description, calories);
            MealDAO.addMeal(newMeal);
        }

        if(action != null && action.equals("edit")){
            int id = Integer.parseInt(request.getParameter("idVal"));
            LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("datetime"));
            String description =  request.getParameter("description");
            int calories =  Integer.parseInt(request.getParameter("calories"));

            Meal oldMeal = new Meal(dateTime, description, calories);
            MealDAO.editMeal(id, oldMeal);
        }

        // перенаправил не меняя на doGet
        doGet(request, response);
    }
}
