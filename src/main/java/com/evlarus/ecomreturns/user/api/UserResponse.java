package com.evlarus.ecomreturns.user.api;

import com.evlarus.ecomreturns.user.domain.Role;
import com.evlarus.ecomreturns.user.domain.User;
import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet())
        );
    }
}
