package app.roaim.dtbazar.storeservice.controller;

import app.roaim.dtbazar.storeservice.domain.Food;
import app.roaim.dtbazar.storeservice.domain.FoodDetail;
import app.roaim.dtbazar.storeservice.dto.FoodDto;
import app.roaim.dtbazar.storeservice.jwt.JWTUtil;
import app.roaim.dtbazar.storeservice.jwt.JwtData;
import app.roaim.dtbazar.storeservice.service.FoodService;
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
@RequestMapping("/food")
public class FoodController {
    private final FoodService service;
    private final JWTUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Food> createFood(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                 @RequestBody FoodDto foodDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.saveFood(foodDto.toFood(jwtData.getSub()));
    }

    @GetMapping
    public Flux<Food> getFoods(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getFoods(page, size);
    }

    @GetMapping("/{foodId}")
    public Mono<FoodDetail> getFoodById(@PathVariable String foodId) {
        return service.getFoodById(foodId);
    }

    @PutMapping("/{foodId}")
    public Mono<Food> updateFoodById(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                     @PathVariable String foodId, @RequestBody FoodDto foodDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        Food food = foodDto.toFood(jwtData.getSub());
        return service.updateFoodById(foodId, food);
    }

    @DeleteMapping("/{foodId}")
    public Mono<FoodDetail> deleteFoodById(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                           @PathVariable String foodId) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.deleteFoodById(jwtData.getSub(), foodId);
    }

}
