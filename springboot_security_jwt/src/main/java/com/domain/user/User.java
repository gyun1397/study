package com.domain.user;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.domain.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "USER")
@JsonIgnoreProperties(value = { "password" }, allowSetters = true, allowGetters = false)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "EMAIL")
    private String email;
    

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "USER_ROLE", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        if (!this.roles.isEmpty()) {
            return this.roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    
    public Set<String> getRoles() {
        return this.roles.stream().map(role -> role.getRole()).collect(Collectors.toSet());
    }
    
    public void addRole(Role role) {
        this.roles.add(role);
    }
    
    
    public User(String userId, String userName, String password, String email) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }
}
