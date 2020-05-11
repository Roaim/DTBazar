package app.roaim.dtbazar.authservice.repository;

import app.roaim.dtbazar.authservice.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findTopByFbAccessToken(String fbAccessToken);
    Mono<User> findTopByFbId(String fbId);
}
