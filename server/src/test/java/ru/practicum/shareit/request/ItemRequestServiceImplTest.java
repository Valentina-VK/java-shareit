package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.InstantMapper;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final ItemRequestService requestService;
    private ItemRequestDto existingRequest;
    private ItemRequestDto createdRequestDto;
    private UserDto requestor;

    @BeforeEach
    void testInitialization() {
        requestor = new UserDto();
        requestor.setId(11L);
        requestor.setName("user1");
        requestor.setEmail("user11@yandex.ru");

        existingRequest = new ItemRequestDto();
        existingRequest.setId(21L);
        existingRequest.setRequestor(requestor);
        existingRequest.setDescription("description1");
        existingRequest.setCreated(InstantMapper.mapStringToInstant("2025-04-25T10:10:10"));

        createdRequestDto = new ItemRequestDto();
        createdRequestDto.setId(1L);
        createdRequestDto.setDescription("new item request");
    }

    @Test
    void createItemRequest_test() {
        NewItemRequestDto newRequestDto = new NewItemRequestDto();
        newRequestDto.setDescription("new item request");

        ItemRequestDto result = requestService.createItemRequest(requestor.getId(), newRequestDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(createdRequestDto.getId()));
        assertThat(result.getDescription(), equalTo(createdRequestDto.getDescription()));
        assertThat(result.getRequestor().getName(), equalTo(requestor.getName()));
    }

    @Test
    void getAllByUser_test() {

        List<ItemRequestDto> result = requestService.getAllByUser(requestor.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(existingRequest));
        assertThat(result.getFirst().getRequestor(), equalTo(requestor));
    }

    @Test
    void getAll_test() {

        List<ItemRequestDto> result = requestService.getAll();

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result.getFirst(), equalTo(existingRequest));
        assertThat(result.getFirst().getRequestor(), equalTo(requestor));
    }

    @Test
    void getById_test() {
        ItemRequestDto result = requestService.getById(existingRequest.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(existingRequest.getId()));
        assertThat(result.getCreated(), equalTo(existingRequest.getCreated()));
        assertThat(result.getDescription(), equalTo(existingRequest.getDescription()));
        assertThat(result.getItems(), equalTo(List.of()));
        assertThat(result.getRequestor().getName(), equalTo(requestor.getName()));
    }
}