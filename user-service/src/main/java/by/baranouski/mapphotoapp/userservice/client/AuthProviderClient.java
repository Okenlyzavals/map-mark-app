package by.baranouski.mapphotoapp.userservice.client;

import by.baranouski.mapphotoapp.userservice.model.User;

public interface AuthProviderClient {
    String createUser(User user, String password);

    void blockUser(User user);

    void unblockUser(User user);
}
