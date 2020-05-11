package app.roaim.dtbazar.storeservice.dto;

import app.roaim.dtbazar.storeservice.domain.Food;
import app.roaim.dtbazar.storeservice.domain.StoreFood;
import lombok.Value;

// TODO move to a separate micro service
@Value
public class StoreFoodDto {

    double unitPrice;
    String foodId;
    double stockQty;

    public StoreFood toStoreFood(String uid) {
        return toStoreFood(uid, null, null, null);
    }

    public StoreFood toStoreFood(String uid, String storeId, String storeName, Food food) {
        return new StoreFood(uid, storeId, storeName, food, unitPrice, stockQty);
    }
}