package app.roaim.dtbazar.storeservice.dto;

import app.roaim.dtbazar.storeservice.domain.Store;
import app.roaim.dtbazar.storeservice.model.Image;
import lombok.Value;

@Value
public class StoreDto {
    String name;
    String mobile;
    Image storeFrontImage;
    double[] location;

    public Store toStore(String uid, String proprietor) {
        return new Store(name, proprietor, mobile, storeFrontImage, uid, location);
    }
}