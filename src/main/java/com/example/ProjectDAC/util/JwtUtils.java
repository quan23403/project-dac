package com.example.ProjectDAC.util;

import com.example.ProjectDAC.domain.User;
import com.example.ProjectDAC.domain.dto.ResUserDTO;
import com.example.ProjectDAC.error.IdInvalidException;
import com.example.ProjectDAC.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtUtils {
    private UserService userService;
    private JwtDecoder jwtDecoder;


    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;
    public JwtUtils(UserService userService, JwtDecoder jwtDecoder) {
        this.userService = userService;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(User user) throws JsonProcessingException, IdInvalidException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("hmquan")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("user", this.userService.convertUserToResUserDTO(user))
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

    public List<Long> getAnkenListFromToken(String token) throws IdInvalidException {
        try {
            // Remove the "Bearer " prefix from the token, if present
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                throw new IdInvalidException("Invalid token format.");
            }
            Jwt jwt = decodeToken(token);

            Map<String, Object> userMap = jwt.getClaim("user");

            // Check if the 'user' claim is present and has the expected 'ankenListId' field
            if (userMap != null && userMap.containsKey("ankenListId")) {
                Object ankenListIdObject = userMap.get("ankenListId");

                // Ensure the object is of type List<Long>
                if (ankenListIdObject instanceof List<?> ankenList) {
                    // Optionally, check if the list only contains Longs (this assumes the list contains the correct type)
                    for (Object item : ankenList) {
                        if (!(item instanceof Long)) {
                            throw new IdInvalidException("Invalid list item type in 'ankenListId'. Expected Long.");
                        }
                    }
                    // Safe casting to List<Long> after validation
                    return (List<Long>) ankenListIdObject;
                } else {
                    throw new IdInvalidException("'ankenListId' is not of type List<Long>.");
                }
            } else {
                throw new IdInvalidException("'user' claim or 'ankenListId' not found in the token.");
            }
        } catch (Exception e) {
            throw new IdInvalidException("Failed to process the token: " + e.getMessage());
        }
    }
}
