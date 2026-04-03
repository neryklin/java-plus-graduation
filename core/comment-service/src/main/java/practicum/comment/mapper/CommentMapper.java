package practicum.comment.mapper;

import org.springframework.stereotype.Component;
import practicum.comment.dto.EventFullDto;
import practicum.comment.model.Comment;
import practicum.interaction.dto.*;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public static CommentDtoResponse toCommentDtoResponse(Comment comment, UserRequestDto user, EventFullDto event) {
        return new CommentDtoResponse(
                comment.getId(),
                comment.getCreated(),
                toEventShortDto(event),
                toUserShortDto(user),
                comment.getText()
        );
    }

    public static Comment toComment(CommentDtoRequest dto, EventFullDto eventEntity, UserRequestDto userEntity, LocalDateTime createdEntity) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setEventId(eventEntity.getId());
        comment.setUserId(userEntity.getId());
        comment.setCreated(createdEntity);
        return comment;
    }

    private static EventShortDto toEventShortDto(EventFullDto event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .build();
    }

    private static UserShortDto toUserShortDto(UserRequestDto user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
