package com.quangntn.whatsappclone.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j(topic = "KEYCLOAK-JWT-AUTHENTICATION-CONVERTER")
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        log.info("KeycloakJwtAuthenticationConverter convert start");
        return new JwtAuthenticationToken(source,
                Stream.concat(new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                        extractResourceRoles(source).stream()
                ).collect(Collectors.toSet()));
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        log.info("KeycloakJwtAuthenticationConverter extractResourceRoles start");
        var resourceAccess = new HashMap<>(jwt.getClaim("resource_access"));
        return Optional.ofNullable(resourceAccess.get("account"))
                .map(account -> (Map<String, List<String>>) account)
                .map(accountMap -> accountMap.get("roles"))
                .map(roles -> roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }
}
