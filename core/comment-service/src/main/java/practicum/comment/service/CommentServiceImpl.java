package practicum.comment.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import practicum.comment.client.EventClient;
import practicum.comment.client.UserClient;
import practicum.comment.dto.EventFullDto;
import practicum.comment.mapper.CommentMapper;
import practicum.comment.model.Comment;
import practicum.comment.model.QComment;
import practicum.comment.storage.CommentRepository;
import practicum.interaction.dto.CommentDtoRequest;
import practicum.interaction.dto.CommentDtoResponse;
import practicum.interaction.dto.UserRequestDto;
import practicum.interaction.enums.EventState;
import practicum.interaction.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentServiceImpl implements CommentService {

    final CommentRepository commentRepository;
    private final EventClient eventClient;
    private final UserClient userClient;
    final CommentMapper commentMapper;

    @Override
    public CommentDtoResponse create(Long userId, Long eventId, CommentDtoRequest dto) {
        EventFullDto event = getPublishedEvent(eventId);
        UserRequestDto user = getUser(userId);
        Comment comment = commentRepository.save(commentMapper.toComment(dto, event, user, LocalDateTime.now()));
        return commentMapper.toCommentDtoResponse(comment, user, event);
    }

    @Override
    public CommentDtoResponse update(Long userId, Long eventId, Long commId, CommentDtoRequest dto) {
        EventFullDto event = getPublishedEvent(eventId);
        Comment comment = getValidComment(userId, eventId, commId);
        comment.setText(dto.getText());
        UserRequestDto user = userClient.getUsersById(List.of(userId)).getFirst();

        return commentMapper.toCommentDtoResponse(commentRepository.save(comment), user, event);
    }

    @Override
    public void delete(Long userId, Long eventId, Long commId) {
        getValidComment(userId, eventId, commId);
        commentRepository.deleteById(commId);
    }

    @Override
    public CommentDtoResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        UserRequestDto user = userClient.getUsersById(List.of(comment.getUserId())).getFirst();
        return commentMapper.toCommentDtoResponse(comment, user, getEvent(comment.getEventId()));
    }

    @Override
    public List<CommentDtoResponse> getCommentsByEventId(Long eventId) {
        EventFullDto event = getEvent(eventId);
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return comments.stream()
                .map(c -> commentMapper.toCommentDtoResponse(c, userClient.getUsersById(List.of(c.getUserId())).getFirst(), event))
                .toList();
    }

    @Override
    public List<CommentDtoResponse> getCommentsByUserIdAndEventId(Long userId, Long eventId) {
        EventFullDto event = getEvent(eventId);
        validateUserExists(userId);
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return comments.stream()
                .map(c -> commentMapper.toCommentDtoResponse(c, userClient.getUsersById(List.of(userId)).getFirst(), event))
                .toList();
    }

    @Override
    public List<CommentDtoResponse> getComments(List<Integer> users, List<Integer> events,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());

        BooleanExpression filter;
        Page<Comment> pageComments;
        if (rangeStart == null || rangeEnd == null) {
            pageComments = commentRepository.findAll(pageRequest);
        } else {
            filter = byDates(rangeStart, rangeEnd);
            pageComments = commentRepository.findAll(filter, pageRequest);
        }

        List<Comment> comments = pageComments.getContent();

        return comments.stream()
                .map(c -> commentMapper.toCommentDtoResponse(c, userClient.getUsersById(List.of(c.getUserId())).getFirst(), getEvent(c.getEventId())))
                .toList();
    }

    private BooleanExpression byDates(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null ? QComment.comment.created.after(start).and(QComment.comment.created.before(end)) : null;
    }

    @Override
    public void delete(Long commId) {
        if (!commentRepository.existsById(commId)) {
            throw new CommentNotFoundException(commId);
        }
        commentRepository.deleteById(commId);
    }

    private EventFullDto getPublishedEvent(Long eventId) {
        EventFullDto event;
        try {
            event = eventClient.getByIdInternal(eventId);
        } catch (Exception e) {
            throw new EventNotFoundException(eventId);
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventDateException("Event id=" + eventId + " is not published");
        }

        return event;
    }

    private UserRequestDto getUser(Long userId) {
        UserRequestDto user = userClient.getUsersById(List.of(userId)).getFirst();
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }

    private void validateUserExists(Long userId) {
        UserRequestDto user = userClient.getUsersById(List.of(userId)).getFirst();
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
    }

    private EventFullDto getEvent(Long eventId) {
        EventFullDto event = eventClient.getByIdInternal(eventId);
        if (event == null) {
            throw new EventNotFoundException(eventId);
        }
        return event;
    }

    private Comment getValidComment(Long userId, Long eventId, Long commId) {
        EventFullDto event = getEvent(eventId);
        validateUserExists(userId);

        Comment comment = commentRepository.findById(commId)
                .orElseThrow(() -> new CommentNotFoundException(commId));

        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new ConflictException("User id=" + userId + " is not the author of the comment");
        }

        return comment;
    }
}
