package com.arenalocastro.videomanagement;

import com.arenalocastro.videomanagement.models.User;
import com.arenalocastro.videomanagement.repositories.ReactiveUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecUserDetailsService implements UserDetailsService {
    @Autowired
    ReactiveUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username).block();
        if(user == null)
            throw new UsernameNotFoundException("User not found");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                getAuth(user.getRoles())
        );
    }

    @Bean
    private BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }


    private List<GrantedAuthority> getAuth(List<String> roles){
        List<GrantedAuthority> authorities = new ArrayList<>();

        for(final String role : roles)
            authorities.add(new SimpleGrantedAuthority(role));

        return authorities;
    }
}
