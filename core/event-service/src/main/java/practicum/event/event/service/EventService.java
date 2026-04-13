package practicum.event.event.service;

import practicum.event.event.dto.*;
import practicum.event.event.model.AdminEventParams;
import practicum.event.event.model.PrivateEventParams;
import practicum.event.event.model.PublicEventParams;
import practicum.event.request.dto.RequestEventDto;
import practicum.interaction.dto.EventShortDto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;

import java.util.List;
import java.util.Set;

public interface EventService {
    List<EventFullDto> getAdmin(AdminEventParams params);

    List<EventShortDto> getPublic(PublicEventParams params);

    List<EventShortDto> getPrivate(PrivateEventParams params);

    EventFullDto getByIdPublic(Long userId, Long eventId, PublicEventParams params);

    EventFullDto getByIdPrivate(Long userId, Long eventId);

    EventFullDto getByIdInt(Long eventId);

    EventFullDto update(Long eventId, EventUpdateAdminDto eventDto);

    EventFullDto updateInt(Long eventId, EventFullDto eventDto);

    EventFullDto updatePrivate(Long userId, Long eventId, EventUpdateUserDto eventUpdateDto);

    EventFullDto create(Long userId, EventCreateDto eventDto);

    List<RequestEventDto> getRequestsByIdPrivate(Long userId, Long eventId);

    EventResultRequestStatusDto updateRequestStatusPrivate(Long userId, Long eventId, EventUpdateRequestStatusDto updateDto);

    EventFullDto setLike(Long eventId, Long userId);

    List<EventFullDto> getRecommendations(Long userId, int maxResults);

    List<RecommendedEventProto> getInteractions(Set<Long> eventsIds);

    List<EventFullDto> getSimilarEvents(Long eventId, Long userId, int maxResults);
}

