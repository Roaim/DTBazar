package app.roaim.dtbazar.authservice.service;

import app.roaim.dtbazar.authservice.domain.User;
import app.roaim.dtbazar.authservice.jwt.JWTUtil;
import app.roaim.dtbazar.authservice.jwt.JwtData;
import app.roaim.dtbazar.authservice.jwt.JwtToken;
import app.roaim.dtbazar.authservice.model.FbUserProfile;
import app.roaim.dtbazar.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class AuthService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        return userRepository.save(user);
    }

    public Mono<User> getUserByFbAccessToken(String fbAccessToken) {
        return userRepository.findTopByFbAccessToken(fbAccessToken);
    }

    public Mono<JwtToken> generateJwtToken(User user) {
        return generateJwtToken(user.getId(), user.getName());
    }

    public Mono<JwtToken> generateJwtToken(String sub, String name) {
        return generateJwtToken(sub, name, singletonList("ROLE_USER"));
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
        return userRepository.findById(id);
    }

    public Mono<User> saveOrGetUser(FbUserProfile fbUserProfile, String facebookAccessToken, String xForwardedFor) {
        String locationId = fbUserProfile.getLocation() == null ? null :
                fbUserProfile.getLocation().getId();
        String locationName = fbUserProfile.getLocation() == null ? null :
                fbUserProfile.getLocation().getName();
        String fbProfilePicture = null;
        if (fbUserProfile.getPicture() != null && fbUserProfile.getPicture().getData() != null) {
            fbProfilePicture = fbUserProfile.getPicture().getData().getUrl();
        }
        return userRepository.findTopByFbId(fbUserProfile.getId()).switchIfEmpty(
                Mono.just(new User(fbUserProfile.getId(),
                        fbUserProfile.getName(), fbUserProfile.getEmail(), fbUserProfile.getGender(),
                        locationId, locationName, fbProfilePicture, facebookAccessToken, xForwardedFor)
                )
        ).map(user -> {
            user.setFbAccessToken(facebookAccessToken);
            return user;
        }).flatMap(this::saveUser);
    }

}

