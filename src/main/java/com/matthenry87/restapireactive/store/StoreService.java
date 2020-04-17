package com.matthenry87.restapireactive.store;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    Flux<StoreEntity> getStores() {

        return storeRepository.findAll();
    }

    public Mono<StoreEntity> getStore(String name) {

        return storeRepository.findByName(name);

//                .orElseThrow(NotFoundException::new);
    }



}
