package app.roaim.dtbazar.storeservice.dto;

import app.roaim.dtbazar.storeservice.domain.Donation;
import app.roaim.dtbazar.storeservice.domain.StoreFood;
import app.roaim.dtbazar.storeservice.model.Currency;
import lombok.Value;

// TODO move to a separate micro service
@Value
public class DonationDto {

    String storeFoodId;
    double amount;
    Currency currency;

    public Donation toDonation(String donorId, String donorName, StoreFood storeFood) {
        return new Donation(storeFood.getStoreId(), storeFood.getStoreName(), donorId, donorName, storeFood.getId(),
                storeFood.getFood().getName(), getAmount(), getCurrency());
    }
}