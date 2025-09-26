package com.rewards.service;

import com.rewards.model.Customer;
import com.rewards.repository.CustomerRepository;
import com.rewards.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {
    private CustomerRepository customerRepository;
    private CustomerServiceImpl service;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        service = new CustomerServiceImpl(customerRepository);
    }

    @Test
    void createCustomer_whenEmailDoesNotExist_shouldSave() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerEmail("test@example.com");
        customer.setCustomerName("John");

        when(customerRepository.existsByCustomerEmail("test@example.com")).thenReturn(false);
        when(customerRepository.save(customer)).thenReturn(customer);

        // Act
        Customer result = service.createCustomer(customer);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getCustomerName());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void createCustomer_whenEmailAlreadyExists_shouldThrowException() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerEmail("duplicate@example.com");

        when(customerRepository.existsByCustomerEmail("duplicate@example.com")).thenReturn(true);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createCustomer(customer)
        );

        assertEquals("Customer with email already exists", ex.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getAllCustomers_shouldReturnList() {
        // Arrange
        Customer c1 = new Customer();
        c1.setCustomerName("John");
        Customer c2 = new Customer();
        c2.setCustomerName("Jane");

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        // Act
        List<Customer> result = service.getAllCustomers();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(c1));
        assertTrue(result.contains(c2));
    }

    @Test
    void getCustomerById_whenFound_shouldReturnCustomer() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("Alice");

        when(customerRepository.findById(1L)).thenReturn( Optional.of(customer));

        // Act
        Customer result = service.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Alice", result.getCustomerName());
    }

    @Test
    void getCustomerById_whenNotFound_shouldThrowException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getCustomerById(99L)
        );

        assertEquals("Customer not found with id 99", ex.getMessage());
    }

    @Test
    void getCustomerByEmail_whenFound_shouldReturnCustomer() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerEmail("bob@example.com");
        customer.setCustomerName("Bob");

        when(customerRepository.findByCustomerEmail("bob@example.com")).thenReturn(Optional.of(customer));

        // Act
        Customer result = service.getCustomerByEmail("bob@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("Bob", result.getCustomerName());
    }

    @Test
    void getCustomerByEmail_whenNotFound_shouldThrowException() {
        when(customerRepository.findByCustomerEmail("ghost@example.com")).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getCustomerByEmail("ghost@example.com")
        );

        assertEquals("Customer not found with email ghost@example.com", ex.getMessage());
    }
}
