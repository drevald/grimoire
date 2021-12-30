package org.helico;

import org.helico.dao.AccountDAO;
import org.helico.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter  {

	@Autowired
	private DataSource dataSource;

    @Autowired
    private AccountDAO accountDAO;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/hello").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/*").access("hasRole('USER')")
            .and()
            .formLogin()
            .loginProcessingUrl("/login")
            .successForwardUrl("/");
    };

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        PasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println("!!! ADMIN = " + new BCryptPasswordEncoder().encode("admin"));
//        auth
//            .userDetailsService(new UserDetailsService() {
//                @Override
//                public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//                        return accountDAO.findAccount(username);
//                    }
//                }
//            )
//            .passwordEncoder(encoder);
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser("admin")
            .password("{noop}admin")
            .roles("USER");
    }

}
