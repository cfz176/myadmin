package com.cfz.utils;

import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@ConfigurationProperties( prefix = "cfz.jwt")
public class JwtUtil {

    private long expire;
    private String secret;
    private String header;

    //生成jwt
    public String generateToken(String username) {

        Date nowDate = new Date();
        Date expiredate = new Date(nowDate.getTime() + 1000 * expire);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")/*jwt头部*/
                .setSubject(username)/*jwt主体*/
                .setIssuedAt(nowDate)/*jwt创建时间*/
                .setExpiration(expiredate)/*jwt过期时间*/
                .signWith(SignatureAlgorithm.HS512, secret)/*算法和密钥*/
                .compact();

    }

    //解析jwt
    public Claims getClaimsByToken(String jwt) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)/*密钥*/
                    .parseClaimsJws(jwt)/*解析jwt*/
                    .getBody();
        } catch (Exception e) {
            return null; /*返回空，jwt非法*/
        }
    }

    //jwt是否过期
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

}
