package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validateService.ValidateService;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ValidateService validateService;
    @Mock
    private ItemRequestMapper mapper;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    private ItemRequest request;
    private ItemRequestDto requestDto;
    private NewItemRequestDto newRequestDto;
    private Item item;
    private User requestor;

    @BeforeEach
    void testInitialization() {
        requestor = new User();
        requestor.setName("TestName");
        requestor.setId(12L);

        request = new ItemRequest();
        request.setId(5L);
        request.setRequestor(requestor);
        request.setDescription("request");
        request.setCreated(Instant.now());

        requestDto = new ItemRequestDto();
        requestDto.setRequestor(requestor);
        requestDto.setDescription(request.getDescription());
        requestDto.setCreated(request.getCreated());

        newRequestDto = new NewItemRequestDto();
        newRequestDto.setDescription(request.getDescription());

        item = new Item();
        item.setId(1L);
        item.setOwnerId(10L);
        item.setAvailable(true);
        item.setRequest(request);
    }

    @Test
    void createItemRequest_test() {
        when(validateService.checkUser(requestor.getId())).thenReturn(requestor);
        when(mapper.toEntity(newRequestDto)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(mapper.toDto(request)).thenReturn(requestDto);

        ItemRequestDto result = requestService.createItemRequest(requestor.getId(), newRequestDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(requestDto.getId()));
        assertThat(result.getRequestor().getName(), equalTo(requestor.getName()));

        verify(validateService, times(1)).checkUser(requestor.getId());
        verify(requestRepository, times(1)).save(request);
        verify(mapper, times(1)).toEntity(newRequestDto);
        verify(mapper, times(1)).toDto(request);
    }

    @Test
    void getAllByUser_test() {
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(requestor.getId())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(List.of(item));
        when(mapper.toDto(request, List.of(item))).thenReturn(requestDto);

        List<ItemRequestDto> result = requestService.getAllByUser(requestor.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst().getRequestor(), equalTo(requestor));

        verify(requestRepository, times(1)).findAllByRequestorIdOrderByCreatedDesc(requestor.getId());
        verify(itemRepository, times(1)).findAllByRequestIdIn(anyList());
        verify(mapper, times(1)).toDto(request, List.of(item));
    }

    @Test
    void getAll_test() {
        when(requestRepository.findAll(Sort.by(Sort.Direction.DESC, "created"))).thenReturn(List.of(request));
        when(mapper.toDto(List.of(request))).thenReturn(List.of(requestDto));

        List<ItemRequestDto> result = requestService.getAll();

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst().getRequestor(), equalTo(requestor));

        verify(requestRepository, times(1)).findAll(Sort.by(Sort.Direction.DESC, "created"));
        verify(mapper, times(1)).toDto(List.of(request));
    }

    @Test
    void getById_test() {
        when(validateService.checkRequest(request.getId())).thenReturn(request);
        when(itemRepository.findAllByRequestId(request.getId())).thenReturn(List.of(item));
        when(mapper.toDto(request, List.of(item))).thenReturn(requestDto);

        ItemRequestDto result = requestService.getById(request.getId());

        assertThat(result, notNullValue());
        assertThat(result.getRequestor().getName(), equalTo(requestor.getName()));

        verify(validateService, times(1)).checkRequest(request.getId());
        verify(itemRepository, times(1)).findAllByRequestId(request.getId());
        verify(mapper, times(1)).toDto(request, List.of(item));
    }
}