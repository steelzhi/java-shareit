package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.request.model.ItemRequest;

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
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    void findAllByRequesterId() {
        List<ItemRequest> requestsByUserId2 = itemRequestRepository.findAllByRequester_Id(4L);

        assertThat(requestsByUserId2, equalTo(List.of(
                itemRequestRepository.getReferenceById(2L),
                itemRequestRepository.getReferenceById(3L))));
    }

    @Test
    void findAllByRequesterIdNot() {
        List<ItemRequest> allRequestsExceptRequestOfUserId4 = itemRequestRepository.findAllByRequester_IdNot(4L);

        assertThat(allRequestsExceptRequestOfUserId4, equalTo(List.of(
                itemRequestRepository.getReferenceById(1L),
                itemRequestRepository.getReferenceById(4L))));
    }
}