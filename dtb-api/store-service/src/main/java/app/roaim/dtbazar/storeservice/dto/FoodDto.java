package app.roaim.dtbazar.storeservice.dto;

import app.roaim.dtbazar.storeservice.domain.Food;
import app.roaim.dtbazar.storeservice.model.Currency;
import app.roaim.dtbazar.storeservice.model.Unit;
import lombok.Value;

// TODO move to a separate micro service
@Value
public class FoodDto {

    String name;
    Unit unit;
    Currency currency;
    Double startingPrice;
    Double endingPrice;
    Double subsidy = .8;

    public Food toFood(String uid) {
        return new Food(uid, getName(), getUnit(), getCurrency(), getStartingPrice(), getEndingPrice(), getSubsidy());
    }

}