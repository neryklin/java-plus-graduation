package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.grpc.stats.action.UserActionProto;
import ru.practicum.service.CollectorService;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionHandler {
    private final CollectorService kafkaService;

    public void handle(UserActionProto userAction) {
        log.info("start convertion Proto -> Avro: {}", userAction);
        UserActionAvro request = UserActionAvro.newBuilder()
                .setUserId(userAction.getUserId())
                .setEventId(userAction.getEventId())
                .setActionType(setUserActionType(userAction.getActionType()))
                .setTimestamp(Instant.ofEpochSecond(userAction.getTimestamp().getSeconds(), userAction.getTimestamp().getNanos()))
                .build();
        log.info("ended convertion  Proto -> Avro: {}", request);
        kafkaService.sendActionFromKafka(request);
    }

    private ActionTypeAvro setUserActionType(ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            default -> throw new IllegalArgumentException("not finde action type: " + actionType);
        };
    }
}