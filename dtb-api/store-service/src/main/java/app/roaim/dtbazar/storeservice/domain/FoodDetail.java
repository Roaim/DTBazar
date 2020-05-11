package app.roaim.dtbazar.storeservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

// TODO move to a separate micro service
@Data
@Document(collection = "food_detail")
public class FoodDetail {

    @Id
    @JsonIgnore
    private String id;
    private Food food;
    private double totalDonation;
    private double spentDonation;
    private int storeCount;
    private double avgUnitPrice;
    private double stockQty;

    public synchronized void onAddedToStore(StoreFood storeFood) {
        double totalCost = getAvgUnitPrice() * getStoreCount();
        totalCost += storeFood.getUnitPrice();
        setStoreCount(getStoreCount() + 1);
        setAvgUnitPrice(totalCost / getStoreCount());
        setStockQty(getStockQty() + storeFood.getStockQty());
    }

    public synchronized void onSpentStock(double quantity) {
        setStockQty(getStockQty() - quantity);
    }

    public synchronized void onAddedStock(double quantity, double unitPrice) {
        double totalPrice = (getStockQty() * getAvgUnitPrice()) + (quantity * unitPrice);
        setAvgUnitPrice(totalPrice / (getStockQty() + quantity));
        setStockQty(getStockQty() + quantity);
    }

    public synchronized void onSpentDonation(double amount) {
        setSpentDonation(getSpentDonation() + amount);
    }

    public synchronized void onAddedDonation(double amount) {
        setTotalDonation(getTotalDonation() + amount);
    }

    @PersistenceConstructor
    public FoodDetail(Food food) {
        super();
        update(food);
    }

    public void update(FoodDetail foodDetail) {
        update(foodDetail.getFood());
    }

    public void update(Food food) {
        this.food = food;
    }
}