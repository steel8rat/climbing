package com.mokhov.climbing.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;
    private String name;

    @Indexed
    private String nickname;
    private String email;
    private String photoPath;
    private String token;
    private List<GrantedAuthority> authorities = new ArrayList<>();

    private Set<String> homeGymIds;

    @JsonIgnore
    private String photoFileName;

    @JsonIgnore
    private String appleRefreshToken;

    @Indexed
    private String appleIdCredentialUser;

    public User(AppleIdCredential appleIdCredential) {
        this.appleIdCredentialUser = appleIdCredential.getUser();
        this.email = appleIdCredential.getEmail();
        this.name = appleIdCredential.getFullName().toString();
    }

    @PersistenceConstructor
    public User(String id, String name, String email, String appleIdCredentialUser) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.appleIdCredentialUser = appleIdCredentialUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void addRole(String role) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
        if (!authorities.contains(authority)) {
            authorities.add(authority);
        }
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }


    @Override
    public String getUsername() {
        return nickname;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

}
