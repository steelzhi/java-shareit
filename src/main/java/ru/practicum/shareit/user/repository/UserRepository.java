package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    //List<User> getUsers();

    //User getUser(Long userId);

    //User postUser(User user);

    @Transactional
    @Modifying
    @Query("UPDATE User AS u " +
            "SET u.name = ?2, " +
            "u.email = ?3 " +
            "WHERE u.id = ?1"/* +
            "SELECT u " +
            "FROM User AS u " +
            "WHERE u.id = :userId;"*/)
    int patchUser(Long userId, String name, String email);

    //void deleteUser(Long userId);

    @Query("DELETE FROM User")
    void deleteAllUsers(); // этот метод нужно также заменить в тестах на deleteAllInBatch()
}