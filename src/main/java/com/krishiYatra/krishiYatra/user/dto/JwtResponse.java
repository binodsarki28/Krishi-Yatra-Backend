package com.krishiYatra.krishiYatra.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String fullName;
    private String email;
    private List<String> roles;
    private List<String> verifiedRoles;

    public JwtResponse(String token, String username, String fullName, String email, List<String> roles, List<String> verifiedRoles) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
        this.verifiedRoles = verifiedRoles;
    }
}
