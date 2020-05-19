package app.roaim.dtbazar.authservice.repository;

import app.roaim.dtbazar.authservice.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findTopByFbAccessToken(String fbAccessToken);

    Mono<User> findTopByFbId(String fbId);

    Flux<User> findAllByOrderByIdDesc(Pageable pageable);

    Flux<User> findAllByEnabledOrderByIdDesc(boolean enabled, PageRequest pageable);
}
