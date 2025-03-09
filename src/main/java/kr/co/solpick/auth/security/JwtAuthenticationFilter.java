//package kr.co.solpick.auth.security;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import  jakarta.servlet.FilterChain;
//import  jakarta.servlet.ServletException;
//import  jakarta.servlet.http.HttpServletRequest;
//import  jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String token = jwtTokenProvider.resolveToken(request);
//        String requestURI = request.getRequestURI();
//
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            Authentication authentication = jwtTokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
//        } else {
//            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}