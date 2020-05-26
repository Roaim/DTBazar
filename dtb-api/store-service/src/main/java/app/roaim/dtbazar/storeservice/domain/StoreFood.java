package app.roaim.dtbazar.storeservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// TODO move to a separate micro service
@Data
@Document(collection = "store_food")
//@CompoundIndex(name = "store_food_idx", def = "{'storeId': 1, 'food_id': 1}")
public class StoreFood {

    @Id
    private String id;
    @Indexed
    private String storeId;
    private String storeName;
    private Food food;
    private double unitPrice;
    private double stockQty;
    private double totalDonation;
    private double spentDonation;
    @JsonIgnore
    private String uid;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Sells a certain quantity of food.
     *
     * @param quantity the quantity to be sold
     * @return the donation amount
     */
    public synchronized double onSell(double quantity) {
        double costPrice = quantity * getUnitPrice();
        double donation = costPrice * getFood().getSubsidy();
        onSpentStock(quantity);
        onSpentDonation(donation);
        return donation;
    }

    public synchronized void onAddedStock(double quantity, double unitPrice) {
        double totalPrice = (getUnitPrice() * getStockQty()) + (quantity * unitPrice);
        setUnitPrice(totalPrice / (getStockQty() + quantity));
        setStockQty(getStockQty() + quantity);
    }

    public synchronized void onAddedDonation(double amount) {
        setTotalDonation(getTotalDonation() + amount);
    }

    private synchronized void onSpentStock(double quantity) {
        setStockQty(getStockQty() - quantity);
    }

    private synchronized void onSpentDonation(double amount) {
        setSpentDonation(getSpentDonation() + amount);
    }

    @PersistenceConstructor
    public StoreFood(String uid, String storeId, String storeName, Food food, double unitPrice, double stockQty) {
        super();
        update(uid, storeId, storeName, food, unitPrice, stockQty);
    }

    public void update(StoreFood sFood) {
        String storeId = sFood.getStoreId();
        if (storeId == null) {
            storeId = getStoreId();
        }
        String storeName = sFood.getStoreName();
        if (storeName == null) {
            storeName = getStoreName();
        }
        String uid = sFood.getUid();
        if (uid == null) {
            uid = getUid();
        }
        Food food = sFood.getFood();
        if (food == null) {
            food = getFood();
        }
        update(uid, storeId, storeName, food, sFood.getUnitPrice(), sFood.getStockQty());
    }

    public void update(String uid, String storeId, String storeName, Food food, double cost, double stockQty) {
        this.uid = uid;
        this.storeId = storeId;
        this.storeName = storeName;
        this.food = food;
        this.unitPrice = cost;
        this.stockQty = stockQty;
    }
}