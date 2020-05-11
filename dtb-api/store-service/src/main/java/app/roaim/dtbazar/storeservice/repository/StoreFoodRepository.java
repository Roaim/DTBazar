package app.roaim.dtbazar.storeservice.repository;

import app.roaim.dtbazar.storeservice.domain.StoreFood;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// TODO move to a separate micro service
public interface StoreFoodRepository extends ReactiveMongoRepository<StoreFood, String> {
    Flux<StoreFood> findAllByStoreId(String storeId, Pageable pageable);

    Flux<StoreFood> findAllByFood_Id(String foodId, Pageable pageable);

    Mono<StoreFood> findFirstByStoreIdAndFood_Id(String storeId, String foodId);
}
