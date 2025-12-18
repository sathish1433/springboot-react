package demo.integration;

import java.util.Map;

import demo.DemoConfiguration;
import demo.repository.ItemRepository;
import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.rest.api.GetItemsResponse;
import demo.rest.api.UpdateItemRequest;
import demo.util.TestRestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { DemoConfiguration.class } )
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class EndToEndIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
    }

    /**
     * Hit the REST endpoints to create, retrieve, update and delete an item.
     */
    @Test
    public void testItemCRUD() {
        // Create the item.
        CreateItemRequest createItemRequest = TestRestData.buildCreateItemRequest(randomAlphabetic(8).toLowerCase(), randomAlphabetic(3).toLowerCase());
        ResponseEntity<Void> createItemResponse = restTemplate.postForEntity("/v1/items", createItemRequest, Void.class);
        assertThat(createItemResponse.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(createItemResponse.getHeaders().getLocation(), notNullValue());

        String itemId = createItemResponse.getHeaders().getLocation().toString();

        // Retrieve the new item.
        ResponseEntity<GetItemResponse> getItemResponse = restTemplate.getForEntity("/v1/items/"+itemId, GetItemResponse.class);
        assertThat(getItemResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(getItemResponse.getBody().getName(), equalTo(createItemRequest.getName()));

        // Update the item.
        UpdateItemRequest updateItemRequest = TestRestData.buildUpdateItemRequest(randomAlphabetic(8).toLowerCase(), randomAlphabetic(3).toLowerCase());
        ResponseEntity<Void> updateItemResponse = restTemplate.exchange("/v1/items/{id}", HttpMethod.PUT, new HttpEntity<>(updateItemRequest), Void.class, Map.of("id", itemId));
        assertThat(updateItemResponse.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

        // Retrieve the updated item.
        ResponseEntity<GetItemResponse> getItemResponseUpdated = restTemplate.getForEntity("/v1/items/"+itemId, GetItemResponse.class);
        assertThat(getItemResponseUpdated.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(getItemResponseUpdated.getBody().getName(), equalTo(updateItemRequest.getName()));

        // Delete the item
        ResponseEntity<Void> deleteItemResponse = restTemplate.exchange("/v1/items/{id}", HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Void.class, Map.of("id", itemId));
        assertThat(deleteItemResponse.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

        // Retrieve the deleted item - should return NOT FOUND.
        ResponseEntity<GetItemResponse> getItemResponseDeleted = restTemplate.getForEntity("/v1/items/"+itemId, GetItemResponse.class);
        assertThat(getItemResponseDeleted.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    /**
     * A 404 NOT FOUND is returned if the item being requested does not exist.
     */
    @Test
    public void testGetItem_NotFound() {
        ResponseEntity<GetItemResponse> getItemResponse = restTemplate.getForEntity("/v1/items/"+randomUUID(), GetItemResponse.class);
        assertThat(getItemResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }


    /**
     * Hit the REST endpoint to create multiple items and retrieve all.
     */
    @Test
    public void testCreateAndGetMultipleItems() {
        // Create the items.
        int totalItems = 10;
        for (int i=0; i<totalItems; i++) {
            CreateItemRequest createItemRequest = TestRestData.buildCreateItemRequest(randomAlphabetic(8).toLowerCase(), randomAlphabetic(3).toLowerCase());
            ResponseEntity<Void> createItemResponse = restTemplate.postForEntity("/v1/items", createItemRequest, Void.class);
            assertThat(createItemResponse.getStatusCode(), equalTo(HttpStatus.CREATED));
        }

        // Retrieve the items.
        ResponseEntity<GetItemsResponse> getItemsResponse = restTemplate.getForEntity("/v1/items", GetItemsResponse.class);
        assertThat(getItemsResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(getItemsResponse.getBody().getItemResponses().size(), equalTo(totalItems));
    }
}
