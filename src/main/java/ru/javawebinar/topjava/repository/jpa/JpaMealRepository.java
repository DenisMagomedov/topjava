package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        if(meal.isNew()){
            try {
                User usrRef = em.getReference(User.class, userId);
                usrRef.getEmail();
                meal.setUser(usrRef);
                em.persist(meal);
            } catch (EntityNotFoundException e) {
                return null;
            }
            return meal;
        }
        else if (meal.getUser().getId() == userId){
            em.merge(meal);
            return meal;
        }
        return null;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        Query query = em.createQuery("DELETE FROM Meal m WHERE m.id=:m_id AND m.user.id=:u_id");
        return 0 != query.setParameter("m_id", id).setParameter("u_id", userId).executeUpdate();
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = em.find(Meal.class, id);
        return (meal != null && meal.getUser().getId() == userId) ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createQuery("SELECT m FROM Meal m WHERE m.user.id=:u_id ORDER BY m.dateTime DESC", Meal.class)
                .setParameter("u_id", userId).getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        TypedQuery<Meal> query = em.createQuery(
                "SELECT m FROM Meal m " +
                        "WHERE m.user.id=:u_id " +
                        "AND m.dateTime >=:st " +
                        "AND m.dateTime <:end " +
                        "ORDER BY m.dateTime DESC", Meal.class);
        return query.setParameter("u_id", userId)
                .setParameter("st", startDate)
                .setParameter("end", endDate).getResultList();
    }
}