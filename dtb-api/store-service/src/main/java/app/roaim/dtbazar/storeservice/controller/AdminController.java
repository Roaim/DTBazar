package app.roaim.dtbazar.storeservice.controller;

import app.roaim.dtbazar.storeservice.domain.Food;
import app.roaim.dtbazar.storeservice.domain.FoodDetail;
import app.roaim.dtbazar.storeservice.domain.Store;
import app.roaim.dtbazar.storeservice.service.FoodService;
import app.roaim.dtbazar.storeservice.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final FoodService foodService;
    private final StoreService storeService;

    @GetMapping("/food")
    Flux<Food> getAllFoodList(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return foodService.getAllFoodList(page, size, enabled);
    }

    @GetMapping("/food/{foodId}")
    Mono<FoodDetail> getFood(@PathVariable String foodId) {
        return foodService.getFoodById(foodId);
    }

    @PatchMapping("/food/{foodId}")
    Mono<Food> updateFoodStatus(
            @PathVariable String foodId, @RequestParam(defaultValue = "true") boolean enable) {
        return foodService.updateFoodStatus(foodId, enable);
    }

    @GetMapping("/store")
    Flux<Store> getAllStoreList(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return storeService.getAllStoreList(page, size, enabled);
    }

    @GetMapping("/store/{storeId}")
    Mono<Store> getStore(@PathVariable String storeId) {
        return storeService.getStoreById(storeId);
    }

    @PatchMapping("/store/{storeId}")
    Mono<Store> updateStoreStatus(
            @PathVariable String storeId, @RequestParam(defaultValue = "true") boolean enable) {
        return storeService.updateStoreStatus(storeId, enable);
    }
}