package com.example.ProjectDAC.util;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
public class JwtUtils {
    private UserRepository userRepository;
    private JwtDecoder jwtDecoder;

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;
    public JwtUtils(UserRepository userRepository, JwtDecoder jwtDecoder) {
        this.userRepository = userRepository;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("hmquan")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
//                .claim("scope", buildScope(user))
                .claim("user", user)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


//    private String buildScope(User user) {
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        if(CollectionUtils.isEmpty(user.getRoles())) {
//            user.getRoles().forEach(stringJoiner::add);
//        }
//        return stringJoiner.toString();
//    }

    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token); // Giải mã token
    }

    public String getUsernameFromToken(String token) {
        Jwt jwt = decodeToken(token);
        return jwt.getClaimAsString("sub"); // Trích xuất thông tin, ví dụ là username
    }
}
