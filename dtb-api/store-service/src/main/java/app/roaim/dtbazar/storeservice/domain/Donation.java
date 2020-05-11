package app.roaim.dtbazar.storeservice.domain;

import app.roaim.dtbazar.storeservice.model.Currency;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
// TODO move to a separate micro service
@Data
@Document(collection = "donation")
public class Donation {

    @Id
    private String id;
    @Indexed
    private String storeId;
    private String storeName;
    @Indexed
    private String donorId;
    private String donorName;
    @Indexed
    private String storeFoodId;
    private String foodName;
    private double amount;
    private Currency currency = Currency.BDT;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @PersistenceConstructor
    public Donation(String storeId, String storeName, String donorId, String donorName, String storeFoodId, String foodName, double amount, Currency currency) {
        super();
        update(storeId, storeName, donorId, donorName, storeFoodId, foodName, amount, currency);
    }

    public void update(Donation donation) {
        update(donation.getStoreId(), donation.getStoreName(), donation.getDonorId(), donation.getDonorName(),
                donation.getStoreFoodId(), donation.getFoodName(), donation.getAmount(), donation.getCurrency());
    }

    public void update(String storeId, String storeName, String giverId, String giverName, String foodId, String foodName, double amount, Currency currency) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.donorId = giverId;
        this.donorName = giverName;
        this.storeFoodId = foodId;
        this.foodName = foodName;
        this.amount = amount;
        this.currency = currency;
    }
}