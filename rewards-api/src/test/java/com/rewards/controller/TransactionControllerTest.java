package com.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewards.dto.TransactionRequestDTO;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.repository.CustomerRepository;
import com.rewards.repository.TransactionRepository;
import com.rewards.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class TransactionControllerTest {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TransactionService transactionService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {

        transactionRepository.deleteAll( );
        customerRepository.deleteAll( );
    }

    @Test
    void createTransaction_shouldReturn201Created() throws Exception {

        TransactionRequestDTO request = new TransactionRequestDTO( );
        request.setAmount( BigDecimal.valueOf( 150 ) );
        request.setTransactionDate( LocalDate.of( 2025, 9, 26 ) );
        request.setCustomerId( 1L );

        Customer customer = new Customer( );
        customer.setId( 1L );
        customer.setCustomerName( "John Doe" );

        Transaction savedTx = new Transaction(
                request.getAmount( ),
                request.getTransactionDate( ),
                customer
        );
        savedTx.setId( 100L );

        when( transactionService.createTransaction( any( TransactionRequestDTO.class ) ) )
                .thenReturn( savedTx );


        mockMvc.perform( post( "/api/transactions" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( request ) ) )
                .andExpect( status( ).isCreated( ) )
                .andExpect( header( ).string( "Location", "/api/transactions/100" ) )
                .andExpect( jsonPath( "$.id" ).value( 100 ) )
                .andExpect( jsonPath( "$.amount" ).value( 150 ) )
                .andExpect( jsonPath( "$.customer.id" ).value( 1 ) );
    }
}
