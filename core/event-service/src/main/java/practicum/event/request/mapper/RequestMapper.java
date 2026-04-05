package practicum.event.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import practicum.event.event.model.Event;
import practicum.event.request.dto.RequestDto;
import practicum.event.request.dto.RequestEventDto;
import practicum.event.request.model.Request;
import practicum.event.request.model.RequestStatus;
import practicum.interaction.dto.UserRequestDto;

import java.time.LocalDateTime;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static Request toRequest(UserRequestDto requester, Event event, RequestStatus status) {
        Request request = new Request();
        request.setEventId(event.getId());
        request.setRequesterId(requester.getId());
        request.setCreated(LocalDateTime.now());
        request.setStatus(status);

        return request;
    }

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreated(),
                request.getRequesterId(),
                request.getEventId(),
                request.getStatus()
        );
    }

    public static RequestEventDto toRequestEventDto(Request request) {
        return new RequestEventDto(
                request.getId(),
                request.getCreated(),
                request.getRequesterId(),
                request.getEventId(),
                request.getStatus()
        );
    }

    public static RequestEventDto toRequestEventDto(RequestDto request) {
        return new RequestEventDto(
                request.getId(),
                request.getCreated(),
                request.getRequester(),
                request.getEvent(),
                request.getStatus()
        );
    }

}
