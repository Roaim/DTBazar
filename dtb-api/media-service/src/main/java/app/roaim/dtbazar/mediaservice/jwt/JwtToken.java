package app.roaim.dtbazar.mediaservice.jwt;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class JwtToken {
    String token;
    LocalDateTime expires;
    LocalDateTime created;
}
