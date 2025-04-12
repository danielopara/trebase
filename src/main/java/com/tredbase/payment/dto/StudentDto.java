package com.tredbase.payment.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StudentDto {
    private UUID id;
    private String first_name;
    private String last_name;
    private BigDecimal balance;

    public List<Map<String, Object>> getParent() {
        return parent;
    }

    public void setParent(List<Map<String, Object>> parent) {
        this.parent = parent;
    }

    private List<Map<String, Object>> parent;

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
