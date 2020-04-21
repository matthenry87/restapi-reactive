package com.matthenry87.restapireactive.store;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class StoreModel {

    private String id;

    @NotEmpty(groups = {StoreController.UpdateStore.class, StoreController.CreateStore.class})
    private String name;

    @NotEmpty(groups = {StoreController.UpdateStore.class, StoreController.CreateStore.class})
    private String address;

    @NotEmpty(groups = {StoreController.UpdateStore.class, StoreController.CreateStore.class})
    private String phone;

    @NotNull(groups = StoreController.UpdateStore.class)
    private Status status;

}
