package app.roaim.dtbazar.storeservice.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RefreshScope
public class JWTUtil implements Serializable {

    private static final String KEY_NAME = "name";
    private static final String KEY_ROLES = "roles";

    @Value("${dtbazar.jwt.secret:6f075d09-cd8f-47dc-9004-a9eb6dba9960}")
    private String secret;
    @Value("${dtbazar.jwt.expireTimeInSec:604800}")
    private long expirationTime;

    public JwtData decode(String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization header must start with Bearer");
        }
        String token = bearerToken.substring(7);
        return getJwtData(token);
    }

    public JwtData getJwtData(String token) {
        return getJwtData(getAllClaimsFromToken(token));
    }

    private JwtData getJwtData(Claims claims) {
        return JwtData.builder()
                .name(claims.get(KEY_NAME, String.class))
                .roles(claims.get(KEY_ROLES, List.class))
                .exp(claims.getExpiration())
                .iat(claims.getIssuedAt())
                .sub(claims.getSubject())
                .build();
    }

    public Claims getAllClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return claims;
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public JwtToken generateToken(JwtData jwtData) {
        return generateToken(jwtData, expirationTime);
    }

    public JwtToken generateToken(JwtData jwtData, long expirationTimeInSecond) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(KEY_ROLES, jwtData.getRoles());
        claims.put(KEY_NAME, jwtData.getName());
        return doGenerateToken(claims, jwtData.getSub(), expirationTimeInSecond);
    }

    private JwtToken doGenerateToken(Map<String, Object> claims, String subject, long expirationTimeInSecond) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeInSecond * 1000);
        return new JwtToken(
                Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(createdDate)
                        .setExpiration(expirationDate)
                        .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(secret.getBytes()))
                        .compact(),
                expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                createdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
    }

    public Mono<Authentication> getAuthentication(String token) {
        return getAuthentication(getAllClaimsFromToken(token));
    }

    public Mono<Authentication> getAuthentication(Claims claims) {
        return getAuthentication(getJwtData(claims));
    }

    public Mono<Authentication> getAuthentication(JwtData jwtData) {
        if (jwtData.getSub() == null || jwtData.getExp().before(new Date())) return Mono.empty();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                jwtData.getSub(),
                null,
                jwtData.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
        return Mono.just(auth);
    }
}
