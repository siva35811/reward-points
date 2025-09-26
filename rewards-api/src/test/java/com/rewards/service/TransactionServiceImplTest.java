package com.rewards.service;

import com.rewards.dto.TransactionRequestDTO;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.repository.CustomerRepository;
import com.rewards.repository.TransactionRepository;
import com.rewards.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {

    private TransactionRepository txRepo;
    private CustomerRepository customerRepo;
    private TransactionServiceImpl service;

    @BeforeEach
    void setUp() {
        txRepo = mock(TransactionRepository.class);
        customerRepo = mock(CustomerRepository.class);
        service = new TransactionServiceImpl(txRepo, customerRepo);
    }

    @Test
    void createTransaction_withCustomerId_shouldSaveTransaction() {

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("test");

        TransactionRequestDTO req = new TransactionRequestDTO();
        req.setCustomerId(1L);
        req.setAmount(BigDecimal.valueOf(100));
        req.setTransactionDate(LocalDate.now());

        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(txRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Transaction result = service.createTransaction(req);


        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        verify(txRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_withCustomerEmail_shouldSaveTransaction() {

        Customer customer = new Customer();
        customer.setId(2L);
        customer.setCustomerName("test");
        customer.setCustomerEmail("test@example.com");

        TransactionRequestDTO req = new TransactionRequestDTO();
        req.setCustomerEmail("test@example.com");
        req.setAmount(BigDecimal.valueOf(200));
        req.setTransactionDate(LocalDate.now());

        when(customerRepo.findByCustomerEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(txRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));


        Transaction result = service.createTransaction(req);


        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(BigDecimal.valueOf(200), result.getAmount());
        verify(txRepo, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_customerNotFound_shouldThrowException() {

        TransactionRequestDTO req = new TransactionRequestDTO();
        req.setCustomerId(99L);
        req.setAmount(BigDecimal.valueOf(50));
        req.setTransactionDate(LocalDate.now());

        when(customerRepo.findById(99L)).thenReturn(Optional.empty());


        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.createTransaction(req)
        );
        assertEquals("Customer not found. Please register first.", ex.getMessage());
        verify(txRepo, never()).save(any(Transaction.class));
    }


}
