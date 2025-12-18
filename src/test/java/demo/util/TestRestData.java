package demo.util;

import java.util.Arrays;
import java.util.UUID;

import demo.rest.api.CreateItemRequest;
import demo.rest.api.GetItemResponse;
import demo.rest.api.GetItemsResponse;
import demo.rest.api.UpdateItemRequest;

import static java.util.UUID.randomUUID;

public class TestRestData {

    public static CreateItemRequest buildCreateItemRequest(String name, String colour) {
        return CreateItemRequest.builder()
                .name(name)
                .colour(colour)
                .build();
    }

    public static UpdateItemRequest buildUpdateItemRequest(String name, String colour) {
        return UpdateItemRequest.builder()
                .name(name)
                .colour(colour)
                .build();
    }

    public static GetItemResponse buildGetItemResponse(UUID id, String name, String colour) {
        return GetItemResponse.builder()
                .id(id)
                .name(name)
                .colour(colour)
                .build();
    }

    public static GetItemsResponse buildGetItemsResponse() {
        return GetItemsResponse.builder()
                .itemResponses(Arrays.asList(buildGetItemResponse(randomUUID(), "test-item1", "red"), buildGetItemResponse(randomUUID(), "test-item2", "blue")))
                .build();
    }
}
