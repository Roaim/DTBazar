package app.roaim.dtbazar.authservice.jwt;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class JwtToken {
    String token;
    LocalDateTime expires;
    LocalDateTime created;
}
