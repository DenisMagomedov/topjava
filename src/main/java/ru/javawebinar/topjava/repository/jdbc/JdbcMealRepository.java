package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);
/*
* ROW_MAPPER можно сделать свой:
	// создаем анонимный клас на основе ИНТЕРФЕЙСА RowMapper<>:
	RowMapper<Meal> ROW_MAPPER_2 = new RowMapper<Meal>() {
        @Override
		// Вручную вытаскиваю из ResultSet значения и создаю новый объект:
		// (значения из rs можно брать и по Лэйблам колонок и по Номерам колонок...)
        public Meal mapRow(ResultSet rs, int i) throws SQLException {
            return new Meal(rs.getInt("id"), rs.getTimestamp("date_time")...);
        }
    }
*/
    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;

    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /*
    * BeanPropertySqlParameterSource:
    * MapSqlParameterSource можно поменять на BeanPropertySqlParameterSource
	(поля для вставки определяются через отражение в бине и метаданные в SQL запросе).

	[НО!] Как в JdbcMealRepository. Если есть параметр, которого в бине нет (owner_id), то придется
	использовать по старинке - MapSqlParameterSource
     */

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("dateTime", Timestamp.valueOf(meal.getDateTime()))
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("ownerId", userId);
        if(meal.isNew()){
            Number newKey = insertMeal.executeAndReturnKey(map);
            meal.setId(newKey.intValue());
        } else if(0 == namedParameterJdbcTemplate.update("UPDATE meals SET date_time=:dateTime, " +
                "description=:description, calories=:calories WHERE owner_id=:ownerId AND id=:id", map)){
            return null;
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return 0 != jdbcTemplate.update("DELETE FROM meals WHERE owner_id=? AND id=?", userId, id);
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meal = jdbcTemplate.query(
                "SELECT * FROM meals WHERE owner_id=? AND id=?", ROW_MAPPER, userId, id);
        return DataAccessUtils.singleResult(meal);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM meals WHERE owner_id=? ORDER BY date_time DESC", ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        Timestamp start = Timestamp.valueOf(startDate);
        Timestamp end = Timestamp.valueOf(endDate);
        return jdbcTemplate.query(
                "SELECT * FROM meals WHERE owner_id = ? AND date_time >= ? AND date_time < ?" +
                        "ORDER BY date_time DESC",ROW_MAPPER, userId, start, end);
    }
}
