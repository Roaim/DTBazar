package app.roaim.dtbazar.storeservice.dto;

import app.roaim.dtbazar.storeservice.domain.FoodSell;
import app.roaim.dtbazar.storeservice.domain.StoreFood;
import app.roaim.dtbazar.storeservice.model.Image;
import lombok.Value;

// TODO move to a separate micro service
@Value
public class FoodSellDto {
    String storeFoodId;
    String buyerName;
    String nid;
    Image nidImage;
    double qty;

    public FoodSell toFoodSell(StoreFood storeFood) {
        return new FoodSell(storeFood.getUid(), storeFood.getStoreId(), storeFood.getStoreName(), storeFood.getId(), storeFood.getFood().getId(), storeFood.getFood().getName(), getBuyerName(),
                getNid(), getNidImage(), storeFood.getUnitPrice(), getQty());
    }
}