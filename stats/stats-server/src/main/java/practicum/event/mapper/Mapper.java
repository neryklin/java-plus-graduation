package practicum.event.mapper;

import practicum.event.model.Hit;
import ru.practicum.dto.HitDto;

public class Mapper {

    public static Hit toEntityHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(hitDto.getTimestamp());

        return hit;
    }
}
