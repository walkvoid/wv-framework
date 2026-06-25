package com.github.walkvoid.wvframework.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JWT 工具类 — accessToken / refreshToken 生成与校验
 *
 * @author walkvoid
 */
public final class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    /** JWT claims key for user ID */
    public static final String CLAIM_USER_ID = "userId";
    /** JWT claims key for username */
    public static final String CLAIM_USERNAME = "username";
    /** JWT claims key for role codes */
    public static final String CLAIM_ROLES = "roles";

    private static volatile SecretKey accessTokenKey;
    private static volatile SecretKey refreshTokenKey;
    private static volatile long accessTokenExpiration = 7 * 24 * 60 * 60 * 1000L; // 7 days
    private static volatile long refreshTokenExpiration = 30 * 24 * 60 * 60 * 1000L; // 30 days

    private JwtUtils() {
    }

    // ==================== 配置 ====================

    public static void init(String base64Secret) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        accessTokenKey = Keys.hmacShaKeyFor(keyBytes);
        refreshTokenKey = Keys.hmacShaKeyFor((base64Secret + "-refresh").getBytes());
    }

    public static void setExpiration(long accessTokenMs, long refreshTokenMs) {
        accessTokenExpiration = accessTokenMs;
        refreshTokenExpiration = refreshTokenMs;
    }

    // ==================== 生成 Token ====================

    public static String generateAccessToken(Long userId, String username, List<String> roles) {
        return buildToken(userId, username, roles, accessTokenKey, accessTokenExpiration);
    }

    public static String generateRefreshToken(Long userId, String username) {
        return buildToken(userId, username, null, refreshTokenKey, refreshTokenExpiration);
    }

    private static String buildToken(Long userId, String username,
                                      List<String> roles, SecretKey key, long expiration) {
        Date now = new Date();
        var builder = Jwts.builder()
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key);
        if (roles != null && !roles.isEmpty()) {
            builder.claim(CLAIM_ROLES, roles);
        }
        return builder.compact();
    }

    // ==================== 校验 AccessToken ====================

    public static Claims parseAccessToken(String token) {
        if (token == null || accessTokenKey == null) {
            return null;
        }
        try {
            return Jwts.parser()
                    .verifyWith(accessTokenKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.debug("AccessToken parse failed: {}", e.getMessage());
            return null;
        }
    }

    // ==================== 校验 RefreshToken ====================

    public static Claims parseRefreshToken(String token) {
        if (token == null || refreshTokenKey == null) {
            return null;
        }
        try {
            return Jwts.parser()
                    .verifyWith(refreshTokenKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.debug("RefreshToken parse failed: {}", e.getMessage());
            return null;
        }
    }

    // ==================== 提取字段 ====================

    public static Long getUserId(Claims claims) {
        Object val = claims != null ? claims.get(CLAIM_USER_ID) : null;
        if (val instanceof Number n) return n.longValue();
        return null;
    }

    public static String getUsername(Claims claims) {
        return claims != null ? (String) claims.get(CLAIM_USERNAME) : null;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRoles(Claims claims) {
        if (claims == null) return List.of();
        Object val = claims.get(CLAIM_ROLES);
        if (val instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }

    // ==================== 便捷方法 ====================

    public static Long getUserIdFromToken(String accessToken) {
        return getUserId(parseAccessToken(accessToken));
    }

    public static String getUsernameFromToken(String accessToken) {
        return getUsername(parseAccessToken(accessToken));
    }
}
