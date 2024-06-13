package by.baranouski.mapphotoapp.userservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuthUtil {

    public static String getCurrentUserId() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .orElse("");
    }

    public static Boolean isUserAccessible(String id) {
        return getCurrentUserId().compareTo(id) == 0;
    }

    private UserAuthUtil() {
    }
}
