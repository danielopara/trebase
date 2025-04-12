package com.tredbase.payment.service.payment;

import com.tredbase.payment.dto.PaymentLedgerDto;
import com.tredbase.payment.entity.Parent;
import com.tredbase.payment.entity.PaymentLedger;
import com.tredbase.payment.entity.Student;
import com.tredbase.payment.repository.ParentRepository;
import com.tredbase.payment.repository.PaymentLedgerRepository;
import com.tredbase.payment.repository.StudentRepository;
import com.tredbase.payment.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentService implements PaymentInterface {

    // dynamic amount
    @Value("${DYNAMIC_AMOUNT}")
    private BigDecimal dynamicAmount;

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final PaymentLedgerRepository paymentLedgerRepository;
    public PaymentService(StudentRepository studentRepository, ParentRepository parentRepository, PaymentLedgerRepository paymentLedgerRepository) {
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.paymentLedgerRepository = paymentLedgerRepository;
    }


    /**
     * Processes a payment transaction. Handles both single-parent and shared-parent scenarios.
     *
     * - If the student has only one parent, only that parent's balance is updated.
     * - If the student has multiple parents, all associated parents receive the payment amount equally.
     * - The student balance is always increased by the full amount.
     *
     * @param parentId The parent initiating the payment
     * @param studentId The student receiving the payment
     * @param amount The amount to be paid
     * @return A BaseResponse object containing success/failure message and payload
     */
    @Override
    @Transactional
    public BaseResponse makePayment(UUID parentId, UUID studentId, BigDecimal amount) {
        log.info("Initiating payment from parent {} to student {} of amount {}", parentId, studentId, amount);
        try{
            Optional<Parent> checkParent = parentRepository.findById(parentId);
            Optional<Student> checkStudent = studentRepository.findById(studentId);

            // Validate both parent and student exist
            if(checkParent.isEmpty() || checkStudent.isEmpty()){
                log.warn("Parent or student not found: parent={}, student={}", parentId, studentId);
                return BaseResponse.createErrorResponse("student or parent not found", null);
            }

            Parent parent = checkParent.get();
            Student student = checkStudent.get();

            Set<Parent> studentParents = student.getParents();

            // check if parent is linked to the student
            if (!studentParents.contains(parent)) {
                log.warn("Parent {} is not linked to student {}", parentId, studentId);
                return BaseResponse.createErrorResponse("Parent is not linked to this student", null);
            }

            BigDecimal adjustedAmount = amount.multiply(  BigDecimal.ONE.add(dynamicAmount));
            log.debug("Adjusted amount after applying dynamic rate {} is {}", dynamicAmount, adjustedAmount);


            // If only one parent is linked to the student, update that parent only
            if(studentParents.size() == 1){
                if (parent.getBalance().compareTo( adjustedAmount) < 0){
                    log.warn("Insufficient balance for parent {}. Required: {}, Available: {}", parentId, adjustedAmount, parent.getBalance());
                    return BaseResponse.createErrorResponse("Insufficient balance", adjustedAmount);
                }
                parent.setBalance(parent.getBalance().subtract(adjustedAmount));
                parentRepository.save(parent);
                log.info("Balance updated for single parent {}: new balance {}", parentId, parent.getBalance());

                PaymentLedger paymentLedger = new PaymentLedger(parentId, studentId, adjustedAmount, LocalDateTime.now(), "SUCCESS");
                paymentLedgerRepository.save(paymentLedger);
            }else{
                // If student is linked to multiple parents
                BigDecimal splitAmount = adjustedAmount.divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN);

                // Check if all parents have enough balance
                List<Parent> insufficientParents = studentParents.stream()
                        .filter(parentBalance -> parent.getBalance().compareTo(splitAmount) < 0)
                        .collect(Collectors.toList());


                if(!insufficientParents.isEmpty()){
                    log.warn("Some parents have insufficient balance for shared payment: {}", insufficientParents.stream().map(Parent::getId).toList());
                    return BaseResponse.createErrorResponse("One or more parents do not have sufficient amount", insufficientParents);
                }
                for(Parent p : studentParents){
//                    Double splitAmount = adjustedAmount/2;
                    //deducte the amount
                    p.setBalance(p.getBalance().subtract(splitAmount));
                    parentRepository.save(p);

                    PaymentLedger paymentLedger = new PaymentLedger(p.getId(), studentId, splitAmount, LocalDateTime.now(), "SUCCESS");
                    paymentLedgerRepository.save(paymentLedger);
                    log.info("Deducted {} from parent {}. New balance: {}", splitAmount, p.getId(), p.getBalance());
                }
            }

            student.setBalance(student.getBalance().add(adjustedAmount));
            studentRepository.save(student);

            PaymentLedger finalLedgerEntry = new PaymentLedger(parentId, studentId, adjustedAmount, LocalDateTime.now(), "SUCCESS");
            paymentLedgerRepository.save(finalLedgerEntry);
            log.info("Final payment ledger entry saved for parent {} to student {} of amount {}", parentId, studentId, adjustedAmount);

            return BaseResponse.createSuccessResponse("Payment processed", Map.of(
                    "amount", adjustedAmount,
                    "studentBalance", student.getBalance()
            ));
        }catch (Exception e){
            log.error("Error processing payment: {}", e.getMessage(), e);
            return BaseResponse.createErrorResponse("error", e);
        }
    }

    @Override
    public BaseResponse getPaymentLedger() {
        try{
            List<PaymentLedger> allPayment = paymentLedgerRepository.findAll();
            List<PaymentLedgerDto> payments  = allPayment.stream().map(payment ->{
                PaymentLedgerDto paymentLedgerDto = new PaymentLedgerDto();
                paymentLedgerDto.setStudentId(payment.getStudentId());
                paymentLedgerDto.setAmount(payment.getAmount());
                paymentLedgerDto.setStatus(payment.getStatus());
                paymentLedgerDto.setParentId(payment.getParentId());
                paymentLedgerDto.setPaymentDate(payment.getPaymentDate());

                return paymentLedgerDto;
            }).toList();

            return BaseResponse.createSuccessResponse("success", payments);
        } catch(Exception e){
            return BaseResponse.createErrorResponse("error", e);
        }
    }
}
