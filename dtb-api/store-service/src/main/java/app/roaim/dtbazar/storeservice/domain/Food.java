package app.roaim.dtbazar.storeservice.domain;

import app.roaim.dtbazar.storeservice.model.Currency;
import app.roaim.dtbazar.storeservice.model.Unit;
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
@Document(collection = "food")
public class Food {

    @Id
    @Indexed
    private String id;
    private String name;
    private Unit unit;
    private Currency currency;
    private Double startingPrice;
    private Double endingPrice;
    private Double subsidy = .8;
    @Indexed
    private boolean enabled = true;
    @JsonIgnore
    private String uid;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @PersistenceConstructor
    public Food(String uid, String name, Unit unit, Currency currency, Double startingPrice, Double endingPrice, Double subsidy) {
        super();
        update(uid, name, unit, currency, startingPrice, endingPrice, subsidy);
    }

    public void update(Food food) {
        update(food.getUid(), food.getName(), food.getUnit(), food.getCurrency(), food.getStartingPrice(), food.getEndingPrice(), food.getSubsidy());
    }

    public void update(String uid, String name, Unit unit, Currency currency, Double startingPrice, Double endingPrice, Double subsidy) {
        this.uid = uid;
        this.name = name;
        this.unit = unit;
        this.currency = currency;
        this.startingPrice = startingPrice;
        this.endingPrice = endingPrice;
        if (subsidy != null) this.subsidy = subsidy;
    }
}