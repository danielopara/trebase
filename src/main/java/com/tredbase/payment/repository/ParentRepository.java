package com.tredbase.payment.repository;

import com.tredbase.payment.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {
    @Override
    List<Parent> findAll();
}
