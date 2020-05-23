package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private Map<Integer, User> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        save(new User(null, "admin", "a@a.com", "", Role.ROLE_ADMIN));
        save(new User(null, "userrr", "u@u.com", "", Role.ROLE_USER));
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return repository.remove(id) != null;
    }

    @Override
    public User save(User user) {
        log.info("save {}", user);
        if(user.getId() == null){
            user.setId(counter.incrementAndGet());
            repository.put(user.getId(), user);
            return user;
        }
        // если такой уже Мил уже есть, то он просто апдейтится:
        // (... -> "что-то") - положит это значение к указанному ключу
        // просто если использовать "return put;" то он вернет предыдущее (замененное значение)
        // НО ПОЧЕМУ ЭТОТ МИДОР не напишет
        //          repository.put(meal.getId(), meal);
        //          return meal;
        // ПОСЛЕ if{}
        // (возможно это страховка от того, что Мил может не леч в репозиторий... я ХУЙ ЗНАЕТ)
        return repository.computeIfPresent(user.getId(), (id, oldMeal) -> user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return repository.get(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        return new ArrayList<>(repository.values());
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        return repository.values().stream().filter(u -> email.equals(u.getEmail())).findFirst().orElse(null);
    }
}
