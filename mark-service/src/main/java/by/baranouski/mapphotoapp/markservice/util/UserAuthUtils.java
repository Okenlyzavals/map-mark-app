package by.baranouski.mapphotoapp.markservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuthUtils {

    public static String getCurrentUserId(){
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .orElse("");
    }

    public static Boolean isSelf(String id){
        return getCurrentUserId().compareTo(id) == 0;
    }

    public static Boolean isAdmin(){
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getAuthorities)
                .map(grantedAuthorities -> grantedAuthorities
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(grantedAuthority ->
                                grantedAuthority.compareTo("ROLE_ADMIN") == 0))
                .orElse(Boolean.FALSE);
        }

    private UserAuthUtils(){}
}
