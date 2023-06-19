/*
package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
public class ItemRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void findAllByOwnerId() {
        List<Item> allUser2Items = itemRepository.findAllByOwner_Id(4L);
        assertEquals(allUser2Items.size(), 3,
                "Некорректное количество предметов в БД у пользователя с id = 4");
        assertEquals(allUser2Items, List.of(
                itemRepository.getReferenceById(2L),
                itemRepository.getReferenceById(3L),
                itemRepository.getReferenceById(5L)));
    }

    @Test
    void searchListOfAvailableItems() {
        TypedQuery<Item> query = em.getEntityManager()
                .createQuery("SELECT i FROM Item AS i WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
                                "OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%')) AND i.available = true",
                        Item.class);

        Item item3 = itemRepository.getReferenceById(3L);
        List<Item> foundItems = query.setParameter("text", "момен").getResultList();
        assertThat(foundItems, equalTo(List.of(item3)));
    }

    @Test
    void findAllByRequestId() {
        List<Item> itemsFoundByRequestId = itemRepository.findAllByRequestId(1L);

        Item item5 = itemRepository.getReferenceById(5L);

        assertThat(itemsFoundByRequestId, equalTo(List.of(item5)));
    }
}*/
