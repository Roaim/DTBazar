package app.roaim.dtbazar.storeservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import app.roaim.dtbazar.storeservice.model.Image;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// TODO move to a separate micro service
@Data
@Document(collection = "food_sell")
public class FoodSell {

    @Id
    private String id;
    @Indexed
    private String storeId;
    private String storeName;
    @Indexed
    private String storeFoodId;
    @Indexed
    private String foodId;
    private String foodName;
    private String buyerName;
    private String nid;
    private Image nidImage;
    private double unitPrice;
    private double qty;
    @JsonIgnore
    private String uid;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @PersistenceConstructor
    public FoodSell(String uid, String storeId, String storeName, String storeFoodId, String foodId, String foodName, String buyerName, String nid, Image nidImage, double unitPrice, double qty) {
        super();
        update(uid, storeId, storeName, storeFoodId, foodId, foodName, buyerName, nid, nidImage, unitPrice, qty);
    }

    public void update(FoodSell foodSell) {
        update(foodSell.getUid(), foodSell.getStoreId(), foodSell.getStoreName(), foodSell.getStoreFoodId(), foodSell.getFoodId(), foodSell.getFoodName(), foodSell.getBuyerName(), foodSell.getNid(), foodSell.getNidImage(), foodSell.getUnitPrice(), foodSell.getQty());
    }

    public void update(String uid, String storeId, String storeName, String storeFoodId, String foodId, String foodName, String buyerName, String nid, Image nidImage, double unitPrice, double qty) {
        this.uid = uid;
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeFoodId = storeFoodId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.buyerName = buyerName;
        this.nid = nid;
        this.nidImage = nidImage;
        this.unitPrice = unitPrice;
        this.qty = qty;
    }
}