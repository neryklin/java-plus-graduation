package practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import practicum.event.exception.BadRequestException;
import practicum.event.mapper.Mapper;
import practicum.event.model.Hit;
import practicum.event.storage.StatsRepository;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void save(HitDto hitDto) {
        Hit hit = Mapper.toEntityHit(hitDto);

        statsRepository.save(hit);
    }

    @Override
    public ResponseEntity<List<StatsDto>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Start date must be before end date.");
        }

        List<StatsDto> stats;

        if (unique) {
            stats = statsRepository.findUniqueHitsByPeriodAndUris(start, end, uris);
        } else {
            stats = statsRepository.findAllHitsByPeriodAndUris(start, end, uris);
        }

        return ResponseEntity.ok(stats);
    }
}
