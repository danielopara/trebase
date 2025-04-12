package com.tredbase.payment.service.payment;

import com.tredbase.payment.response.BaseResponse;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * PaymentInterface defines the contract for processing payments
 * between parents and students in the system.
 */
public interface PaymentInterface {
    /**
     * Initiates a payment from a parent to a student based on the request details.
     *
     * The request body containing parentId, studentId, and amount
     * @return A response indicating the result of the payment operation
     */
    BaseResponse makePayment(UUID parentId, UUID studentId, BigDecimal amount );
}
