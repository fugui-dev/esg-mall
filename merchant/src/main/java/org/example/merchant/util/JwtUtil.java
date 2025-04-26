package org.example.merchant.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.example.merchant.entity.User;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class JwtUtil {

    private static final String SECRET_KEY = "esg_mall_secret";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时
    private static final String TOKEN_KEY_PREFIX = "token:";
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    public JwtUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRE_TIME);

        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("role",user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        // 将token存入Redis，设置过期时间
        String redisKey = TOKEN_KEY_PREFIX + user.getRole()    + ":" + user.getId();

        redisTemplate.opsForValue().set(redisKey, token, EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return "Bearer " + token;
    }

    public void invalidateToken(User user){

        String redisKey = TOKEN_KEY_PREFIX + user.getRole()    + ":" + user.getId();

        redisTemplate.delete(redisKey);
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("Token已过期：", e);
            throw e;
        } catch (JwtException e) {
            log.error("Token解析失败：", e);
            throw e;
        }
    }

    public void invalidateToken(String token) {

        Long userId = getUserId(token);

        String userRole = getUserRole(token);

        if (userId != null) {
            // 从Redis中删除token
            String redisKey = TOKEN_KEY_PREFIX + userRole + ":" + userId;
            redisTemplate.delete(redisKey);

            // 将token加入黑名单
            Claims claims = parseToken(token);
            if (claims != null) {
                Date expiration = claims.getExpiration();
                long ttl = expiration.getTime() - System.currentTimeMillis();
                if (ttl > 0) {
                    redisTemplate.opsForValue().set(TOKEN_BLACKLIST_PREFIX + token, "1", ttl, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public String getUserRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }


}