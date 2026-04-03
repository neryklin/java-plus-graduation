package practicum.event.event.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivateEventParams {
    private Long userId;
    private Integer from;
    private Integer size;

}
