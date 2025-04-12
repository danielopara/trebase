package com.tredbase.payment.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParentDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private BigDecimal balance;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Map<String, Object>> getStudents() {
        return students;
    }

    public void setStudents(List<Map<String, Object>> students) {
        this.students = students;
    }

    private List<Map<String, Object>> students;

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
