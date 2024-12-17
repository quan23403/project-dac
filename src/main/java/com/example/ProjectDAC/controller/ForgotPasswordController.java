package com.example.ProjectDAC.controller;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.domain.dto.ForgotPassword;
import com.example.ProjectDAC.domain.dto.MailBody;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.repository.ForgetPasswordRepository;
import com.example.ProjectDAC.request.ChangePassword;
import com.example.ProjectDAC.service.EmailService;
import com.example.ProjectDAC.service.UserService;
import jakarta.persistence.Id;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.emitter.Emitable;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final EmailService emailService;
    private final ForgetPasswordRepository forgetPasswordRepository;
    public ForgotPasswordController(UserService userService, EmailService emailService,
                                    ForgetPasswordRepository forgetPasswordRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.forgetPasswordRepository = forgetPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) throws IdInvalidException {
        User user = this.userService.getUserByEmail(email);
        if(user == null) {
            throw new IdInvalidException("Email does not exist !!");
        }

        ForgotPassword fp = this.forgetPasswordRepository.findByUser(user);
        if(fp != null)  {
            this.forgetPasswordRepository.deleteById(fp.getFpid());
        }
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forget Password request: " + otp)
                .subject("OTP for Forgot Password request")
                .build();
        fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .active(false)
                .build();
        emailService.sendSimpleMessage(mailBody);
        forgetPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification!");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable int otp, @PathVariable String email) throws IdInvalidException {
        User user = this.userService.getUserByEmail(email);
        if(user == null) {
            throw new IdInvalidException("Email does not exist !!");
        }
        ForgotPassword fp = forgetPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));
        if(fp.getExpirationTime().before(Date.from(Instant.now())))  {
            forgetPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }
        fp.setActive(true);
        forgetPasswordRepository.save(fp);
        return ResponseEntity.ok("OTP verified!");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email) throws IdInvalidException {
        User user = this.userService.getUserByEmail(email);
        ForgotPassword fp = forgetPasswordRepository.findByOtpAndUser(changePassword.otp(), user)
                .orElseThrow(() -> new IdInvalidException("Invalid OTP for email: " + email));
        if(!fp.isActive()) {
            return new ResponseEntity<>("Wrong OTP", HttpStatus.EXPECTATION_FAILED);
        }
        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }
        String encodedPassword = passwordEncoder.encode(changePassword.password());
        user.setPassword(encodedPassword);
        userService.resetPassword(user);
        forgetPasswordRepository.deleteById(fp.getFpid());
        return ResponseEntity.ok("Password has been changed!");
    }
    private int otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
