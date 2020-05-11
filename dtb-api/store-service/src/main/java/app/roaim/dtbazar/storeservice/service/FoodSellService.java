package app.roaim.dtbazar.storeservice.service;

import app.roaim.dtbazar.storeservice.domain.FoodSell;
import app.roaim.dtbazar.storeservice.dto.FoodSellDto;
import app.roaim.dtbazar.storeservice.repository.FoodSellRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
public class FoodSellService {
    private final FoodSellRepository repository;
    private final IntercomService intercomService;

    public Mono<FoodSell> saveFoodSell(String uid, FoodSellDto foodSellDto) {
        return intercomService.getStoreFoodById(foodSellDto.getStoreFoodId())
                .flatMap(storeFood -> {
                    if (!storeFood.getUid().equals(uid)) {
                        return error(denyUpdate("store's food"));
                    }
                    FoodSell foodSell = foodSellDto.toFoodSell(storeFood);
                    return intercomService.onSellFood(storeFood, foodSell.getQty())
                            .then(repository.save(foodSell));
                });
    }

    public Flux<FoodSell> getAllFoodSell(String foodId, String storeId, String storeFoodId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        if (storeFoodId != null) {
            return repository.findAllByStoreFoodId(storeFoodId, pageable);
        } else if (storeId != null) {
            return repository.findAllByStoreId(storeId, pageable);
        } else if (foodId != null) {
            return repository.findAllByFoodId(foodId, pageable);
        }
        return repository.findAllBy(pageable);
    }

    public Mono<FoodSell> getFoodSellById(String foodSellId) {
        return repository.findById(foodSellId).switchIfEmpty(
                error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("FoodSellId: %s not found", foodSellId))
                )
        );
    }

    public Mono<FoodSell> deleteFoodSellById(String uid, String foodSellId) {
        return getFoodSellById(foodSellId).flatMap(foodSell -> {
            if (foodSell.getUid().equals(uid)) {
                return repository.deleteById(foodSellId)
                        .thenReturn(foodSell);
            } else {
                return error(denyDelete("foodSell"));
            }
        });
    }

    public Mono<FoodSell> updateFoodSellId(String uid, String foodSellId, FoodSellDto foodSellDto) {
        return getFoodSellById(foodSellId).flatMap(foodSell -> {
            if (foodSell.getUid().equals(uid)) {
                foodSell.setBuyerName(foodSellDto.getBuyerName());
                foodSell.setNid(foodSellDto.getNid());
                foodSell.setNidImage(foodSellDto.getNidImage());
                foodSell.setQty(foodSellDto.getQty());
                return repository.save(foodSell);
            } else {
                return error(denyUpdate("foodSell"));
            }
        });
    }
}
