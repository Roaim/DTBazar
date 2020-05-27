package app.roaim.dtbazar.storeservice.repository;

import app.roaim.dtbazar.storeservice.domain.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface StoreRepository extends ReactiveMongoRepository<Store, String> {
    Flux<Store> findAllByEnabledTrue(Pageable pageable);

    Flux<Store> findByLocationNearAndEnabledTrue(Point location, Pageable pageable);

    Flux<Store> findByNameStartsWithIgnoreCaseAndEnabledTrue(String name, Pageable pageable);

    Flux<Store> findAllByUidAndEnabledTrue(String uid, Pageable pageable);

    Flux<Store> findAllByOrderByIdDesc(Pageable pageable);

    Flux<Store> findAllByEnabledOrderByIdDesc(boolean enabled, Pageable pageable);
}
