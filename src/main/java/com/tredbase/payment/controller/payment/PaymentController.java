package com.tredbase.payment.controller.payment;

import com.tredbase.payment.dto.PaymentDto;
import com.tredbase.payment.response.BaseResponse;
import com.tredbase.payment.service.payment.PaymentService;
import com.tredbase.payment.utils.ResponseHandlers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for handling payment-related operations such as making payments.
 */
@RestController
@RequestMapping("/api/v1/payment")
@Tag(name = "Payment Controller")
public class PaymentController {

    private final PaymentService paymentService;
    private final ResponseHandlers responseHandlers;

    public PaymentController(PaymentService paymentService, ResponseHandlers responseHandlers) {
        this.paymentService = paymentService;
        this.responseHandlers = responseHandlers;
    }

    /**
     * Endpoint for processing a payment.
     *
     * @param paymentDto the DTO containing parent ID, student ID, and payment amount
     * @return a ResponseEntity containing the payment processing result
     */
    @Operation(summary = "Make payment", description = "Process a payment for a specific parent and student.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment successfully processed"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/makePayment")
    public ResponseEntity<?> makePayment(
            @Parameter(description = "Payment details including parent ID, student ID, and amount to be paid")
            @RequestBody PaymentDto paymentDto) {

        // Process the payment using the service
        BaseResponse response = paymentService.makePayment(
                paymentDto.getParentId(),
                paymentDto.getStudentId(),
                paymentDto.getAmount()
        );

        return responseHandlers.handleResponse(response);
    }
}
