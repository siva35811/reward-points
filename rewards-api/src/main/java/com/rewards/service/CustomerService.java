package com.rewards.service;

import com.rewards.model.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);

    List<Customer> getAllCustomers();

    Customer getCustomerById(Long id);

    Customer getCustomerByEmail(String email);
}
