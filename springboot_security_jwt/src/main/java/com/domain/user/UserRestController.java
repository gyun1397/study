package com.domain.user;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.domain.item.Item;
import com.domain.item.ItemDTO;
import com.domain.item.ItemRepository;
import com.domain.util.TokenUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    
 private final UserService userService;
    
    
    @PostMapping("/auth/login")
    public @ResponseBody ResponseEntity<?> login(@RequestBody LoginVO loginVO) throws Exception {
        String token = userService.login(loginVO.getUserId(), loginVO.getPassword());
        return ResponseEntity.ok(new TokenVO(token));
    }

    @GetMapping("/auth/logout")
    public @ResponseBody ResponseEntity<?> logout(HttpServletRequest request) throws Exception {
        userService.logout(TokenUtil.extractJWT(request).orElseThrow(() -> new Exception("토큰 정보를 찾을 수 없습니다.")));
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/api/users")
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody ResponseEntity<?> create(@RequestBody UserDTO userDTO) throws Exception {
        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(user);
    }
}

@Setter
@Getter
@NoArgsConstructor
class TokenVO {
    private String token;
    
    public TokenVO(String token) {
        this.token = token;
    }
    
}