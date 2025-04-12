package com.tredbase.payment.controller.auth;

import com.tredbase.payment.dto.LoginDto;
import com.tredbase.payment.response.BaseResponse;
import com.tredbase.payment.service.user.implementation.UserService;
import com.tredbase.payment.utils.ResponseHandlers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user authentication
 */
@RestController
@RequestMapping("/api/v1/payment/auth")
@Tag(name="Authentication Controller")
public class AuthController {

    private final UserService userService;
    private final ResponseHandlers responseHandlers;

    public AuthController(UserService userService, ResponseHandlers responseHandlers) {
        this.userService = userService;
        this.responseHandlers = responseHandlers;
    }

    /**
     * Endpoint for user login. Authenticates user credentials and returns a JWT token.
     *
     * @param loginDto the login credentials (username and password)
     * @return a ResponseEntity containing either the JWT token or an error message
     */
    @Operation(summary = "User Login", description = "Authenticate user and generate JWT token for valid credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in with JWT token"),
            @ApiResponse(responseCode = "400", description = "Invalid username or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "Login credentials including username and password")
            @RequestBody LoginDto loginDto) {

        // Call the user service to handle login
        BaseResponse response = userService.accountLogin(loginDto);

        return responseHandlers.handleResponse(response);
    }
}
