package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (Objects.isNull(ids)) {
            log.info("Список user-ов получен");
            return userRepository
                    .findAll(pageable)
                    .map(a -> UserMapper.toUserDto(a))
                    .toList();
        }
        log.info("Список user-ов получен по ids {}", ids);
        return userRepository.findAllById(ids).stream()
                .map(a -> UserMapper.toUserDto(a))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(NewUserRequest request) {
        UserDto userDto = UserMapper.toUserDto(userRepository.save(UserMapper.toUser(request)));
        log.info("Добавлен новый лист");
        return userDto;
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
            log.info("Пользователь с id {} успешно удален", userId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException("Юзер с id " + userId + " не найден");
        }
    }
}