package com.example.sellcourse.service;

import com.example.sellcourse.dto.response.auth.AuthenticationResponse;
import com.example.sellcourse.dto.response.auth.IntrospectResponse;
import com.example.sellcourse.dto.resquest.auth.AuthenticationRequest;
import com.example.sellcourse.dto.resquest.auth.IntrospectRequest;
import com.example.sellcourse.dto.resquest.auth.LogoutRequest;
import com.example.sellcourse.dto.resquest.auth.RefreshTokenRequest;
import com.example.sellcourse.entities.InvalidatedToken;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.exception.ExpiredTokenException;
import com.example.sellcourse.exception.InvalidTokenException;
import com.example.sellcourse.repository.InvalidatedTokenRepository;
import com.example.sellcourse.repository.user.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;


    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        var user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        String role = user.getRole().getRoleName().toString();

        return AuthenticationResponse.builder()
                .token(token)
                .role(role)
                .authenticated(true)
                .build();
    }

    public void logout(LogoutRequest request) {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            if (!invalidatedTokenRepository.existsById(jit)) {
                InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
                invalidatedTokenRepository.save(invalidatedToken);
            } else {
                log.info("Token has already been invalidated");
            }

        } catch (AppException | ParseException | JOSEException exception) {
            log.info("Token already expired or invalid");
        }
    }
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean isValid = true;
        String scope =  null;
        try {
            SignedJWT signedJWT = verifyToken(token, false);
            scope = (String) signedJWT.getJWTClaimsSet().getClaim("scope");
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .scope(scope)
                .build();
    }
    public SignedJWT verifyToken(String token, boolean isRefresh)
            throws ParseException ,JOSEException {
        if (token == null || token.trim().isEmpty()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.DAYS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        if (expiryTime.before(new Date())) {
            throw new ExpiredTokenException();
        }

        var verified = signedJWT.verify(verifier);
        if (!verified) throw new InvalidTokenException();

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new InvalidTokenException();

        return signedJWT;
    }

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("learning.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request){
        try {
            var signJWT = verifyToken(request.getToken(), true);

            var jit = signJWT.getJWTClaimsSet().getJWTID();
            var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
            invalidatedTokenRepository.save(invalidatedToken);

            var email = signJWT.getJWTClaimsSet().getSubject();
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

            var token = generateToken(user);

            return AuthenticationResponse.builder()
                    .token(token)
                    .authenticated(true)
                    .build();
        }
        catch (ParseException | JOSEException e){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Optional.ofNullable(user.getRole()).ifPresent(role -> stringJoiner.add(role.getRoleName().toString()));
        return stringJoiner.toString();
    }

}
