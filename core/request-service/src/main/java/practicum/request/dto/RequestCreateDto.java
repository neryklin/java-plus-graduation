package practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import practicum.interaction.enums.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestCreateDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long requesterId;
    private Long eventId;
    private RequestStatus status;
}
