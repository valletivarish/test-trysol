package com.monocept.myapp.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.monocept.myapp.entity.Admin;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.repository.AdminRepository;
import com.monocept.myapp.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private AdminRepository adminRepository;

    public CustomUserDetailsService(UserRepository userRepository,AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository=adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user.getAdmin() == null && user.getEmployee() == null) {
            throw new UsernameNotFoundException("User not found with username or email: " + email);
        }

        // Determine if it's an admin or user
        UserDetails userDetails;
        if (user.getAdmin() != null) {
            Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        } else {
            Set<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
            userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        }

        return userDetails;
    }
}

