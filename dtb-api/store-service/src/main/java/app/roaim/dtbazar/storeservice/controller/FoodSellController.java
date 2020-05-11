package app.roaim.dtbazar.storeservice.controller;

import app.roaim.dtbazar.storeservice.domain.FoodSell;
import app.roaim.dtbazar.storeservice.dto.FoodSellDto;
import app.roaim.dtbazar.storeservice.jwt.JWTUtil;
import app.roaim.dtbazar.storeservice.jwt.JwtData;
import app.roaim.dtbazar.storeservice.service.FoodSellService;
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
@RequestMapping("/foodSell")
public class FoodSellController {
    private final FoodSellService service;
    private final JWTUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FoodSell> addFoodToStore(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                         @RequestBody FoodSellDto foodSellDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.saveFoodSell(jwtData.getSub(), foodSellDto);
    }

    @GetMapping
    Flux<FoodSell> getAllFoodSells(
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String foodId,
            @RequestParam(required = false) String storeFoodId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllFoodSell(foodId, storeId, storeFoodId, page, size);
    }

    @GetMapping("/{foodSellId}")
    Mono<FoodSell> getFoodSellById(@PathVariable String foodSellId) {
        return service.getFoodSellById(foodSellId);
    }

    @PutMapping("/{foodSellId}")
    Mono<FoodSell> updateFoodSell(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                  @PathVariable String foodSellId, @RequestBody FoodSellDto foodSellDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.updateFoodSellId(jwtData.getSub(), foodSellId, foodSellDto);
    }

    @DeleteMapping("/{foodSellId}")
    Mono<FoodSell> deleteFoodSell(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                  @PathVariable String foodSellId) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.deleteFoodSellById(jwtData.getSub(), foodSellId);
    }
}
