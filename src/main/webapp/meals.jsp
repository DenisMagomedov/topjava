<%--
  Created by IntelliJ IDEA.
  User: superpony
  Date: 13.05.2020
  Time: 22:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Meals</title>

    <%--можно обойтись и без CSS (смю дальше)--%>
    <style type="text/css">
        table{
            text-align: center;
        }
        .normalColor{
            color: green;
        }
        .excessColor{
            color: red;
        }
    </style>

    <%--
    Тут проверяю атрибут "editing"..
    Зависит будет ли jsp'ха использоваться для редкатирования,
    или будет отображаться стандартно.
    В зависимости от прверки, создаются переменные с соответствубщими значениями.--%>
    <c:choose>
        <%--как вариант: ${editing == false} и ${editing == true}--%>
        <c:when test="${editing == false}">
            <c:url var="editOrAddPath" value="meals?postAction=add"/>
            <c:set var="buttonName" value="Add Meal"/>
            <c:set var="idVal" value="-1"/>
            <c:set var="datetimeVal" value="2020-01-01T00:00"/>
            <c:set var="descriptionVal" value=""/>
            <c:set var="caloriesVal" value="0"/>
        </c:when>
        <c:when test="${editing == true}">
            <c:url var="editOrAddPath" value="meals?postAction=edit"/>
            <c:set var="buttonName" value="Edit Meal"/>

            <%--
            Если будет редактирование элемента, то поля использующиеся для добавления нового элемента
            будут заполнены значениями из этих переменных созданных из полей редактируемого meal (mealToEdit),
            который прилетел сюда как атрибут, установленный, как ответ на запрос (жмак по ссылке "edit")
            на странице отображения элементов ("Meals").
            --%>
            <c:set var="idVal" value="${mealToEdit.id}"/>
            <c:set var="datetimeVal" value="${mealToEdit.dateTime}"/>
            <c:set var="descriptionVal" value="${mealToEdit.description}"/>
            <c:set var="caloriesVal" value="${mealToEdit.calories}"/>
        </c:when>
    </c:choose>

</head>
    <body>
    <h3><a href="index.html">Home</a></h3>
    <hr>

    <form action="meals" method="post">
        <h2>
            M E A L S
            <br/>
            <input type="time" name="starttime" value="${starttimez}">
            <input type="time" name="endtime" value="${endtimez}">
            <input type="submit" value="Filter">
        </h2>
    </form>

    <table>
        <c:forEach var="mealTo" items="${mealz}">
            <jsp:useBean id="mealTo" scope="page" type="ru.javawebinar.topjava.model.MealTo"/>
            <fmt:parseDate var="date" value="${mealTo.dateTime}" pattern="y-M-dd'T'H:m"/>
            <fmt:formatDate var="finalDate" value="${date}" pattern="yyyy.MM.dd HH:mm"/>

                <%--БЕЗ ИСПОЛЬЗОВАНИЯ CSS:
            <tr style="${mealTo.excess ? 'color: red' : 'color: forestgreen'}">--%>

                <%-- используя CSS:--%>
                <%--Тернарный оператор в JSTL:--%>
            <tr class="${mealTo.excess ? 'excessColor' : 'normalColor'}">
                <td>${finalDate}</td>
                <td>${mealTo.description}</td>
                <td>${mealTo.calories}</td>
                <td>
                    <%--Ссылки на редактирование и удаление--%>
                    <a href="meals?action=edit&id=${mealTo.id}">edit</a>
                    <a href="meals?action=delete&id=${mealTo.id}">delete</a>
                </td>
            </tr>
        </c:forEach>
    </table>

    <br/>

    <%--
    Это нижние три поля ввода "Дата" "Описание" "Калории"
    Путь зависит от атрибута "editing" (который прилетел сюда из сервлета)
       Если "editing" false, то эти поля ввода используются для создания нового элемента.
    И запрос полетит по адресу "meals?postAction=add".
       Если "editing" true, то поля заполняются данными редактируемого элемента и запрос полетит
    по адресу "meals?postAction=edit". Кароч меняется только параметр "postAction".--%>
    <form action="${editOrAddPath}" method="post">

        <%-- невидимое поле, будет использоваться для РЕДАКТИРОВАНИЯ
         (отправка в метод POST для создания нового meal с таким же id) --%>
        <input type="hidden" value="${idVal}" name="idVal">

        <label>
            Date:
            <input type="datetime-local" value="${datetimeVal}" name="datetime">
        </label>

        <label>
            Description:
            <input type="text" value="${descriptionVal}" name="description">
        </label>

        <label>
            Calories:
            <input type="number" value="${caloriesVal}" name="calories">
        </label>

        <input type="submit" value="${buttonName}">
    </form>


</body>
</html>