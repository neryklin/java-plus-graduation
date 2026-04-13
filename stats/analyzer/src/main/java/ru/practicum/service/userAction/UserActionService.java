package ru.practicum.service.userAction;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import java.util.Set;

public interface UserActionService {
    void saveUserAction(UserActionAvro userActionAvro);

    public Set<Long> findSortedEventIdsOfUser(Long userId, int maxResult);
}