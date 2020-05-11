package app.roaim.dtbazar.storeservice.controller;

import app.roaim.dtbazar.storeservice.domain.StoreFood;
import app.roaim.dtbazar.storeservice.dto.StoreFoodDto;
import app.roaim.dtbazar.storeservice.jwt.JWTUtil;
import app.roaim.dtbazar.storeservice.jwt.JwtData;
import app.roaim.dtbazar.storeservice.model.AddStock;
import app.roaim.dtbazar.storeservice.service.StoreFoodService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

// TODO move to a separate micro service
@RestController
@AllArgsConstructor
@RequestMapping("/storeFood")
public class StoreFoodController {
    private final StoreFoodService service;
    private final JWTUtil jwtUtil;

    @PostMapping("/store/{storeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<StoreFood> addFoodToStore(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                          @PathVariable String storeId, @RequestBody StoreFoodDto storeFoodDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.save(jwtData.getSub(), storeId, storeFoodDto);
    }

    @GetMapping("/store/{storeId}")
    public Flux<StoreFood> getStoreFoodsByStoreId(@PathVariable String storeId,
                                                  @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getStoreFoods(storeId, page, size);
    }

    @GetMapping("/food/{foodId}")
    public Flux<StoreFood> getStoreFoodsByFoodId(@PathVariable String foodId,
                                                 @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getStoreFoodsByFoodId(foodId, page, size);
    }

    @GetMapping("/store/{storeId}/food/{foodId}")
    public Mono<StoreFood> getByStoreAndFoodId(@PathVariable String storeId, @PathVariable String foodId) {
        return service.getByStoreAndFoodId(storeId, foodId);
    }

    @GetMapping("/{storeFoodId}")
    public Mono<StoreFood> getStoreFoodById(@PathVariable String storeFoodId) {
        return service.getStoreFoodById(storeFoodId);
    }

    @PutMapping("/{storeFoodId}")
    public Mono<StoreFood> updateStoreFoodById(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                               @PathVariable String storeFoodId, @RequestBody StoreFoodDto foodDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        StoreFood storeFood = foodDto.toStoreFood(jwtData.getSub());
        return service.updateByStoreFoodId(storeFoodId, storeFood);
    }

    @DeleteMapping("/{storeFoodId}")
    public Mono<StoreFood> deleteFoodById(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                          @PathVariable String storeFoodId) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.deleteStoreFoodById(jwtData.getSub(), storeFoodId);
    }

    @PatchMapping("/{storeFoodId}/addStock")
    public Mono<StoreFood> addStock(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                    @PathVariable String storeFoodId, @RequestBody AddStock addStock) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.addStock(jwtData.getSub(), storeFoodId, addStock);
    }

    @PatchMapping("/{storeFoodId}/sellFood")
    public Mono<StoreFood> sellFood(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                    @PathVariable String storeFoodId, @RequestParam double qty) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.sellFood(jwtData.getSub(), storeFoodId, qty);
    }
}
