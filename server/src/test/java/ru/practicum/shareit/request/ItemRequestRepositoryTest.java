package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private User requestor;
    private ItemRequest requestFirst;
    private ItemRequest requestLast;

    @BeforeEach
    void initBase() {
        requestor = new User();
        requestor.setName("TestName");
        requestor.setEmail("test-requestor@yandex.ru");
        requestor = userRepository.save(requestor);

        requestFirst = new ItemRequest();
        requestFirst.setRequestor(requestor);
        requestFirst.setDescription("earlier request");
        requestFirst = requestRepository.save(requestFirst);

        requestLast = new ItemRequest();
        requestLast.setRequestor(requestor);
        requestLast.setDescription("later request");
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_withExist_returnRightResult() {

        requestLast = requestRepository.save(requestLast);

        List<ItemRequest> result = requestRepository.findAllByRequestorIdOrderByCreatedDesc(requestor.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result.getFirst(), equalTo(requestLast));
    }
}