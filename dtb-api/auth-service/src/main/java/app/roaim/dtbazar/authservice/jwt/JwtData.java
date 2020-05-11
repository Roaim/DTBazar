package app.roaim.dtbazar.authservice.jwt;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Builder
public class JwtData {
    private String name;
    private List<String> roles;
    private Date exp;
    private Date iat;
    private String sub;
}