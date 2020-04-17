package com.matthenry87.restapireactive.store;

import com.matthenry87.restapireactive.exception.AlreadyExistsException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.matthenry87.restapireactive.store.Status.OPEN;

@Service
class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    Flux<StoreEntity> getStores() {

        return storeRepository.findAll();
    }

    Mono<StoreEntity> getStore(String name) {

        return storeRepository.findByName(name);

//                .orElseThrow(NotFoundException::new);
    }

    Mono<StoreEntity> createStore(StoreEntity store) {

//        storeRepository.findByName(store.getName());
//                .ifPresent(x -> { throw new AlreadyExistsException(); });

//        store.setStatus(OPEN);

        return storeRepository.save(store);
    }



}
