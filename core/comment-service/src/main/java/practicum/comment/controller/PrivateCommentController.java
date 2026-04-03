package practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import practicum.comment.service.CommentService;
import practicum.interaction.dto.CommentDtoRequest;
import practicum.interaction.dto.CommentDtoResponse;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateCommentController {

    final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoResponse create(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @Valid @RequestBody CommentDtoRequest dto) {
        log.info("create comment user userId {} for event eventId {}.", userId, eventId);
        return commentService.create(userId, eventId, dto);
    }

    @PatchMapping("/{commId}")
    public CommentDtoResponse update(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @PathVariable Long commId,
                                     @Valid @RequestBody CommentDtoRequest dto) {
        log.info("Update comment commentId {} user userId {} for event eventId {}.", commId, userId, eventId);
        return commentService.update(userId, eventId, commId, dto);
    }

    @DeleteMapping("/{commId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long eventId,
                       @PathVariable Long commId) {
        log.info("Delete comment commentId {} user userId {} for event eventId {}.", commId, userId, eventId);
        commentService.delete(userId, eventId, commId);
    }

    @GetMapping
    public List<CommentDtoResponse> findCommentsByUserIdAndEventId(@PathVariable Long userId,
                                                                   @PathVariable Long eventId) {
        log.info("Get comments user userId {} for event eventId {}.", userId, eventId);
        return commentService.getCommentsByUserIdAndEventId(userId, eventId);
    }

}
