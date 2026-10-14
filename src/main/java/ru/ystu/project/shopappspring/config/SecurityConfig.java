package ru.ystu.project.shopappspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.ystu.project.shopappspring.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Публичные страницы (доступны всем)
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        // Только для ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Всё остальное — после входа
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                // Отключаем CSRF для H2-консоли (иначе она не откроется)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                // Разрешаем H2-консоль во фрейме (по умолчанию Spring запрещает)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}