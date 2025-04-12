package com.tredbase.payment;

import com.tredbase.payment.entity.Parent;
import com.tredbase.payment.entity.PaymentLedger;
import com.tredbase.payment.entity.Student;
import com.tredbase.payment.repository.ParentRepository;
import com.tredbase.payment.repository.PaymentLedgerRepository;
import com.tredbase.payment.repository.StudentRepository;
import com.tredbase.payment.response.BaseResponse;
import com.tredbase.payment.service.payment.PaymentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class PaymentTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private PaymentLedgerRepository paymentLedgerRepository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID parentId;
    private UUID studentId;
    private UUID secondParentId;
    private Parent parent;
    private Parent secondParent;
    private Student student;
    private BigDecimal dynamicAmount;

    @BeforeEach
    public void setUp() {
        parentId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        secondParentId = UUID.randomUUID();

        parent = new Parent();
        parent.setId(parentId);
        parent.setBalance(new BigDecimal("1000.00"));

        secondParent = new Parent();
        secondParent.setId(secondParentId);
        secondParent.setBalance(new BigDecimal("1000.00"));

        student = new Student();
        student.setId(studentId);
        student.setBalance(new BigDecimal("0.00"));

        // Set dynamic amount rate
        dynamicAmount = new BigDecimal("0.05");
        ReflectionTestUtils.setField(paymentService, "dynamicAmount", dynamicAmount);
    }

    @Test
    public void testMakePayment_ParentOrStudentNotFound() {
        // Given
        when(parentRepository.findById(parentId)).thenReturn(Optional.empty());
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // When
        BaseResponse response = paymentService.makePayment(parentId, studentId, new BigDecimal("100.00"));

        // Then
        assertEquals(400, response.getStatusCode());
        assertEquals("student or parent not found", response.getMessage());
        assertNull(response.getData());

        // Reset and test student not found
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        response = paymentService.makePayment(parentId, studentId, new BigDecimal("100.00"));

        assertEquals(400, response.getStatusCode());
        assertEquals("student or parent not found", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    public void testMakePayment_ParentNotLinkedToStudent() {
        // Given
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Set<Parent> parents = new HashSet<>();
        // Intentionally not adding the parent to simulate unlinked parent
        student.setParents(parents);

        // When
        BaseResponse response = paymentService.makePayment(parentId, studentId, new BigDecimal("100.00"));

        // Then
        assertEquals(400,response.getStatusCode());
        assertEquals("Parent is not linked to this student", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    public void testMakePayment_SingleParent_InsufficientBalance() {
        // Given
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Set<Parent> parents = new HashSet<>();
        parents.add(parent);
        student.setParents(parents);

        BigDecimal amount = new BigDecimal("2000.00");  // More than parent's balance
        BigDecimal adjustedAmount = amount.multiply(BigDecimal.ONE.add(dynamicAmount));

        // When
        BaseResponse response = paymentService.makePayment(parentId, studentId, amount);

        // Then
        assertEquals(400,response.getStatusCode());
        assertEquals("Insufficient balance", response.getMessage());
        assertEquals(adjustedAmount, response.getData());
    }

    @Test
    public void testMakePayment_SingleParent_SuccessfulPayment() {
        // Given
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Set<Parent> parents = new HashSet<>();
        parents.add(parent);
        student.setParents(parents);

        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal adjustedAmount = amount.multiply(BigDecimal.ONE.add(dynamicAmount));

        when(parentRepository.save(any(Parent.class))).thenReturn(parent);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(paymentLedgerRepository.save(any(PaymentLedger.class))).thenReturn(new PaymentLedger());

        // When
        BaseResponse response = paymentService.makePayment(parentId, studentId, amount);

        // Then
        assertEquals(200,response.getStatusCode());
        assertEquals("Payment processed", response.getMessage());

        Map<String, BigDecimal> payload = (Map<String, BigDecimal>) response.getData();
        assertEquals(adjustedAmount, payload.get("amount"));
        assertEquals(adjustedAmount, payload.get("studentBalance"));

        // Verify parent balance deduction
        verify(parentRepository).save(parent);
        assertEquals(new BigDecimal("1000.00").subtract(adjustedAmount), parent.getBalance());

        // Verify student balance increase
        verify(studentRepository).save(student);
        assertEquals(adjustedAmount, student.getBalance());

        // Verify payment ledger entries (2 entries are created)
        verify(paymentLedgerRepository, times(2)).save(any(PaymentLedger.class));
    }

    @Test
    public void testMakePayment_MultipleParents_InsufficientBalance() {
        // Given
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // Second parent with insufficient balance
        secondParent.setBalance(new BigDecimal("10.00"));

        Set<Parent> parents = new HashSet<>();
        parents.add(parent);
        parents.add(secondParent);
        student.setParents(parents);

        BigDecimal amount = new BigDecimal("100.00");

        // When
        BaseResponse response = paymentService.makePayment(parentId, studentId, amount);

        // Then
        assertEquals(400, response.getStatusCode());

        assertEquals("One or more parents do not have sufficient amount", response.getMessage());
        assertTrue(response.getData() instanceof List);
    }

    @Test
    public void testMakePayment_MultipleParents_SuccessfulPayment() {
        // Given
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Set<Parent> parents = new HashSet<>();
        parents.add(parent);
        parents.add(secondParent);
        student.setParents(parents);

        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal adjustedAmount = amount.multiply(BigDecimal.ONE.add(dynamicAmount));
        BigDecimal splitAmount = adjustedAmount.divide(new BigDecimal("2"), RoundingMode.HALF_EVEN);

        when(parentRepository.save(any(Parent.class))).thenReturn(parent).thenReturn(secondParent);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(paymentLedgerRepository.save(any(PaymentLedger.class))).thenReturn(new PaymentLedger());

        // When
        BaseResponse response = paymentService.makePayment(parentId, studentId, amount);

        // Then
        assertEquals(200,response.getStatusCode());
        assertEquals("Payment processed", response.getMessage());

        Map<String, BigDecimal> payload = (Map<String, BigDecimal>) response.getData();
        assertEquals(adjustedAmount, payload.get("amount"));
        assertEquals(adjustedAmount, payload.get("studentBalance"));

        // Verify both parents' balances were updated
        verify(parentRepository, times(2)).save(any(Parent.class));

        // Verify student balance increase
        verify(studentRepository).save(student);
        assertEquals(adjustedAmount, student.getBalance());

        // Verify payment ledger entries (3 entries are created - one for each parent and a final summary)
        verify(paymentLedgerRepository, times(3)).save(any(PaymentLedger.class));
    }

    @Test
    public void testMakePayment_ExceptionHandling() {
        // Given
        when(parentRepository.findById(parentId)).thenThrow(new RuntimeException("Database error"));

        // When
        BaseResponse response = paymentService.makePayment(parentId, studentId, new BigDecimal("100.00"));

        // Then
        assertEquals(400,response.getStatusCode());
        assertEquals("error", response.getMessage());
        assertTrue(response.getData() instanceof Exception);
    }

    @Test
    public void testMakePayment_ValidatesCorrectBalanceChanges() {
        // Given
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Set<Parent> parents = new HashSet<>();
        parents.add(parent);
        student.setParents(parents);

        BigDecimal initialParentBalance = parent.getBalance();
        BigDecimal initialStudentBalance = student.getBalance();
        BigDecimal paymentAmount = new BigDecimal("100.00");
        BigDecimal adjustedAmount = paymentAmount.multiply(BigDecimal.ONE.add(dynamicAmount));

        when(parentRepository.save(any(Parent.class))).thenReturn(parent);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(paymentLedgerRepository.save(any(PaymentLedger.class))).thenReturn(new PaymentLedger());

        // When
        paymentService.makePayment(parentId, studentId, paymentAmount);

        // Then
        // Verify exact balance changes
        assertEquals(initialParentBalance.subtract(adjustedAmount), parent.getBalance());
        assertEquals(initialStudentBalance.add(adjustedAmount), student.getBalance());
    }
}