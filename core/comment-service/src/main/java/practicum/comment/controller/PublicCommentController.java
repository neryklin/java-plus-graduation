package practicum.comment.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practicum.comment.service.CommentService;
import practicum.interaction.dto.CommentDtoResponse;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCommentController {

    final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDtoResponse findCommentById(@PathVariable Long commentId) {
        log.info("Get comments by comment id {}.", commentId);
        return commentService.getCommentById(commentId);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDtoResponse> findCommentsByEventId(@PathVariable Long eventId) {
        log.info("Get comments by evenr id {}.", eventId);
        return commentService.getCommentsByEventId(eventId);
    }
}