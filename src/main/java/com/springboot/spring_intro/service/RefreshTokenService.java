package com.springboot.spring_intro.service;

import com.springboot.spring_intro.entity.RefreshToken;
import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.exception.InvalidRefreshTokenException;
import com.springboot.spring_intro.repository.RefreshTokenRepo;
import com.springboot.spring_intro.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StudentRepo studentRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    @Value("${refresh.expiration-days}")
    private long refreshTokenExpiryDays;

    public RefreshToken createRefreshToken(String email){
        Student student = studentRepo.findByEmail(email)
                .orElseThrow(() -> new InvalidRefreshTokenException("Student Not Found with Email: "+email));
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(refreshTokenExpiryDays));
        refreshToken.setStudent(student);
        refreshToken.setRevoked(false);

        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid REfresh Token"));

        if (refreshToken.isRevoked()){
            throw new InvalidRefreshTokenException("Refresh Token has been Revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new InvalidRefreshTokenException("Refresh token has Expired");
        }

        return refreshToken;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldRefreshToken){

        oldRefreshToken.setRevoked(true);
        refreshTokenRepo.save(oldRefreshToken);

        return createRefreshToken(
                oldRefreshToken.getStudent().getEmail());
    }

    @Transactional
    public void revokeRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid Refresh Token"));
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }

}
