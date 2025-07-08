package cn.zzb.grade.utils;

import cn.hutool.core.util.IdUtil;
import cn.zzb.grade.entity.User;
import cn.zzb.grade.exceptions.ApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static cn.zzb.grade.constants.ExceptionEnum.JWT_ERROR;
import static cn.zzb.grade.constants.ExceptionEnum.NOT_LOGIN;

/**
 * @author zzb
 */
@Slf4j
public class JwtUtil {
    private static final String USER_ID_KEY = "userId";
    private static final String USER_ROLE_KEY = "role";

    // 设置 token 有效期为 7 天
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;
    // 使用 Base64 编码保存以便配置或查看
    private static final String SIGNATURE = "YurdsAOzJbSYbcnC3DWtYXaz/znh4AqfQmhQ9NEb/YM=";

    /**
     * 根据用户信息生成 JWT token
     *
     * @param user 用户对象，包含用户的ID
     * @return 生成的 JWT token 字符串
     */
    public static String enCodeToken(User user) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder
                // 设置请求头
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                // 设置自定义负载信息payload
                .claim(USER_ID_KEY, user.getUserId())
                .claim(USER_ROLE_KEY, user.getRole())
                .setSubject("Authentication")
                // 过期日期
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                // 是JWT的唯一标识，回避重放攻击
                .setId(IdUtil.simpleUUID())
                // 签名Signature
                .signWith(SignatureAlgorithm.HS256, SIGNATURE)
                .compact();
    }

    /**
     * 解码 JWT token，获取其中的 Claims 信息。
     *
     * @param token 待解码的 JWT token 字符串
     * @return Claims对象，包含token中的声明信息
     */
    private static Claims deCodeToken(String token) {
        try {
            // 创建一个 JWT 解析器，设置签名密钥
            Jws<Claims> claimsJws = Jwts
                    .parser()
                    .setSigningKey(SIGNATURE)
                    .parseClaimsJws(token);
            // 获取 JWT 中的 Claims 对象
            // 返回解析后的 Claims 对象
            return claimsJws.getBody();
        } catch (JwtException e) {
            throw new ApiException(JWT_ERROR);
        }
    }

    public static User getUserFromToken(String token) {
        // 去除 "Bearer " 前缀
        if (token != null) {
            token = token.replace("Bearer ", "");
        }
        // 解析 token 获取 Claims 数据
        Claims claims = deCodeToken(token);
        if (claims == null || !claims.containsKey(USER_ID_KEY) || !claims.containsKey(USER_ROLE_KEY)) {
            throw new ApiException(NOT_LOGIN);
        }
        // 从 Claims 中提取用户数据，并创建 User 对象
        User user = new User();
        user.setUserId((String) claims.get(USER_ID_KEY));
        user.setRole((String) claims.get(USER_ROLE_KEY));
        // 返回创建的 User 对象
        return user;
    }
}
