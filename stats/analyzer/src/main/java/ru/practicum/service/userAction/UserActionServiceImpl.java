package ru.practicum.service.userAction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.mapper.MapperUserAction;
import ru.practicum.model.ActionType;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserActionRepository userActionRepository;
    private final MapperUserAction mapperUserAction;

    @Override
    public void saveUserAction(UserActionAvro newUserActionAvro) {
        UserAction oldUserAction = userActionRepository.findByUserIdAndEventId(newUserActionAvro.getUserId(), newUserActionAvro.getEventId());
        UserAction newUserAction = mapperUserAction.toUserAction(newUserActionAvro);
        if (oldUserAction == null) {
            userActionRepository.save(newUserAction);
            log.debug("save new weight  {}", newUserAction);
            return;
        }
        Double newWeight = getActionWeight(newUserAction.getActionType());
        Double oldWeight = getActionWeight(oldUserAction.getActionType());
        if (oldWeight >= newWeight) {
            log.debug("save not nesessary weight not big  {}, {}", oldWeight, newWeight);
            return;
        }
        oldUserAction.setActionType(newUserAction.getActionType());
        oldUserAction.setTimestamp(newUserAction.getTimestamp());
        userActionRepository.save(oldUserAction);
        log.info("save new weigth {}", oldUserAction);
    }

    @Override
    public Set<Long> findSortedEventIdsOfUser(Long userId, int maxResult) {
        HashSet<UserAction> userActionsSet =  new HashSet<>(userActionRepository.findAllByUserId(userId,
                PageRequest.of(0, maxResult, Sort.by("timestamp").descending())
        ));
        return userActionsSet.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
    }

    private Double getActionWeight(ActionType actionType) {
        return switch (actionType) {
            case LIKE -> 1.0;
            case REGISTER -> 0.8;
            case VIEW -> 0.4;
        };
    }
}