package com.matthenry87.restapireactive.store;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class StoreServiceTest {

    @Autowired
    private StoreService storeService;

    @Test
    void getStores() {
    }

    @Test
    void getStore() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setName("name");

        Mono<StoreEntity> createdStore = storeService.createStore(store1)
        .flatMap(x -> storeService.getStore(x.getId()));

        // Act/Assert
        StepVerifier.create(createdStore)
                .expectNextMatches(x -> x.getName().equals(store1.getName()))
                .verifyComplete();
    }

}