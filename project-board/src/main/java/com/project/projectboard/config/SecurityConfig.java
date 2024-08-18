package com.project.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
//@EnableWebSecurity
//springboot 에서 springSecurity 연동해서 사용할 때는 따로 추가하지 않아도 됨 -> AutoConfig안에 들어있음
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .formLogin(withDefaults()); // Use the withDefaults() method for formLogin defaults

        return http.build();
    }
}
