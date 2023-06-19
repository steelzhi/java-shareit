/*
package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SqlGroup({
        @Sql(scripts = "classpath:schema.sql",
                config = @SqlConfig(encoding = "UTF-8"),
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:create_test_data.sql",
                config = @SqlConfig(encoding = "UTF-8"),
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    void findAllByItem_Id() {
        List<Comment> foundComments = commentRepository.findAllByItem_Id(2L);

        assertThat(foundComments, equalTo(List.of(
                commentRepository.getReferenceById(1L),
                commentRepository.getReferenceById(2L))));
    }
}*/
