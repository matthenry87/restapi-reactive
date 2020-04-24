package com.matthenry87.restapireactive.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final StoreMapper storeMapper;

    public StoreController(StoreService storeService, StoreMapper storeMapper) {
        this.storeService = storeService;
        this.storeMapper = storeMapper;
    }

    @GetMapping
    public Flux<StoreModel> get() {

        return storeService.getStores()
                .map(storeMapper::toModel);
    }

    @GetMapping("/{id}")
    public Mono<StoreModel> getById(@PathVariable String id) {

        return storeService.getStore(id)
                .map(storeMapper::toModel);
    }

    @PostMapping
    public Mono<ResponseEntity<StoreModel>> post(@RequestBody @Validated(CreateStore.class) StoreModel storeModel) {

        StoreEntity storeEntity = storeMapper.toEntity(storeModel);

        return storeService.createStore(storeEntity)
                .map(x -> {

                    storeModel.setId(storeEntity.getId());
                    storeModel.setStatus(storeEntity.getStatus());

                    return new ResponseEntity<>(storeModel, HttpStatus.CREATED);
                });
    }

    @PutMapping("/{id}")
    public Mono<StoreModel> put(@RequestBody @Validated(UpdateStore.class) StoreModel storeModel,
                                @PathVariable String id) {

        storeModel.setId(id);

        StoreEntity storeEntity = storeMapper.toEntity(storeModel);

        return storeService.updateStore(storeEntity)
                .map(x -> storeModel);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {

        return storeService.deleteStore(id)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }

    interface UpdateStore {
    }

    interface CreateStore {
    }

}
