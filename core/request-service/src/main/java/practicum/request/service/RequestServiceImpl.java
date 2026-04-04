package practicum.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practicum.interaction.dto.UserRequestDto;
import practicum.interaction.enums.EventState;
import practicum.interaction.enums.RequestStatus;
import practicum.interaction.exception.CompilationNotFoundException;
import practicum.interaction.exception.ConflictException;
import practicum.interaction.exception.UserNotFoundException;
import practicum.request.client.EventClient;
import practicum.request.client.UserClient;
import practicum.request.dto.EventFullDto;
import practicum.request.dto.RequestDto;
import practicum.request.mapper.RequestMapper;
import practicum.request.model.Request;
import practicum.request.storage.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserClient userClient;
    private final EventClient eventClient;

    private void validateRequestCreation(UserRequestDto requester, EventFullDto event) {
        if (requestRepository.existsByRequesterIdAndEventId(requester.getId(), event.getId())) {
            log.info("Request already exists  user id={} and event id={}", requester.getId(), event.getId());
            throw new ConflictException(String.format(
                    "Request already exists user id=%d and event id=%d", requester.getId(), event.getId()));
        }

        if (event.getInitiator().getId().equals(requester.getId())) {
            log.info("Event initiator id={} not create a request event id={}", requester.getId(), event.getId());
            throw new ConflictException(String.format(
                    "Event initiator id=%d not create a request event id=%d", requester.getId(), event.getId()));
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.info("participate not published event  id={}", event.getId());
            throw new ConflictException(String.format("participate not published event id=%d", event.getId()));
        }

        if (event.getParticipantLimit() > 0 &&
                requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            log.info("Participant limit  event  id={}", event.getId());
            throw new ConflictException(String.format("Participant limit  event id=%d", event.getId()));
        }

    }

    private UserRequestDto getUser(Long userId) {
        UserRequestDto user = userClient.getUsersById(List.of(userId)).getFirst();
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }

    private EventFullDto getEvent(Long EventFullDto) {
        EventFullDto event;
        try {
            event = eventClient.getById(EventFullDto);
        } catch (Exception e) {
            throw new ConflictException("You cannot register in an unpublished event.");
        }
        return event;
    }

    @Override
    @Transactional
    public RequestDto create(Long userId, Long eventId) {
        log.info("Creating request for user with id: {} and event with id: {}", userId, eventId);
        UserRequestDto user = getUser(userId);
        EventFullDto event = getEvent(eventId);
        validateRequestCreation(user, event);
        Request request = new Request();
        request.setRequesterId(userId);
        request.setEventId(eventId);
        request.setCreated(LocalDateTime.now());
        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            EventFullDto updEvent = eventClient.updateInternal(event, eventId);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> get(Long userId) {
        UserRequestDto user = getUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto update(Long userId, Long requestId) {
        UserRequestDto user = getUser(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CompilationNotFoundException(requestId));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toRequestDto(
                requestRepository.save(request)
        );
    }

    @Override
    @Transactional
    public Request updateInternal(Request request) {
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getByEventId(Long eventId) {
        return requestRepository.findAllByEventId(eventId);
    }

    @Override
    public List<Request> getByEventIdAndIds(Long eventId, Set<Long> requestsIds) {
        return requestRepository.findAllByEventIdAndIdIn(eventId, requestsIds);

    }
}
