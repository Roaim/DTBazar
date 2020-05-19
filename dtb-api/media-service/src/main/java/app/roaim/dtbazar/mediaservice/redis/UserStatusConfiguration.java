package app.roaim.dtbazar.mediaservice.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class UserStatusConfiguration {
    @Bean
    ReactiveRedisOperations<String, UserStatus> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserStatus> serializer = new Jackson2JsonRedisSerializer<>(UserStatus.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, UserStatus> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, UserStatus> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}