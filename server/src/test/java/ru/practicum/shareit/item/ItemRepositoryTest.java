package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private Item item;

    @BeforeEach
    void initBase() {
        user = new User();
        user.setName("TestName");
        user.setEmail("test@yandex.ru");
        user = userRepository.save(user);
        item = new Item();
        item.setOwnerId(user.getId());
        item.setName("TestItem");
        item.setDescription("for test");
        item.setAvailable(true);
        item = itemRepository.save(item);
    }

    @Test
    void findByOwnerId_whenItExisting_thenReturnResult() {
        List<Item> result = itemRepository.findByOwnerId(user.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(item));
        assertThat(result.getFirst().getOwnerId(), equalTo(item.getOwnerId()));
        assertThat(result.getFirst().getName(), equalTo(item.getName()));
        assertThat(result.getFirst().getDescription(), equalTo(item.getDescription()));
        assertThat(result.getFirst().getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getFirst().getRequest(), equalTo(item.getRequest()));
        assertThat(result.getFirst().getComments(), equalTo(item.getComments()));
    }

    @Test
    void findByOwnerId_whenItNotExisting_thenReturnEmptyList() {
        Long notExistingId = -100L;
        List<Item> result = itemRepository.findByOwnerId(notExistingId);

        assertThat(result, notNullValue());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByNameContainingIgnoreCaseAndAvailableTrue_whenItExistAndAvailable_thenReturnResult() {
        List<Item> result = itemRepository.findByNameContainingIgnoreCaseAndAvailableTrue(item.getName().toUpperCase());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(item));
    }

    @Test
    void findByNameContainingIgnoreCaseAndAvailableTrue_whenItExistButNotAvailable_thenReturnEmptyList() {
        item.setAvailable(false);
        itemRepository.save(item);

        List<Item> result = itemRepository.findByNameContainingIgnoreCaseAndAvailableTrue(item.getName());

        assertThat(result, notNullValue());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByNameContainingIgnoreCaseAndAvailableTrue_whenItNotExist_thenReturnEmptyList() {
        List<Item> result = itemRepository.findByNameContainingIgnoreCaseAndAvailableTrue("NOT EXISTING ITEM");

        assertThat(result, notNullValue());
        assertTrue(result.isEmpty());
    }
}