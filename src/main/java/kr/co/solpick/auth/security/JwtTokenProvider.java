////package kr.co.solpick.auth.security;
////
////import io.jsonwebtoken.Jwts;
////import io.jsonwebtoken.SignatureAlgorithm;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.stereotype.Component;
////
////import java.util.Date;
////
////@Component
////public class JwtTokenProvider {
////    @Value("${jwt.secret}")
////    private String secretKey;
////
////    @Value("${jwt.expiration}")
////    private long validityInMilliseconds;
////
////    public String createToken(String email) {
////        Date now = new Date();
////        Date validity = new Date(now.getTime() + validityInMilliseconds);
////
////        return Jwts.builder()
////                .setSubject(email)
////                .setIssuedAt(now)
////                .setExpiration(validity)
////                .signWith(SignatureAlgorithm.HS256, secretKey)
////                .compact();
////    }
////
////    public String getEmailFromToken(String token) {
////        return Jwts.parser()
////                .setSigningKey(secretKey)
////                .parseClaimsJws(token)
////                .getBody()
////                .getSubject();
////    }
////
////    public boolean validateToken(String token) {
////        try {
////            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
////            return true;
////        } catch (Exception e) {
////            return false;
////        }
////    }
////}
//
//package kr.co.solpick.auth.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.http.HttpServletRequest;
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Component
//public class JwtTokenProvider {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    @Value("${jwt.token-validity-in-milliseconds}")
//    private long tokenValidityInMilliseconds;
//
//    private final UserDetailsService userDetailsService;
//    private Key key;
//
//    public JwtTokenProvider(UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//    @PostConstruct
//    protected void init() {
//        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public String createToken(String email, Long userId) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("email", email);
//        claims.put("userId", userId);
//
//        long now = (new Date()).getTime();
//        Date validity = new Date(now + this.tokenValidityInMilliseconds);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(now))
//                .setExpiration(validity)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public Authentication getAuthentication(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        String username = claims.get("email", String.class);
//        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }
//
//    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jws<Claims> claims = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//            return !claims.getBody().getExpiration().before(new Date());
//        } catch (Exception e) {
//            log.error("토큰 검증 실패", e);
//            return false;
//        }
//    }
//}