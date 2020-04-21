package com.matthenry87.restapireactive.store;

import com.matthenry87.restapireactive.exception.AlreadyExistsException;
import com.matthenry87.restapireactive.exception.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
class StoreService {

    private final StoreRepository storeRepository;

    StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    Flux<StoreEntity> getStores() {

        return storeRepository.findAll();
    }

    Mono<StoreEntity> getStore(String id) {

        return storeRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException()));
    }

    Mono<StoreEntity> createStore(StoreEntity store) {

        if (store.getStatus() == null) {

            store.setStatus(Status.OPEN);
        }

        return storeRepository.findByName(store.getName())
                .map(x -> {
                    throw new AlreadyExistsException();
                })
                .then(storeRepository.save(store));
    }

    Mono<StoreEntity> updateStore(StoreEntity storeEntity) {

        String id = storeEntity.getId();

        return storeRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .then(storeRepository.findByNameAndIdNot(storeEntity.getName(), id))
                .map(x -> {
                    throw new AlreadyExistsException("another store with name already exists");
                })
                .then(storeRepository.save(storeEntity));
    }

    Mono<Void> deleteStore(String id) {

        return storeRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(x -> storeRepository.deleteById(id));
    }

}
