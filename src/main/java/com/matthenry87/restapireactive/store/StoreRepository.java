package com.matthenry87.restapireactive.store;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StoreRepository extends ReactiveMongoRepository<StoreEntity, String> {

    Mono<StoreEntity> findByName(String name);

    Mono<StoreEntity> findByNameAndIdNot(String name, String id);

}
