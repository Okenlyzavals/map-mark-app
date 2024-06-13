package by.baranouski.mapphotoapp.userservice.config;

import lombok.Data;
import lombok.ToString;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("local.keycloak")
@Data
public class KeycloakConfig {
    private String serverUri;
    private String realm;

    @ToString.Exclude
    private String clientSecret;
    private String userManagementClientId;

    public Keycloak getKeycloakBaseConfiguration() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUri)
                .realm(realm)
                .clientSecret(clientSecret)
                .clientId(userManagementClientId)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
