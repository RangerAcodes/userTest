package com.example.userservice.security;

import com.example.userservice.Models.Role;
import org.springframework.security.core.GrantedAuthority;

public class CustomSpringGrantedAuthority implements GrantedAuthority {
    private Role role;

    public CustomSpringGrantedAuthority(Role role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role.getRole();
    }
}