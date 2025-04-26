package org.example.merchant.filter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.merchant.common.CommonConstant;
import org.example.merchant.handler.BusinessException;
import org.example.merchant.util.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatformAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate; // 注入 RedisTemplate

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {


        // 1. 获取并清理 Token
        String token = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (token == null) {
            throw new BusinessException("未登录");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 2. 解析 JWT
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            throw new BusinessException("token无效");
        }

        // 3. 检查角色
        String role = claims.get("role", String.class);
        if (!CommonConstant.PLATFORM_ROLE.equals(role)) {
            throw new BusinessException("无权限");
        }

        // 4. 检查 Redis 中是否存在该 Token（关键新增逻辑）
        Long userId = claims.get("userId", Long.class);
        String redisKey = "token:" + role + ":" + userId; // 和 JwtUtil 的存储格式一致
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        if (!storedToken.equals(token)) {
            throw new BusinessException("账号已在其他设备登录");
        }

        return true;
    }
}