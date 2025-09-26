package com.rewards.repository;

import com.rewards.model.Customer;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setup() {
        customer = new Customer();
        customer.setCustomerName("test");
        customer.setCustomerEmail("test@example.com");
        customer.setCustomerContactNumber("9876543210");
        customer = customerRepository.save(customer);
    }


    @Test
    void testSaveCustomer_valid() {

        Customer saved = customerRepository.save(customer);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCustomerName()).isEqualTo("test");
        assertThat(saved.getCustomerEmail()).isEqualTo("test@example.com");
        assertThat(saved.getCustomerContactNumber()).isEqualTo("9876543210");
    }

    @Test
    void testSaveAndFindById() {


        Optional<Customer> found = customerRepository.findById(customer.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("test");
        assertThat(found.get().getCustomerEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testSaveDuplicateEmail_shouldFail() {

        Customer duplicate = new Customer("test", "test@example.com", "1234567890");

        assertThatThrownBy(() -> customerRepository.saveAndFlush(duplicate))
                .isInstanceOf(Exception.class); 
    }




    @Test
    void testFindByIdNotFound() {
        Optional<Customer> found = customerRepository.findById(999L);
        assertThat(found).isNotPresent();
    }

    @Test
    void testSaveCustomer_invalid_shouldThrowConstraintViolation() {
        Customer invalidCustomer = new Customer(); 

        assertThatThrownBy(() -> customerRepository.saveAndFlush(invalidCustomer))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("must not be blank")
                .hasMessageContaining("must not be null");
    }
}
