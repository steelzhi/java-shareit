package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("DELETE FROM User")
    void deleteAllUsers(); // этот метод нужно также заменить в тестах на deleteAllInBatch()
}