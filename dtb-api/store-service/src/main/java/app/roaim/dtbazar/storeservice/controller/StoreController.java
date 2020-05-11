package app.roaim.dtbazar.storeservice.controller;

import app.roaim.dtbazar.storeservice.domain.Store;
import app.roaim.dtbazar.storeservice.dto.StoreDto;
import app.roaim.dtbazar.storeservice.jwt.JWTUtil;
import app.roaim.dtbazar.storeservice.jwt.JwtData;
import app.roaim.dtbazar.storeservice.service.MediaDeleteService;
import app.roaim.dtbazar.storeservice.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/store")
@AllArgsConstructor
public class StoreController {

    private final StoreService service;
    private final MediaDeleteService mediaDeleteService;
    private final JWTUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Store> saveStore(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                          @RequestBody StoreDto storeDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.saveStore(storeDto.toStore(jwtData.getSub(), jwtData.getName()));
    }

    @GetMapping
    Flux<Store> getStores(@RequestParam(required = false) String name,
                          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return name == null ? service.getAllStores(page, size) : service.getStoresByName(name, page, size);
    }

    @GetMapping("/{storeId}")
    Mono<Store> getStore(@PathVariable String storeId) {
        return service.getStoreById(storeId);
    }

    @GetMapping("/nearby")
    Flux<Store> getNearby(@RequestParam double lat, @RequestParam double lon,
                          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getNearByStores(lat, lon, page, size);
    }

    @PutMapping("/{storeId}")
    Mono<Store> updateStore(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                            @PathVariable String storeId, @RequestBody StoreDto storeDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        Store store = storeDto.toStore(jwtData.getSub(), jwtData.getName());
        return service.updateStoreId(storeId, store);
    }

    @DeleteMapping("/{storeId}")
    Mono<Store> deleteStore(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                            @PathVariable String storeId) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.deleteStoreById(storeId, jwtData.getSub()).doOnSuccess(store -> {
            if (store != null) mediaDeleteService.delete(store.getStoreFrontImage().getId(), bearerToken);
        });
    }
}
