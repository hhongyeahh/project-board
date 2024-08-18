package com.project.projectboard.config;

import com.project.projectboard.domain.UserAccount;
import com.project.projectboard.dto.UserAccountDto;
import com.project.projectboard.dto.security.BoardPrincipal;
import com.project.projectboard.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
//@EnableWebSecurity
//springboot 에서 springSecurity 연동해서 사용할 때는 따로 추가하지 않아도 됨 -> AutoConfig안에 들어있음
@Configuration
public class SecurityConfig  {
    /*인증 구현 후 테스트 돌리면 변화
    * 컨트롤러가 다 TEST 실패
    *  => 웹 MVC 테스트라는 SliceTest 는 자동으로 Security 의 영향까지 받음*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //스프링 시큐리티의 관리 하에 두고 인증과 권한 체크하는 부분
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 정적 리소스에 대한 요청은 모두 허용 (CSS, JS, 이미지 등)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // GET 요청에 대해 특정 경로는 인증 필요 없음
                        .requestMatchers(HttpMethod.GET, "/", "/articles", "/articles/search-hashtag").permitAll()
                        // 그 외 모든 요청에 대해서는 인증 요구
                        .anyRequest().authenticated()
                )
                // 기본 로그인 페이지 설정
                .formLogin(withDefaults())
                // 로그아웃 성공 시 리다이렉트할 URL 설정
                .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }


    /*
    추천하지 않는 방식
    차라리 HttpSecurity 에서 PermitAll 하는 방식을 선택해라고 추천
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 아예 스프링 시큐리티 검사에서 제외하겠다
        // 특정 경로를 아예 보안 필터에서 제외 (static 리소스 등 : (ex) CSS, JS, HTML, 정적리소스 등)
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
     */

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository){
        return  username ->userAccountRepository
                /*
                * userAccountRepository 를 터치하는데, 이 녀석이 제대로 빈으로 등록되어 있지 않거나, 이 안에 인증 데이터가 들어가 있지 않으면
                * JPA 연결 테스트 - insert 테스트 실패 -> security 전용 설정을 넣어줘야함
                * */

                .findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)
                .orElseThrow(()-> new UsernameNotFoundException("유저를 찾을 수 없습니다. - username: "+username));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
