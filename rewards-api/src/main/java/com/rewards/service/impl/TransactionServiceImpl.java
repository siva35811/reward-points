package com.rewards.service.impl;

import com.rewards.dto.TransactionRequestDTO;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.repository.CustomerRepository;
import com.rewards.repository.TransactionRepository;
import com.rewards.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository txRepo;
    private final CustomerRepository customerRepo;

    public TransactionServiceImpl(TransactionRepository txRepo, CustomerRepository customerRepo) {
        this.txRepo = txRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public Transaction createTransaction(TransactionRequestDTO req) {
        Customer customer = null;

        // 1. Try by ID
        if ( req.getCustomerId( ) != null ) {
            customer = customerRepo.findById( req.getCustomerId( ) ).orElse( null );
        }

        // 2. Try by Email
        if ( customer == null && req.getCustomerEmail( ) != null ) {
            customer = customerRepo.findByCustomerEmail( req.getCustomerEmail( ) ).orElse( null );
        }
        // 3. Throw if not found
        if ( customer == null ) {
            throw new NoSuchElementException( "Customer not found. Please register first." );
        }

        // 4. Save transaction
        Transaction tx = new Transaction( req.getAmount( ), req.getTransactionDate( ), customer );
        return txRepo.save( tx );
    }
}
