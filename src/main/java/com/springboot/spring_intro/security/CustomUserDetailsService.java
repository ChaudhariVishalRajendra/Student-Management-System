package com.springboot.spring_intro.security;

import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepo studentRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Student Not Found with Email: "+username));
        return User.builder()
                .username(student.getEmail())
                .password(student.getPassword())
                        .roles(student.getRole())
                        .build();
    }



}
