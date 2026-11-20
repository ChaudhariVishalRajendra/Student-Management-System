package com.springboot.spring_intro.config;

import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {
    private final StudentRepo repo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@gmail.com";
        if (repo.existsByEmail(adminEmail)){
            System.out.println("Admin already exist.");
            return;
        }

        Student admin = new Student();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail(adminEmail);
        admin.setPassword(
                encoder.encode("admin@100")
        );
        admin.setRole("ADMIN");
        repo.save(admin);
        System.out.println("=================================");
        System.out.println("Default Admin Created Successfully");
        System.out.println("Email: "+adminEmail);
        System.out.println("Password: admin@100");
        System.out.println("=================================");


    }
}
