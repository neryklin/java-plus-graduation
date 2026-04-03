package practicum.user.service;

import practicum.interaction.dto.UserCreateDto;
import practicum.interaction.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    UserRequestDto create(UserCreateDto userCreateDto);

    List<UserRequestDto> get(List<Long> ids, int from, int size);

    void delete(Long userId);
}
