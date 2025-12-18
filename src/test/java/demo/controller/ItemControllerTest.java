package demo.controller;

import java.net.URI;
import java.util.UUID;

import demo.exception.InvalidItemException;
import demo.exception.ItemNotFoundException;
import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.rest.api.GetItemsResponse;
import demo.rest.api.UpdateItemRequest;
import demo.service.ItemService;
import demo.util.TestRestData;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemService serviceMock;
    private ItemController controller;

    @BeforeEach
    public void setUp() {
        serviceMock = mock(ItemService.class);
        controller = new ItemController(serviceMock);
    }

    /**
     * Ensure that the REST request is successfully passed on to the service.
     */
    @Test
    public void testCreateItem_Success() throws Exception {
        UUID itemId = randomUUID();
        CreateItemRequest request = TestRestData.buildCreateItemRequest(randomAlphabetic(8), randomAlphabetic(3));
        when(serviceMock.createItem(request)).thenReturn(itemId);
        ResponseEntity response = controller.createItem(request);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getHeaders().getLocation(), equalTo(URI.create(itemId.toString())));
        verify(serviceMock, times(1)).createItem(request);
    }

    @Test
    public void testCreateItem_InvalidItem() {
        CreateItemRequest request = TestRestData.buildCreateItemRequest(randomAlphabetic(8), randomAlphabetic(3));
        doThrow(new InvalidItemException("Invalid item")).when(serviceMock).createItem(request);
        ResponseEntity response = controller.createItem(request);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        verify(serviceMock, times(1)).createItem(request);
    }

    /**
     * If an exception is thrown, an error is logged but the processing completes successfully.
     *
     * This ensures the consumer offsets are updated so that the message is not redelivered.
     */
    @Test
    public void testCreateItem_ServiceThrowsException() {
        CreateItemRequest request = TestRestData.buildCreateItemRequest(randomAlphabetic(8), randomAlphabetic(3));
        doThrow(new RuntimeException("Service failure")).when(serviceMock).createItem(request);
        ResponseEntity response = controller.createItem(request);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        verify(serviceMock, times(1)).createItem(request);
    }

    @Test
    public void testUpdateItem_Success() {
        UUID itemId = randomUUID();
        UpdateItemRequest request = TestRestData.buildUpdateItemRequest(RandomStringUtils.randomAlphabetic(8), randomAlphabetic(3));
        ResponseEntity response = controller.updateItem(itemId, request);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
        verify(serviceMock, times(1)).updateItem(itemId, request);
    }

    @Test
    public void testUpdateItem_NotFound() {
        UUID itemId = randomUUID();
        UpdateItemRequest request = TestRestData.buildUpdateItemRequest(RandomStringUtils.randomAlphabetic(8), randomAlphabetic(3));
        doThrow(new ItemNotFoundException()).when(serviceMock).updateItem(itemId, request);
        ResponseEntity response = controller.updateItem(itemId, request);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        verify(serviceMock, times(1)).updateItem(itemId, request);
    }

    @Test
    public void testUpdateItem_InvalidItem() {
        UUID itemId = randomUUID();
        UpdateItemRequest request = TestRestData.buildUpdateItemRequest(RandomStringUtils.randomAlphabetic(8), randomAlphabetic(3));
        doThrow(new InvalidItemException("Invalid item")).when(serviceMock).updateItem(itemId, request);
        ResponseEntity response = controller.updateItem(itemId, request);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        verify(serviceMock, times(1)).updateItem(itemId, request);
    }

    @Test
    public void testUpdateItem_ServiceThrowsException() {
        UUID itemId = randomUUID();
        UpdateItemRequest request = TestRestData.buildUpdateItemRequest(randomAlphabetic(8), randomAlphabetic(3));
        doThrow(new RuntimeException("Service failure")).when(serviceMock).updateItem(itemId, request);
        ResponseEntity response = controller.updateItem(itemId, request);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        verify(serviceMock, times(1)).updateItem(itemId, request);
    }

    @Test
    public void testGetItem_Success() {
        UUID itemId = randomUUID();
        GetItemResponse getItemResponse = TestRestData.buildGetItemResponse(itemId, "test-item", "red");
        when(serviceMock.getItem(itemId)).thenReturn(getItemResponse);
        ResponseEntity<GetItemResponse> response = controller.getItem(itemId);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody().getId(), equalTo(itemId));
        assertThat(response.getBody().getName(), equalTo("test-item"));
        assertThat(response.getBody().getColour(), equalTo("red"));
        verify(serviceMock, times(1)).getItem(itemId);
    }

    @Test
    public void testGetItem_NotFound() {
        UUID itemId = randomUUID();
        when(serviceMock.getItem(itemId)).thenThrow(new ItemNotFoundException());
        ResponseEntity<GetItemResponse> response = controller.getItem(itemId);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        verify(serviceMock, times(1)).getItem(itemId);
    }

    @Test
    public void testGetItems() {
        GetItemsResponse getItemsResponse = TestRestData.buildGetItemsResponse();
        when(serviceMock.getItems()).thenReturn(getItemsResponse);
        ResponseEntity<GetItemsResponse> response = controller.getItems();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody().getItemResponses().size(), equalTo(2));
        assertThat(response.getBody().getItemResponses().get(0).getName(), equalTo("test-item1"));
        assertThat(response.getBody().getItemResponses().get(0).getColour(), equalTo("red"));
        assertThat(response.getBody().getItemResponses().get(1).getName(), equalTo("test-item2"));
        assertThat(response.getBody().getItemResponses().get(1).getColour(), equalTo("blue"));
        verify(serviceMock, times(1)).getItems();
    }

    @Test
    public void testDeleteItem_Success() {
        UUID itemId = randomUUID();
        ResponseEntity response = controller.deleteItem(itemId);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
        verify(serviceMock, times(1)).deleteItem(itemId);
    }

    @Test
    public void testDeleteItem_NotFound() {
        UUID itemId = randomUUID();
        doThrow(new ItemNotFoundException()).when(serviceMock).deleteItem(itemId);
        ResponseEntity response = controller.deleteItem(itemId);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        verify(serviceMock, times(1)).deleteItem(itemId);
    }
}
