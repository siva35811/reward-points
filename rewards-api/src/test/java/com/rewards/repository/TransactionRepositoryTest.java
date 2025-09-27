package com.rewards.repository;

import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest 
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class TransactionRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Customer customer;
    private Transaction transaction;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        // Create and save a customer
        customer = new Customer();
        customer.setCustomerName("Test");
        customer.setCustomerEmail("test@example.com");
        customer.setCustomerContactNumber("1234567890");
        customer = customerRepository.save(customer);

        // Create and save a transaction for this customer
        transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCustomer(customer);
        transaction = transactionRepository.save(transaction);
    }

    @Test
    void testFindByCustomerIdAndDateBetween() {
        LocalDate from = LocalDate.now().minusMonths(2);
        LocalDate to = LocalDate.now();

        List<Transaction> transactions =
                transactionRepository.findByCustomerIdAndTransactionDateBetween(customer.getId(), from, to);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getAmount()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void testFindNoTransactionsInRange() {
        LocalDate from = LocalDate.now().minusYears(1);
        LocalDate to = LocalDate.now().minusMonths(11);

        List<Transaction> transactions =
                transactionRepository.findByCustomerIdAndTransactionDateBetween(customer.getId(), from, to);

        assertThat(transactions).isEmpty();
    }
}
