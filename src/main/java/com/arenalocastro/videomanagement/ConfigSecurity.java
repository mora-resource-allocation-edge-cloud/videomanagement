package com.arenalocastro.videomanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    SecUserDetailsService secUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(secUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/.~~spring-boot!~/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers(HttpMethod.GET,"/ping").permitAll()
                .antMatchers(HttpMethod.GET, "/listHeaders").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers(HttpMethod.GET, "/videos").permitAll()
                .antMatchers(HttpMethod.GET, "/videos/{id}").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }
}
