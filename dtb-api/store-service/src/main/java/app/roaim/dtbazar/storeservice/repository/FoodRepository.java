package app.roaim.dtbazar.storeservice.repository;

import app.roaim.dtbazar.storeservice.domain.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

// TODO move to a separate micro service
public interface FoodRepository extends ReactiveMongoRepository<Food, String> {
    Flux<Food> findAllByEnabled(boolean enabled, Pageable pageable);
    Flux<Food> findAllByOrderByIdDesc(Pageable pageable);
    Flux<Food> findAllByEnabledOrderByIdDesc(boolean enabled, Pageable pageable);
}
