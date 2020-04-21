package com.matthenry87.restapireactive.store;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    StoreEntity toEntity(StoreModel store);

    StoreModel toModel(StoreEntity storeEntity);

}
