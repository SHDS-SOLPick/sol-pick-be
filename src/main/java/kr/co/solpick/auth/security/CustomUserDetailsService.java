//package kr.co.solpick.auth.security;
//
//import kr.co.solpick.member.entity.Member;
//import kr.co.solpick.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
////import org.springframework.transaction.annotation.Transactional;
//import jakarta.transaction.Transactional;
//import java.util.Collections;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    @Transactional(readOnly = true)
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return memberRepository.findByEmail(email)
//                .map(this::createUserDetails)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
//    }
//
//    private UserDetails createUserDetails(Member member) {
//        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
//
//        return new User(
//                member.getEmail(),
//                "", // 레시픽 인증을 사용하므로 실제 비밀번호는 사용하지 않음
//                Collections.singleton(authority)
//        );
//    }
//}