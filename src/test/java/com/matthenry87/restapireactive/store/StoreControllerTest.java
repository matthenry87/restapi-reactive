package com.matthenry87.restapireactive.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StoreControllerTest {

    @Mock
    private StoreService storeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebTestClient webClient;

    @BeforeEach
    void init() {

        MockitoAnnotations.initMocks(this);

        StoreMapper storeMapper = new StoreMapperImpl();

        StoreController storeController = new StoreController(storeService, storeMapper);

        webClient = WebTestClient.bindToController(storeController).build();
    }

    @Test
    void get_works() {
        // Arrange
        when(storeService.getStores()).thenReturn(Flux.just(new StoreEntity(), new StoreEntity()));

        // Act/Assert
        webClient.get()
                .uri("/store")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    void getById_works() {
        // Arrange
        StoreEntity storeEntity = createStoreEntity();

        when(storeService.getStore("id")).thenReturn(Mono.just(storeEntity));

        // Act/Assert
        webClient.get().uri("/store/id")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"address\":\"123 High St\",\"name\":\"Store Name\"," +
                "\"phone\":\"3039993456\",\"status\":\"OPEN\"}");
    }

    @Test
    void post_works() throws JsonProcessingException {
        // Arrange
        StoreModel storeModel = createStoreModel();

        String json = objectMapper.writeValueAsString(storeModel);

        StoreEntity storeEntity = createStoreEntity();

        when(storeService.createStore(any(StoreEntity.class))).thenReturn(Mono.just(storeEntity));

        // Act/Assert
        webClient.post().uri("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().json("{\"address\":\"123 High St\",\"name\":\"Store Name\"," +
                "\"phone\":\"3039993456\",\"status\":\"OPEN\"}");
    }

    @Test
    void put_works() throws JsonProcessingException {
        // Arrange
        StoreModel storeModel = createStoreModel();

        String json = objectMapper.writeValueAsString(storeModel);

        StoreEntity storeEntity = createStoreEntity();

        when(storeService.updateStore(any(StoreEntity.class))).thenReturn(Mono.just(storeEntity));

        // Act/Assert
        webClient.put().uri("/store/id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("{\"address\":\"123 High St\",\"name\":\"Store Name\"," +
                "\"phone\":\"3039993456\",\"status\":\"OPEN\"}");
    }

    @Test
    void delete_works() {
        // Arrange
         when(storeService.deleteStore("id")).thenReturn(Mono.empty());

        // Act/Assert
        webClient.delete().uri("/store/id")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    private StoreEntity createStoreEntity() {

        StoreEntity storeEntity = new StoreEntity();

        storeEntity.setName("Store Name");
        storeEntity.setAddress("123 High St");
        storeEntity.setPhone("3039993456");
        storeEntity.setStatus(Status.OPEN);

        return storeEntity;
    }

    private StoreModel createStoreModel() {

        StoreModel storeModel = new StoreModel();

        storeModel.setName("Store Name");
        storeModel.setAddress("123 High St");
        storeModel.setPhone("3039993456");
        storeModel.setStatus(Status.OPEN);

        return storeModel;
    }

}