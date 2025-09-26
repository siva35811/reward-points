package com.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewards.model.Customer;
import com.rewards.repository.CustomerRepository;
import com.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class CustomerControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CustomerRepository customerRepo;

    @Autowired
    private TransactionRepository transactionRepository;

    private Customer customer;


    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        customerRepo.deleteAll();
        customer = new Customer();
        customer.setCustomerName("test");
        customer.setCustomerEmail("test@example.com");
        customer.setCustomerContactNumber("9876543210");
        customer = customerRepo.save(customer);
    }

    @Test
    void createCustomer_shouldReturn201() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerName("Alice");
        customer.setCustomerEmail("alice@test.com");
        customer.setCustomerContactNumber("1234567890");

        mockMvc.perform(post("/api/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/customers/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.customerEmail").value("alice@test.com"));
    }


    @Test
    void getAllCustomers_shouldReturnList() throws Exception {
        Customer c = new Customer();
        c.setCustomerName("Bob");
        c.setCustomerEmail("bob@test.com");
        c.setCustomerContactNumber("9876543210");
        customerRepo.save(c);

        mockMvc.perform(get("/api/customers/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("test"))
                .andExpect(jsonPath("$[0].customerEmail").value("test@example.com"));
    }

    @Test
    void getCustomerById_shouldReturnCustomer() throws Exception {
        Customer c = new Customer();
        c.setCustomerName("Charlie");
        c.setCustomerEmail("charlie@test.com");
        c.setCustomerContactNumber("1231231234");
        Customer saved = customerRepo.save(c);

        mockMvc.perform(get("/api/customers/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Charlie"))
                .andExpect(jsonPath("$.customerEmail").value("charlie@test.com"));
    }

    @Test
    void getCustomerByEmail_shouldReturnCustomer() throws Exception {
        Customer c = new Customer();
        c.setCustomerName("David");
        c.setCustomerEmail("david@test.com");
        c.setCustomerContactNumber("1112223333");
        customerRepo.save(c);

        mockMvc.perform(get("/api/customers/by-email")
                        .param("email", "david@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("David"))
                .andExpect(jsonPath("$.customerEmail").value("david@test.com"));
    }


    @Test
    void getCustomerById_notFound() throws Exception {
        mockMvc.perform(get("/api/customers/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomerByEmail_notFound() throws Exception {
        mockMvc.perform(get("/api/customers/by-email")
                        .param("email", "notfound@test.com"))
                .andExpect(status().isNotFound());
    }
}
