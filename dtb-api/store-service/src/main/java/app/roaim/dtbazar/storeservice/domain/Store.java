package app.roaim.dtbazar.storeservice.domain;

import app.roaim.dtbazar.storeservice.model.Image;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Arrays;

@Data
@Document(collection = "store")
public class Store {

    @Id
    private String id;
    private String name;
    private String proprietor;
    private String mobile;
    private String address;
    private Image storeFrontImage;
    private String uid;
    @GeoSpatialIndexed
    private double[] location;
    private double totalDonation;
    private double spentDonation;
    private double allFoodPrice;
    @Indexed
    private boolean enabled = true;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    public synchronized void onIncreaseAllFoodPrice(double amount) {
        setAllFoodPrice(getAllFoodPrice() + amount);
    }

    public synchronized void onDecreaseAllFoodPrice(double amount) {
        setAllFoodPrice(getAllFoodPrice() - amount);
    }

    public synchronized void onSpentDonation(double amount) {
        setSpentDonation(getSpentDonation() + amount);
    }

    public synchronized void onAddedDonation(double amount) {
        setTotalDonation(getTotalDonation() + amount);
    }

    @PersistenceConstructor
    public Store(String name, String proprietor, String mobile, String address, Image storeFrontImage, String uid, double[] location) {
        super();
        update(name, proprietor, mobile, address, storeFrontImage, uid, location);
    }

    public Store(String name, String proprietor, String mobile, String address, Image storeFrontImage, String uid, double x, double y) {
        this(name, proprietor, mobile, address, storeFrontImage, uid, new double[]{x, y});
    }

    public void update(Store store) {
        update(store.getName(), store.getProprietor(), store.getMobile(), store.getAddress(), store.getStoreFrontImage(), store.getUid(), store.getLocation());
    }

    private void update(String name, String proprietor, String mobile, String address, Image storeFrontImage, String uid, double[] location) {
        this.name = name;
        this.proprietor = proprietor;
        this.mobile = mobile;
        this.address = address;
        this.storeFrontImage = storeFrontImage;
        this.uid = uid;
        this.location = location;
    }

    @Override
    public String toString() {
        return "Store [id= " + id + ", name= " + name + ", location= "
                + Arrays.toString(location) + "]";
    }
}