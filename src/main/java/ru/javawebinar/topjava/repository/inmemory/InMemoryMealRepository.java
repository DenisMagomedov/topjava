package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);



    {
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 30, 10, 0), "AЗавтрак", 1500), 1);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 30, 13, 0), "AОбед", 1000), 1);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 30, 20, 0), "AУжин", 500), 1);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 31, 10, 0), "AЗавтрак", 1000), 1);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 31, 13, 0), "AОбед", 500), 1);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 31, 20, 0), "AУжин", 510), 1);

        save(new Meal(LocalDateTime.of(2020, Month.MAY, 30, 10, 0), "UЗавтрак", 1500), 2);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 30, 13, 0), "UОбед", 1000), 2);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 30, 20, 0), "UУжин", 500), 2);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 31, 10, 0), "UЗавтрак", 1000), 2);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 31, 13, 0), "UОбед", 500), 2);
        save(new Meal(LocalDateTime.of(2020, Month.MAY, 31, 20, 0), "UУжин", 510), 2);
    }


    @Override
    public Meal save(Meal meal, int ownerId) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setOwnerId(ownerId);
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // если такой уже Мил уже есть, то он просто апдейтится:
        // (... -> "что-то") - положит это значение к указанному ключу
        // просто если использовать "return put;" то он вернет предыдущее (замененное значение)
        // НО ПОЧЕМУ ЭТОТ МИДОР не напишет
        //          repository.put(meal.getId(), meal);
        //          return meal;
        // ПОСЛЕ if{}
        // (возможно это страховка от того, что Мил может не лечь в репозиторий... я ХУЙ ЗНАЕТ)
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int ownerId) {
        log.info("delete {}", id);
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int ownerId) {
        log.info("get {}", id);
        Meal currentMeal = repository.get(id);
        return currentMeal.getOwnerId() == ownerId ? currentMeal : null;
    }

    @Override
    public List<Meal> getAll(int ownerId) {
        log.info("getAll");
        return repository.values().stream().filter(m -> m.getOwnerId() == ownerId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getByDate(LocalDate start, LocalDate end, int owner){
        return getAll(owner).stream()
                .filter(meal -> DateTimeUtil.isBetweenInclusive(meal.getDate(), start, end))
                .collect(Collectors.toList());
    }
}

