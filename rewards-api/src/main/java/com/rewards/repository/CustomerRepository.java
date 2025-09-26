package com.rewards.repository;

import com.rewards.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerEmail(String email);

    boolean existsByCustomerEmail(String email);
}
