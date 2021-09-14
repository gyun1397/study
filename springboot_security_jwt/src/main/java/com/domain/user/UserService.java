package com.domain.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.domain.role.Role;
import com.domain.role.RoleRepository;
import com.domain.util.RedisUtil;
import com.domain.util.TokenUtil;
import lombok.RequiredArgsConstructor;

@Service("userService")
@RequiredArgsConstructor
public class UserService {
    private final UserRepository  userRepository;
    private final RoleRepository  roleRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(String userId, String password) throws Exception {
        User user = userRepository.findOneByUserId(userId);
        if (user == null) {
            throw new Exception("해당 ID를 찾을 수 없습니다.");
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("패스워드가 틀렸습니다.");
        }
        Map<String, Object> claims = getClaims(user);
        return TokenUtil.makeToken(claims);
    }
    
    public void logout(String jwt) {
        long expireDate = TokenUtil.getExpireDate(jwt).getTime();
        System.currentTimeMillis();
        RedisUtil.setDataExpire(jwt, "1", expireDate - System.currentTimeMillis());
    }
    
    public User createUser(UserDTO userDTO) {
        User user = userDTO.convertUser();
        for (String strRole : userDTO.getRoles()) {
            Role role = roleRepository.findOneByRole(strRole);
            if (role != null) {
                user.addRole(role);
            }
        }
        user = userRepository.save(user);
        return user;
    }

    private Map<String, Object> getClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUserId());
        claims.put("role", user.getAuthorities());
        return claims;
    }
}
