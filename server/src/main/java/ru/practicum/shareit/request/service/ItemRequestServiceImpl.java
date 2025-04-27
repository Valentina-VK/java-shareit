package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validateService.ValidateService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ValidateService validateService;
    private final ItemRequestMapper mapper;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newDto) {
        User user = validateService.checkUser(userId);
        ItemRequest itemRequest = mapper.toEntity(newDto);
        itemRequest.setRequestor(user);
        return mapper.toDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Long> ids = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> items = itemRepository.findAllByRequestIdIn(ids);
        Map<Long, List<Item>> itemsByRequest = new HashMap<>();
        for (Item item : items) {
            if (!itemsByRequest.containsKey(item.getRequest().getId())) {
                itemsByRequest.put(item.getRequest().getId(), new ArrayList<>());
            }
            itemsByRequest.get(item.getRequest().getId()).add(item);
        }
        return requests.stream()
                .map(request ->
                        mapper.toDto(request, itemsByRequest.get(request.getId())))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return mapper.toDto(requestRepository.findAll(Sort.by(Sort.Direction.DESC, "created")));
    }

    @Override
    public ItemRequestDto getById(Long requestId) {
        ItemRequest itemRequest = validateService.checkRequest(requestId);
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return mapper.toDto(itemRequest, items);
    }
}