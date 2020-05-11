package app.roaim.dtbazar.storeservice.service;

import app.roaim.dtbazar.storeservice.domain.StoreFood;
import app.roaim.dtbazar.storeservice.dto.StoreFoodDto;
import app.roaim.dtbazar.storeservice.model.AddStock;
import app.roaim.dtbazar.storeservice.repository.StoreFoodRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static app.roaim.dtbazar.storeservice.ThrowableUtil.denyDelete;
import static app.roaim.dtbazar.storeservice.ThrowableUtil.denyUpdate;
import static reactor.core.publisher.Mono.error;

// TODO move to a separate micro service
@Service
@AllArgsConstructor
public class StoreFoodService {
    private final StoreFoodRepository repository;
    private final IntercomService intercomService;

    public Mono<StoreFood> save(String uid, String storeId, StoreFoodDto storeFoodDto) {
        return intercomService.getStoreById(storeId).flatMap(store -> {
            if (store.getUid().equals(uid)) {
                return intercomService.getFoodById(storeFoodDto.getFoodId())
                        .flatMap(foodDetail -> {
                            StoreFood storeFood = storeFoodDto.toStoreFood(uid, storeId, store.getName(), foodDetail.getFood());
                            return intercomService.onAddedToStore(store, storeFood, foodDetail).then(save(storeFood));
                        });
            } else {
                return error(denyUpdate("store's food"));
            }
        });
    }

    public Mono<StoreFood> save(StoreFood storeFood) {
        return repository.save(storeFood);
    }

    public Flux<StoreFood> getStoreFoods(String storeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByStoreId(storeId, pageable);
    }

    public Flux<StoreFood> getStoreFoodsByFoodId(String foodId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAllByFood_Id(foodId, pageable);
    }

    public Mono<StoreFood> getByStoreAndFoodId(String storeId, String foodId) {
        return repository.findFirstByStoreIdAndFood_Id(storeId, foodId)
                .switchIfEmpty(
                        error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                String.format("StoreId: %s or FoodId: %s not found", storeId, foodId)))
                );
    }

    public Mono<StoreFood> getStoreFoodById(String storeFoodId) {
        return repository.findById(storeFoodId)
                .switchIfEmpty(
                        error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                String.format("StoreFoodId: %s not found", storeFoodId)))
                );
    }

    public Mono<StoreFood> updateByStoreFoodId(String storeFoodId, StoreFood storeFood) {
        return getStoreFoodById(storeFoodId).flatMap(sf -> {
            if (sf.getUid().equals(storeFood.getUid())) {
                sf.update(storeFood);
                return repository.save(sf);
            } else {
                return error(denyUpdate("store's food"));
            }
        });
    }

    public Mono<StoreFood> deleteStoreFoodById(String uid, String storeFoodId) {
        return getStoreFoodById(storeFoodId).flatMap(storeFood -> {
            if (storeFood.getUid().equals(uid)) {
                return repository.deleteById(storeFoodId).thenReturn(storeFood);
            } else {
                return error(denyDelete("store's food"));
            }
        });
    }

    public Mono<StoreFood> addStock(String uid, String storeFoodId, AddStock addStock) {
        return getStoreFoodById(storeFoodId).flatMap(storeFood -> {
            if (storeFood.getUid().equals(uid)) {
                storeFood.onAddedStock(addStock.getQuantity(), addStock.getUnitPrice());
                return intercomService.onAddedStock(storeFood, addStock)
                        .then(save(storeFood));
            } else {
                return error(denyUpdate("store's food"));
            }
        });
    }

    public Mono<StoreFood> sellFood(String uid, String storeFoodId, double qty) {
        return getStoreFoodById(storeFoodId).flatMap(storeFood -> {
            if (storeFood.getUid().equals(uid)) {
                return intercomService.onSellFood(storeFood, qty);
            } else {
                return error(denyUpdate("store's food"));
            }
        });
    }
}
