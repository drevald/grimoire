package org.helico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/favicon.ico", "/error404.jsp").permitAll()
                .anyRequest().hasRole("USER")
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/dict", true)
                .failureUrl("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery(
            "SELECT accountname, password, enabled FROM account WHERE accountname=?"
        );
        userDetailsManager.setAuthoritiesByUsernameQuery(
            "SELECT accountname, 'ROLE_USER' FROM account WHERE accountname = ?"
        );
        return userDetailsManager;
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        // WARNING: NoOpPasswordEncoder is deprecated and insecure
        // Consider migrating to BCryptPasswordEncoder for production use
        return NoOpPasswordEncoder.getInstance();
    }
}
