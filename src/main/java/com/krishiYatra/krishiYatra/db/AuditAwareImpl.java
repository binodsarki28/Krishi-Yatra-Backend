//package com.krishiYatra.krishiYatra.db;
//
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Component
//@Slf4j
//public class AuditAwareImpl implements AuditorAware<String> {
//    private static final String DEFAULT_USERNAME = "AUDITOR";
//
//    @NotNull
//    @Override
//    public Optional<String> getCurrentAuditor() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
//            String name = jwtAuthenticationToken.getToken().getClaimAsString("name");
//            log.debug("Retrieved name claim: {}", name);
//
//            if (name == null) {
//                name = jwtAuthenticationToken.getToken().getClaimAsString("sub");
//            }
//
//            if (name != null) {
//                return Optional.of(name);
//            }
//        }
//        return Optional.of(DEFAULT_USERNAME);
//    }
//
//    public Optional<String> getCurrentAuditorEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
//            return Optional.ofNullable(jwtAuthenticationToken.getToken().getClaimAsString("email"));
//        }
//        return Optional.empty();
//    }
//
//    public Optional<String[]> getCurrentAuditorNameParts() {
//        return getCurrentAuditor().map(fullName -> fullName.split(" ", 2));
//    }
//
//    public boolean isUserAuthenticated() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null) {
//            return false;
//        }
//
//        if (authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal())) {
//            return false;
//        }
//
//        return authentication.isAuthenticated() && authentication instanceof JwtAuthenticationToken;
//    }
//}
