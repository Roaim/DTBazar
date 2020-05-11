package app.roaim.dtbazar.storeservice.repository;

import app.roaim.dtbazar.storeservice.domain.FoodSell;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

// TODO move to a separate micro service
public interface FoodSellRepository extends ReactiveMongoRepository<FoodSell, String> {
    Flux<FoodSell> findAllBy(Pageable pageable);

    Flux<FoodSell> findAllByStoreFoodId(String storeFoodId, Pageable pageable);

    Flux<FoodSell> findAllByStoreId(String storeId, Pageable pageable);

    Flux<FoodSell> findAllByFoodId(String foodId, Pageable pageable);
}
