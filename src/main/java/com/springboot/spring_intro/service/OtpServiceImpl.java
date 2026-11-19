package com.springboot.spring_intro.service;

import com.springboot.spring_intro.entity.OtpVerification;
import com.springboot.spring_intro.repository.OtpVerificatonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificatonRepo otpVerificatonRepo;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder encoder;
    private static final int MAX_ATTEMPTS = 3;

    @Override
    public void generateAndSendOtp(String email) {
        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtp(encoder.encode(otp));
        verification.setExpireAt(
                LocalDateTime.now().plusMinutes(5)
        );
        verification.setVerified(false);
        verification.setAttempts(0);

        otpVerificatonRepo.save(verification);
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public void verifyOtp(String email, String otp) {
        OtpVerification verification = otpVerificatonRepo.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));
        if (verification.isVerified()){
            throw new RuntimeException("OTP already Verified");
        }

        if (LocalDateTime.now().isAfter(verification.getExpireAt())){
            throw new RuntimeException("OTP expired");
        }

        if (verification.getAttempts()>=MAX_ATTEMPTS){
            throw new RuntimeException("Maximum OTP attempts exceed's");
        }

        if (!encoder.matches(otp, verification.getOtp())){
            verification.setAttempts(verification.getAttempts()+1);
            otpVerificatonRepo.save(verification);
            if (verification.getAttempts()>=MAX_ATTEMPTS){
                throw new RuntimeException("Maximum OTP attempts exceed's");
            }
            throw new RuntimeException("Invalid OTP");
        }

        verification.setVerified(true);
        otpVerificatonRepo.save(verification);
    }
}
