package app.roaim.dtbazar.storeservice.service;

import app.roaim.dtbazar.storeservice.domain.Donation;
import app.roaim.dtbazar.storeservice.domain.FoodDetail;
import app.roaim.dtbazar.storeservice.domain.Store;
import app.roaim.dtbazar.storeservice.domain.StoreFood;
import app.roaim.dtbazar.storeservice.model.AddStock;
import app.roaim.dtbazar.storeservice.repository.StoreFoodRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

// TODO move to a separate micro service
@Service
@AllArgsConstructor
public class IntercomService {
    private final FoodService foodService;
    private final StoreService storeService;
    private final StoreFoodRepository storeFoodRepository;

    public Mono<?> onAddDonation(Donation donation, StoreFood storeFood) {
        storeFood.onAddedDonation(donation.getAmount());
        return storeFoodRepository.save(storeFood)
                .then(
                        foodService.getFoodById(storeFood.getFood().getId()).flatMap(foodDetail -> {
                            foodDetail.onAddedDonation(donation.getAmount());
                            return foodService.saveFoodDetail(foodDetail);
                        })
                ).then(
                        storeService.getStoreById(donation.getStoreId()).flatMap(store -> {
                            store.onAddedDonation(donation.getAmount());
                            return storeService.saveStore(store);
                        })
                );
    }

    public Mono<?> onAddedToStore(Store store, StoreFood storeFood, FoodDetail foodDetail) {
        foodDetail.onAddedToStore(storeFood);
        store.onIncreaseAllFoodPrice(storeFood.getStockQty() * storeFood.getUnitPrice());
        return foodService.saveFoodDetail(foodDetail).then(storeService.saveStore(store));
    }

    public Mono<?> onAddedStock(StoreFood storeFood, AddStock addStock) {
        return foodService.getFoodById(storeFood.getFood().getId()).flatMap(foodDetail -> {
            foodDetail.onAddedStock(addStock.getQuantity(), addStock.getUnitPrice());
            return foodService.saveFoodDetail(foodDetail);
        }).then(storeService.getStoreById(storeFood.getStoreId()).flatMap(store -> {
            store.onIncreaseAllFoodPrice(addStock.getQuantity() * addStock.getUnitPrice());
            return storeService.saveStore(store);
        }));
    }

    public Mono<StoreFood> onSellFood(StoreFood storeFood, double qty) {
        double donationAmount = storeFood.onSell(qty);
        return foodService.getFoodById(storeFood.getFood().getId()).flatMap(foodDetail -> {
            foodDetail.onSpentStock(qty);
            foodDetail.onSpentDonation(donationAmount);
            return foodService.saveFoodDetail(foodDetail);
        }).then(storeService.getStoreById(storeFood.getStoreId())).flatMap(store -> {
            store.onSpentDonation(donationAmount);
            store.onDecreaseAllFoodPrice(qty * storeFood.getUnitPrice());
            return storeService.saveStore(store);
        }).then(storeFoodRepository.save(storeFood));
    }

    public Mono<Store> getStoreById(String storeId) {
        return storeService.getStoreById(storeId);
    }

    public Mono<FoodDetail> getFoodById(String foodId) {
        return foodService.getFoodById(foodId);
    }

    public Mono<StoreFood> getStoreFoodById(String storeFoodId) {
        return storeFoodRepository.findById(storeFoodId).switchIfEmpty(
                error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("StoreFoodId: %s not found", storeFoodId))
                )
        );
    }
}
