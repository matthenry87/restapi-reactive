package com.matthenry87.restapireactive.store;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

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
        Mono<StoreEntity> store = storeService.getStore("1");

//      StepVerifier.create(store)

    }
}