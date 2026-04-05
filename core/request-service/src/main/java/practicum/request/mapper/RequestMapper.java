package practicum.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import practicum.interaction.dto.UserRequestDto;
import practicum.interaction.enums.RequestStatus;
import practicum.request.dto.RequestDto;
import practicum.request.model.Event;
import practicum.request.model.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static Request toRequest(UserRequestDto requester, Event event, RequestStatus status) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Request request = new Request();
        request.setEventId(event.getId());
        request.setRequesterId(requester.getId());
        request.setCreated(LocalDateTime.parse(formattedDateTime));
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

//    public static RequestEventDto toEventRequestDto(Request request) {
//        return new RequestEventDto(
//                request.getId(),
//                request.getCreated(),
//                request.getRequesterId(),
//                request.getEventId(),
//                request.getStatus()
//        );
//    }
//
//    public static Request toRequest(RequestDto requestDto) {
//        return new Request(
//            requestDto.getId(),
//            requestDto.getCreated(),
//            requestDto.getRequester(),
//            requestDto.getEvent(),
//            requestDto.getStatus()
//        );
//    }

}
