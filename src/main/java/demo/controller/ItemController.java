package demo.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import demo.exception.InvalidItemException;
import demo.exception.ItemNotFoundException;
import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.rest.api.GetItemsResponse;
import demo.rest.api.UpdateItemRequest;
import demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/items")
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<String> createItem(@RequestBody CreateItemRequest request) {
        log.info("Received request to create item with name: " + request.getName());
        try {
            UUID itemId = itemService.createItem(request);
            return ResponseEntity.created(URI.create(itemId.toString())).build();
        } catch(InvalidItemException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch(Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<String> updateItem(@PathVariable UUID itemId, @RequestBody UpdateItemRequest request) {
        log.info("Received request to update item with id: {} - name: {}", itemId, request.getName());
        try {
            itemService.updateItem(itemId, request);
            return ResponseEntity.noContent().build();
        } catch(ItemNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch(InvalidItemException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch(Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<GetItemResponse> getItem(@PathVariable UUID itemId) {
        log.info("Looking up item with id: " + itemId);
        try {
            GetItemResponse response = itemService.getItem(itemId);
            return ResponseEntity.ok(response);
        } catch(ItemNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    public ResponseEntity<GetItemsResponse> getItems() {
        log.info("Retrieving items");
        GetItemsResponse response = itemService.getItems();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable UUID itemId) {
        log.info("Deleting item with id: {}", itemId);
        try {
            itemService.deleteItem(itemId);
            return ResponseEntity.noContent().build();
        } catch(ItemNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
