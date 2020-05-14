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
            <tr class="${mealTo.excess ? 'excessColor' : 'normalColor'}">
                <td>${finalDate}</td>
                <td>${mealTo.description}</td>
                <td>${mealTo.calories}</td>
                <td>
                    <a href="meals?action=edit&id=${mealTo.id}">edit</a>
                    <a href="meals?action=delete&id=${mealTo.id}">delete</a>
                </td>
            </tr>
        </c:forEach>
    </table>

    <br/>

    <form action="meals?action=add" method="post">
        <label>
            Date:
            <input type="datetime-local" value="2020-01-01T00:00" name="datetime">
        </label>

        <label>
            Description:
            <input type="text" name="description">
        </label>

        <label>
            Calories:
            <input type="number" name="calories">
        </label>

        <input type="submit" value="Add">
    </form>


</body>
</html>