package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public List<UserDto> getUserDtos() {
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getUserDto(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Пользователя с id = " + userId + " не существует."));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto postUserDto(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User savedUser = userRepository.save(user);
        userDto.setId(savedUser.getId());
        return userDto;
    }

    @Override
    @Transactional
    public UserDto patchUserDto(long id, UserDto userDto) {
        UserDto existingUserDto = getUserDto(id);
        userDto.setId(id);
        if (userDto.getName() == null) {
            userDto.setName(existingUserDto.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(existingUserDto.getEmail());
        }
        User user = UserMapper.mapToUser(userDto);
        userRepository.save(user);
        return userDto;
    }

    @Override
    @Transactional
    public void deleteUserDto(long userDtoId) {
        userRepository.deleteById(userDtoId);
    }
}