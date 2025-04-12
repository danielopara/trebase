package com.tredbase.payment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;


/**
 * Entity representing a Parent in the payment system.
 * A parent can have a balance and is associated with multiple students (many-to-many).
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="parent")
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private BigDecimal balance;

    /**
     * List of students associated with this parent.
     * Many-to-Many relationship.
     * The owning side is defined in the Student entity.
     * FetchType.LAZY ensures data is only loaded when accessed.
     */
    @ManyToMany(mappedBy = "parents", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Student> students;

    //Getters & Setters

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastname(){
        return lastName;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal amount){
        this.balance = amount;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
