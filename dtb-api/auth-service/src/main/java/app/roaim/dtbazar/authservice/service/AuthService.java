package app.roaim.dtbazar.authservice.service;

import app.roaim.dtbazar.authservice.domain.User;
import app.roaim.dtbazar.authservice.redis.UserStatus;
import app.roaim.dtbazar.authservice.jwt.JWTUtil;
import app.roaim.dtbazar.authservice.jwt.JwtData;
import app.roaim.dtbazar.authservice.jwt.JwtToken;
import app.roaim.dtbazar.authservice.model.FbUserProfile;
import app.roaim.dtbazar.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final ReactiveRedisOperations<String, UserStatus> userStatusOps;

    public Mono<User> saveUser(User user) {
        return userRepository.save(user);
    }

    public Mono<User> getUserByFbAccessToken(String fbAccessToken) {
        return userRepository.findTopByFbAccessToken(fbAccessToken);
    }

    public Mono<JwtToken> generateJwtToken(User user) {
        return userStatusOps.opsForValue().setIfAbsent(user.getId(), new UserStatus(user.isEnabled())).flatMap(ignore ->
                user.isEnabled()
                        ? generateJwtToken(user.getId(), user.getName(), user.isAdmin())
                        : Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are blocked"))
        );
    }

    public Mono<JwtToken> generateJwtToken(String sub, String name, boolean isAdmin) {
        List<String> usrRoles = new LinkedList<>();
        usrRoles.add("ROLE_USER");
        if (isAdmin) usrRoles.add("ROLE_ADMIN");
        return generateJwtToken(sub, name, usrRoles);
    }

    public Mono<JwtToken> generateJwtToken(String sub, String name, List<String> roles) {
        return generateJwtToken(
                JwtData.builder()
                        .sub(sub)
                        .name(name)
                        .roles(roles)
                        .build()
        );
    }

    public Mono<JwtToken> generateJwtToken(JwtData jwtData) {
        return Mono.just(jwtUtil.generateToken(jwtData));
    }

    public Mono<JwtData> decodeJwt(String bearerToken) {
        return Mono.just(jwtUtil.decode(bearerToken));
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public Mono<User> saveGetUser(FbUserProfile fbUserProfile, String facebookAccessToken, String xForwardedFor) {
        return userRepository.findTopByFbId(fbUserProfile.getId()).switchIfEmpty(
                Mono.just(new User())
        ).map(user -> {
            String locationId = fbUserProfile.getLocation() == null ? null :
                    fbUserProfile.getLocation().getId();
            String locationName = fbUserProfile.getLocation() == null ? null :
                    fbUserProfile.getLocation().getName();
            String fbProfilePicture = null;
            if (fbUserProfile.getPicture() != null && fbUserProfile.getPicture().getData() != null) {
                fbProfilePicture = fbUserProfile.getPicture().getData().getUrl();
            }
            user.update(fbUserProfile.getId(), fbUserProfile.getName(), fbUserProfile.getEmail(), fbUserProfile.getGender(),
                    locationId, locationName, fbProfilePicture, facebookAccessToken, xForwardedFor);
            return user;
        }).flatMap(this::saveUser);
    }

    public Flux<User> getUserList(int page, int size, Boolean enabled) {
        PageRequest pageable = PageRequest.of(page, size);
        return enabled == null
                ? userRepository.findAllByOrderByIdDesc(pageable)
                : userRepository.findAllByEnabledOrderByIdDesc(enabled, pageable);
    }

    public Mono<User> updateUserStatus(String userId, boolean enable, boolean admin) {
        return getUserById(userId).flatMap(user -> {
            user.setEnabled(enable);
            user.setAdmin(admin);
            return userStatusOps.opsForValue().set(userId, new UserStatus(enable))
                    .thenReturn(user);
        }).flatMap(userRepository::save);
    }
}

