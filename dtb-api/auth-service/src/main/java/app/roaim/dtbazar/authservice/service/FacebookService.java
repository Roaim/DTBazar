package app.roaim.dtbazar.authservice.service;

import app.roaim.dtbazar.authservice.model.FbUserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Service
@RefreshScope
public class FacebookService {

    @Value("${spring.social.facebook.appId}")
    String facebookAppId;
    @Value("${spring.social.facebook.appSecret}")
    String facebookSecret;
    @Value("${dtbazar.url.scheme}")
    private String scheme;
    @Value("${dtbazar.gateway.fb.redirect.host}")
    private String host;
    @Value("${dtbazar.api.version}")
    private String apiVersion;
	@Value("${dtbazar.gateway.fb.oauth.scopes}")
    private String scopes;

    public String getRedirectUrl() {
        return String.format("%s://%s/api/%s/auth/facebook/accessToken", scheme, host, apiVersion);
    }

    public Mono<String> createFacebookAuthorizationUrl() {
        return Mono.fromCallable(this::generateFacebookAuthorizationUrl);
    }

    public String generateFacebookAuthorizationUrl() {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(getRedirectUrl());
        params.setScope(scopes);
        return oauthOperations.buildAuthorizeUrl(params);
    }

    public Mono<AccessGrant> createFacebookAccessToken(String code) {
        return Mono.fromCallable(() -> generateFbAccessToken(code));
    }

    private AccessGrant generateFbAccessToken(String code) {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
        return connectionFactory.getOAuthOperations().exchangeForAccess(code, getRedirectUrl(), null);
    }

    public Mono<FbUserProfile> getFacebookUserProfile(String accessToken) {
        return Mono.fromCallable(() -> getFacebookUser(accessToken));
    }

    public FbUserProfile getFacebookUser(String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        String[] fields = {"id", "name", "email", "gender", "location", "picture{url}"};
        return facebook.fetchObject("me", FbUserProfile.class, fields);
    }

}
