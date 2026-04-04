package practicum.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import practicum.interaction.dto.UserCreateDto;
import practicum.interaction.dto.UserRequestDto;
import practicum.user.model.User;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserRequestDto toUserRequestDto(User user) {
        return new UserRequestDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserCreateDto userCreateDto) {
        User user = new User();
        user.setEmail(userCreateDto.getEmail());
        user.setName(userCreateDto.getName());
        return user;
    }

}
