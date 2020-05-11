package app.roaim.dtbazar.storeservice.repository;

import app.roaim.dtbazar.storeservice.domain.FoodDetail;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

// TODO move to a separate micro service
public interface FoodDetailRepository extends ReactiveMongoRepository<FoodDetail, String> {
    Mono<FoodDetail> findFirstByFood_Id(String foodId);

    Mono<FoodDetail> deleteFoodDetailByFood_Id(String foodId);
}
