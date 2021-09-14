package com.domain.user;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long      id;
    private String    userId;
    private String    userName;
    private String    password;
    private String    email;
    private Set<String> roles = new HashSet<>();
    
    public User convertUser() {
        User user = new User(this.userId, this.userName, this.password, this.email);
        return user;
    }
}
