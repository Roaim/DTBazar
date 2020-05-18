package app.roaim.dtbazar.authservice.redis;

import app.roaim.dtbazar.authservice.service.AuthService;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

// Uncomment to update last 100 users' status to redis
@Component
public class UserStatusUpdater {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, UserStatus> userStatusOps;
    private final AuthService authService;

    public UserStatusUpdater(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, UserStatus> userStatusOps, AuthService authService) {
        this.factory = factory;
        this.userStatusOps = userStatusOps;
        this.authService = authService;
    }

    @PostConstruct
    public void loadData() {
//        un-comment to delete all values
//        factory.getReactiveConnection().serverCommands().flushAll();

        authService.getUserList(0, 100, null).flatMap(user ->
                userStatusOps.opsForValue().set(user.getId(), new UserStatus(user.isEnabled()))
        ).thenMany(userStatusOps.keys("*")).flatMap(userStatusOps.opsForValue()::get)
                .subscribe(System.out::println);
    }
}