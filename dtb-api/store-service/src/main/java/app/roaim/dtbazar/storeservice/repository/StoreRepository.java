package app.roaim.dtbazar.storeservice.repository;

import app.roaim.dtbazar.storeservice.domain.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface StoreRepository extends ReactiveMongoRepository<Store, String> {
    Flux<Store> findAllBy(Pageable pageable);

    Flux<Store> findByLocationNear(Point location, Pageable pageable);

    Flux<Store> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
}
