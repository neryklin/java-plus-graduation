package practicum.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practicum.interaction.enums.RequestStatus;
import practicum.request.model.Request;

import java.util.List;
import java.util.Set;


public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndIdIn(Long eventId, Set<Long> requestIds);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

}
