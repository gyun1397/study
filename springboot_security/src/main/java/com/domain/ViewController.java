package com.domain;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ViewController {

    @GetMapping("/")
    public String rootPage() {
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String mainPage(@AuthenticationPrincipal User user,
            Map<String, Object> model) {
        model.put("username", user.getUsername());
        return "main";
    }

    @GetMapping("/admin")
    public String adminPage(@AuthenticationPrincipal User user,
            Map<String, Object> model) {
        model.put("username", user.getUsername());
        return "admin";
    }

    @GetMapping("/user")
    public String userPage(@AuthenticationPrincipal User user,
            Map<String, Object> model) {
        model.put("username", user.getUsername());
        return "user";
    }
}