package com.tredbase.payment.repository;

import com.tredbase.payment.entity.PaymentLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentLedgerRepository extends JpaRepository<PaymentLedger, Long> {
}
