package practicum.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import practicum.interaction.dto.UserCreateDto;
import practicum.interaction.dto.UserRequestDto;
import practicum.interaction.exception.UserNotFoundException;
import practicum.user.mapper.UserMapper;
import practicum.user.model.User;
import practicum.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserRequestDto create(UserCreateDto userCreateDto) {
        log.info("User created: {}", userCreateDto);
        return UserMapper.toUserRequestDto(userRepository.save(UserMapper.toUser(userCreateDto)));
    }

    @Override
    public void delete(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        log.info("User with id={}, was deleted", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserRequestDto> get(List<Long> ids, int from, int size) {
        log.info("User get: {}", ids);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("Id"));
        Page<User> usersPage = (ids == null || ids.isEmpty()) ?
                userRepository.findAll(pageable) :
                userRepository.findAllByIdIn(ids, pageable);

        return usersPage.stream()
                .map(UserMapper::toUserRequestDto)
                .collect(Collectors.toList());
    }

}
