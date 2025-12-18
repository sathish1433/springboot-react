package demo.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import demo.domain.Item;
import demo.exception.InvalidItemException;
import demo.exception.ItemNotFoundException;
import demo.repository.ItemRepository;
import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.rest.api.GetItemsResponse;
import demo.rest.api.UpdateItemRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(@Autowired ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public UUID createItem(CreateItemRequest request) {
        if(request.getName()==null || request.getName().trim().length()==0
            || request.getColour()==null || request.getColour().trim().length()==0) {
            throw new InvalidItemException("Name and colour must be set.");
        }
        Item item = Item.builder()
                .name(request.getName())
                .colour(request.getColour())
                .build();
        item = itemRepository.save(item);
        log.info("Item created with id: " + item.getId());
        return item.getId();
    }

    public void updateItem(UUID itemId, UpdateItemRequest request) {
        if(request.getName()==null || request.getName().trim().length()==0
                || request.getColour()==null || request.getColour().trim().length()==0) {
            throw new InvalidItemException("Name and colour must be set.");
        }
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if(itemOpt.isPresent()) {
            log.info("Found item with id: " + itemId);
            Item item = itemOpt.get();
            item.setName(request.getName());
            item.setColour(request.getColour());
            itemRepository.save(item);
            log.info("Item updated with id: {} - name: {} - colour: {}", itemId, request.getName(), request.getColour());
        } else {
            log.error("Item with id: {} not found.", itemId);
            throw new ItemNotFoundException();
        }
    }

    public GetItemResponse getItem(UUID itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        GetItemResponse getItemResponse;
        if(itemOpt.isPresent()) {
            log.info("Found item with id: " + itemOpt.get().getId());
            getItemResponse = GetItemResponse.builder()
                    .id(itemOpt.get().getId())
                    .name(itemOpt.get().getName())
                    .colour(itemOpt.get().getColour())
                    .build();
        } else {
            log.warn("Item with id: " + itemId + " not found.");
            throw new ItemNotFoundException();
        }
        return getItemResponse;
    }

    public GetItemsResponse getItems() {
        List<Item> items = itemRepository.findAll();
        List<GetItemResponse> itemResponses = items.stream()
                .map(item -> GetItemResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .colour(item.getColour())
                        .build())
                .collect(Collectors.toList());
        return GetItemsResponse.builder().itemResponses(itemResponses).build();
    }

    public void deleteItem(UUID itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if(itemOpt.isPresent()) {
            itemRepository.delete(itemOpt.get());
            log.info("Deleted item with id: {}", itemOpt.get().getId());
        } else {
            log.error("Item with id: {} not found.", itemId);
            throw new ItemNotFoundException();
        }
    }
}
