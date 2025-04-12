package com.tredbase.payment.service.user.implementation;

import com.tredbase.payment.dto.LoginDto;
import com.tredbase.payment.entity.UserModel;
import com.tredbase.payment.jwt.JwtService;
import com.tredbase.payment.repository.UserRepository;
import com.tredbase.payment.response.BaseResponse;
import com.tredbase.payment.service.user.UserInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service

/**
 * Service class for handling user authentication operations
 */
public class UserService implements UserInterface {
    private  final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService( JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    /**
     * Handles user login by authenticating the credentials and generating a JWT token.
     *
     * @param loginDto the login data transfer object containing username and password
     * @return a BaseResponse containing either the JWT token or an error message
     */

    @Override
    public BaseResponse accountLogin(LoginDto loginDto) {
        try{
            if(loginDto == null){
                return BaseResponse.createErrorResponse("no request body", null);
            }

            // Authenticate the user based on the provided username and password
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
            );
            // If authentication is unsuccessful, return an error message
            if (authentication == null || !authentication.isAuthenticated()){
                return BaseResponse.createErrorResponse(
                    "username or password is incorrect",
                        null
                );
            }
            // Generate JWT token for the authenticated user
            String token = jwtService.generateAccessToken(authentication);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);

            return BaseResponse.createSuccessResponse(
                    "user logged in successfully",
                    response
            );

        }catch (Exception e){
            return BaseResponse.createErrorResponse("INTERNAL SERVER ERROR", null);
        }
    }
}
