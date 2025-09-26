package com.rewards.service.impl;

import com.rewards.model.Customer;
import com.rewards.repository.CustomerRepository;
import com.rewards.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        // check if email already exists
        if ( customerRepository.existsByCustomerEmail( customer.getCustomerEmail( ) ) ) {
            throw new IllegalArgumentException( "Customer with email already exists" );
        }
        return customerRepository.save( customer );
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll( );
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById( id )
                .orElseThrow( () -> new NoSuchElementException( "Customer not found with id " + id ) );
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByCustomerEmail( email )
                .orElseThrow( () -> new NoSuchElementException( "Customer not found with email " + email ) );
    }
}
