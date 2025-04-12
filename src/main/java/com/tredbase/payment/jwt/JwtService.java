package com.tredbase.payment.jwt;

import com.tredbase.payment.entity.UserModel;
import com.tredbase.payment.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * JWT Utility Service:
 * - Generates and validates JWT tokens
 * - Extracts claims and metadata
 */
@Service
public class JwtService {
    @Value("${SECRET_KEY}")
    private String secretKey;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Converts Base64-encoded secret key into a JWT signing key.
     */
    private Key getSigningKey(){
        if(secretKey == null || secretKey.isEmpty()){
            throw new RuntimeException("secret key is not set");
        }
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    /**
     * Parses all claims from a token.
     */
    private Claims extractAllClaims(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            throw new RuntimeException("invalid token");
        }
    }

    /**
     * Extracts a specific claim
     */
    private <T>T extractClaims(String token, Function<Claims, T> claimsFunction){
        Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    /**
     * Generates a JWT token with custom claims and a subject (username).
     * Token expires in 1 day (86400000 ms).
     */
    private String generateToken(Map<String, Object> getDetails, String username){
        return Jwts.builder()
                .setClaims(getDetails)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (long) 86400000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the expiration date from a JWT token.
     */
    private Date extractExpirationDate(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * Checks if a token is expired.
     */
    private boolean isTokenExpired(String token){
        return !extractExpirationDate(token).before(new Date());
    }

    /**
     * Extracts the username (subject) from the token.
     */
    public String extractUsername(String token){
        return  extractClaims(token, Claims::getSubject);
    }


    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && isTokenExpired(token);
    }

    /**
     * Generates access token based on an authenticated user.
     */
    public String generateAccessToken(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<UserModel> user = userRepository.findByUsername(userDetails.getUsername());

        if(user.isEmpty()){
            throw new RuntimeException("user not found");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("usrename", user.get().getUsername());

        return generateToken(claims, userDetails.getUsername());
    }

    /**
     * Generates access token using a provided username.
     */
    public String generateAccessTokenByUsername(String username){
        Optional<UserModel> user = userRepository.findByUsername(username);

        if(user.isEmpty()){
            throw new RuntimeException("user not found");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("firstName", user.get().getUsername());

        return generateToken(claims, username);
    }
}
