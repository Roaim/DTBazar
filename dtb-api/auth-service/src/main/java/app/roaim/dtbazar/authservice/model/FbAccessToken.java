package app.roaim.dtbazar.authservice.model;

import lombok.Value;
import org.springframework.social.oauth2.AccessGrant;

@Value
public class FbAccessToken {
    String accessToken;
    String scope;
    String refreshToken;
    Long expireTime;

    public static FbAccessToken fromAccessGrant(AccessGrant accessGrant) {
        return new FbAccessToken(accessGrant.getAccessToken(), accessGrant.getScope(),
                accessGrant.getRefreshToken(), accessGrant.getExpireTime());
    }
}
