package by.baranouski.mapphotoapp.userservice.client.impl;

import by.baranouski.mapphotoapp.userservice.client.AuthProviderClient;
import by.baranouski.mapphotoapp.userservice.config.KeycloakConfig;
import by.baranouski.mapphotoapp.userservice.exception.KeycloakException;
import by.baranouski.mapphotoapp.userservice.model.Role;
import by.baranouski.mapphotoapp.userservice.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KeycloakClient implements AuthProviderClient {

    KeycloakConfig keycloakConfig;

    public String createUser(User user, String password) {
        log.info("Registering {}-type user in keycloak from invitation for email '{}' username '{}'",
                user.getRole(), user.getEmail(), user.getUsername());
        var keycloak = keycloakConfig.getKeycloakBaseConfiguration();
        var realm = keycloak.realm(keycloakConfig.getRealm());

        var usersResource = realm.users();
        var userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setCredentials(buildCredentials(password));

        try (var response = usersResource.create(userRepresentation)) {
            if (response.getStatusInfo().getStatusCode() != HttpStatus.CREATED.value()) {
                throw new KeycloakException("Could not register user " + user.getUsername());
            }
            var userId = CreatedResponseUtil.getCreatedId(response);
            log.info("User (email '{}') successfully registered with id {}", user.getEmail(), userId);
            addRoleToUser(userId, user.getRole(), realm);
            log.info("Successfully added role '{}' to keycloak user {}", user.getRole(), userId);
            return userId;
        } catch (Exception e) {
            throw new KeycloakException("Could not register user " + user.getUsername(), e);
        }
    }

    @Override
    public void blockUser(User user) {
        setEnabled(user, false);
    }

    @Override
    public void unblockUser(User user) {
        setEnabled(user, true);

    }

    private void setEnabled(User user, boolean enabled) {
        try {
            log.info("Attempting to set enabled={} for user with id '{}' in keycloak", enabled, user.getId());
            var keycloak = keycloakConfig.getKeycloakBaseConfiguration();
            var realm = keycloak.realm(keycloakConfig.getRealm());
            var userResource = realm.users().get(user.getId());
            var userRepresentation = userResource.toRepresentation();
            userRepresentation.setEnabled(enabled);
            userResource.update(userRepresentation);
            log.info("Successfully set enabled={} for user with id '{}' in keycloak", enabled, user.getId());
        } catch (Exception e) {
            throw new KeycloakException(String.format("Could not set enabled to '%s' for user '%s': ", enabled, user.getId()), e);
        }
    }

    private void addRoleToUser(String userId, Role role, RealmResource realm) {
        var userResource = getUserById(userId, realm);
        var roleResource = getRole(role, realm);
        var roleRepresentation = roleResource.toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }

    private UserResource getUserById(String userId, RealmResource realm) {
        var userResource = realm.users().get(userId);

        if (userResource != null) {
            return userResource;
        }
        throw new KeycloakException("Could not find user by ID " + userId);
    }


    private RoleResource getRole(Role role, RealmResource realm) {
        var roleResource = realm.roles().get(role.getValue());
        if (roleResource != null) {
            return roleResource;
        }
        throw new KeycloakException("Could not find role " + role.getValue());
    }

    private List<CredentialRepresentation> buildCredentials(String password) {
        var creds = new CredentialRepresentation();
        creds.setType(CredentialRepresentation.PASSWORD);
        creds.setTemporary(false);
        creds.setValue(password);
        return List.of(creds);
    }
}
