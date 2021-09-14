package com.domain.filter;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.domain.util.RedisUtil;
import com.domain.util.TokenUtil;

public class JwtAuthenticationFilter  extends OncePerRequestFilter {
    
    private String loginUrl = "/auth/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        if (!loginUrl.equals(path)) {
            AbstractAuthenticationToken authentication = null;
            try {
                Optional<String> token = TokenUtil.extractJWT(request);
                if (token.isPresent()) {
                    if (!RedisUtil.hasKey(token.get())) {
                        Optional<String> user = TokenUtil.verifyJWT(token.get());
                        if (user.isPresent()) {
                            authentication = new UsernamePasswordAuthenticationToken(user.get(), null, TokenUtil.getAuthorities(token.get()));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (authentication == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "권한 없음");
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
    
}
