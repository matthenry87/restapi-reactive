package com.matthenry87.restapireactive.store;

import com.matthenry87.restapireactive.exception.AlreadyExistsException;
import com.matthenry87.restapireactive.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class StoreServiceTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreService storeService;

    @Test
    void getStores_works() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setName("name");

        StoreEntity store2 = new StoreEntity();
        store2.setName("name2");

        Flux<StoreEntity> createdStore = storeRepository.deleteAll()
                .then(storeRepository.save(store1))
                .then(storeRepository.save(store2))
                .thenMany(storeService.getStores());

        // Act/Assert
        StepVerifier.create(createdStore)
                .expectNextMatches(x -> x.getName().equals(store1.getName()))
                .expectNextMatches(x -> x.getName().equals(store2.getName()))
                .verifyComplete();
    }

    @Test
    void getStore_works() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setName("name");

        Mono<StoreEntity> createdStore = storeRepository.deleteAll()
                .then(storeRepository.save(store1))
                .flatMap(x -> storeService.getStore(x.getId()));

        // Act/Assert
        StepVerifier.create(createdStore)
                .expectNextMatches(x -> x.getName().equals(store1.getName()))
                .verifyComplete();
    }

    @Test
    void getStore_returnsNotFoundException_whenNotFound() {
        // Arrange
        Mono<StoreEntity> storeNotFound = storeRepository.deleteAll()
                .then(storeService.getStore("foo"));

        // Act/Assert
        StepVerifier.create(storeNotFound)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void createStore_works() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setName("name");

        Mono<StoreEntity> createdStore = storeRepository.deleteAll()
                .then(storeService.createStore(store1))
                .flatMap(x -> storeRepository.findById(x.getId()));

        // Act/Assert
        StepVerifier.create(createdStore)
                .expectNextMatches(x -> x.getId().equals(store1.getId()))
                .verifyComplete();
    }

    @Test
    void createStore_ThrowsAlreadyExistsException_whenAlreadyExists() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setName("name");

        Mono<StoreEntity> createdStore = storeRepository.deleteAll()
                .then(storeService.createStore(store1))
                .then(storeService.createStore(store1));

        // Act/Assert
        StepVerifier.create(createdStore)
                .expectError(AlreadyExistsException.class)
                .verify();
    }

    @Test
    void updateStore_works() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setName("name");

        Mono<StoreEntity> createdStore = storeRepository.deleteAll()
                .then(storeRepository.save(store1))
                .flatMap(x -> {
                    store1.setId(x.getId());
                    return storeService.updateStore(store1);
                });

        // Act/Assert
        StepVerifier.create(createdStore)
                .expectNextMatches(x -> x.getName().equals(store1.getName()))
                .verifyComplete();
    }

    @Test
    void updateStore_ThrowAlreadyExistsException_whenStoreWithNameAlreadyExists() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setName("name");

        StoreEntity store2 = new StoreEntity();
        store2.setName("name2");

        Mono<StoreEntity> createdStore = storeRepository.deleteAll()
                .then(storeRepository.save(store1))
                .then(storeRepository.save(store2))
                .flatMap(x -> {
                    store2.setId(x.getId());
                    store2.setName("name");
                    return storeService.updateStore(store2);
                });

        // Act/Assert
        StepVerifier.create(createdStore)
                .expectError(AlreadyExistsException.class)
                .verify();
    }

    @Test
    void deleteStore_works() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setName("name");

        Mono<Void> voidMono = storeRepository.deleteAll()
                .then(storeRepository.save(store1))
                .flatMap(x -> storeService.deleteStore(x.getId()));

        // Act/Assert
        StepVerifier.create(voidMono)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteStore_ThrowsNotFoundException_whenStoreNotFound() {
        // Arrange
        StoreEntity store1 = new StoreEntity();
        store1.setId("id");

        Mono<Void> voidMono = storeRepository.deleteAll()
                .then(storeService.deleteStore(store1.getId()));

        // Act/Assert
        StepVerifier.create(voidMono)
                .verifyError(NotFoundException.class);
    }

}