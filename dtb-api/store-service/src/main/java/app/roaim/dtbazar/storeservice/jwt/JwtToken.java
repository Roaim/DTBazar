package app.roaim.dtbazar.storeservice.jwt;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class JwtToken {
    String token;
    LocalDateTime expires;
    LocalDateTime created;
}
