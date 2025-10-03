package com.rewards.controller;


import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.repository.CustomerRepository;
import com.rewards.repository.TransactionRepository;
import com.rewards.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class RewardControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private TransactionRepository txRepo;
    @Autowired
    private RewardService rewardService;

    private Customer c;
    private Customer customer;

    @BeforeEach
    void setup() {
        txRepo.deleteAll();
        customerRepo.deleteAll();

        c = new Customer();
        c.setCustomerName("test");
        c.setCustomerEmail("test@test.com");
        c.setCustomerContactNumber("1234567890");
        c = customerRepo.save(c);

        txRepo.save(new Transaction(BigDecimal.valueOf(120.0), LocalDate.now().minusDays(10), c));
        txRepo.save(new Transaction(BigDecimal.valueOf(70.0), LocalDate.now().minusMonths(5), c));

        customer = new Customer( );
        customer.setCustomerName( "test1" );
        customer.setCustomerEmail( "test1@test.com" );
        customer.setCustomerContactNumber( "1134567890" );
        customer = customerRepo.save( customer );

        txRepo.save( new Transaction( BigDecimal.valueOf( 70.0 ), LocalDate.now( ), customer ) );
    }

    @Test
    void getRewards_returnOk() throws Exception {
        mockMvc.perform( get( "/api/rewards/customer/" + c.getId( ) )
                        .param("months", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("test"))
                .andExpect(jsonPath("$.totalRewards").isNumber());
    }

    @Test
    void getRewards_returnsOkWithMinPoints() throws Exception {
        mockMvc.perform( get( "/api/rewards/customer/" + customer.getId( ) )
                        .param( "months", "1" )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.customerName" ).value( "test1" ) )
                .andExpect( jsonPath( "$.totalRewards" ).isNumber( ) );
    }


    @Test
    void getRewards_withFromTo_shouldReturnOk() throws Exception {
        mockMvc.perform( get( "/api/rewards/customer/" + c.getId( ) )
                        .param( "from", LocalDate.now( ).minusMonths( 1 ).toString( ) )
                        .param( "to", LocalDate.now( ).toString( ) ) )
                .andExpect( status( ).isOk( ) )
                .andExpect( jsonPath( "$.totalRewards" ).isNumber( ) );
    }


    @Test
    void getRewards_withMonthsAndFromTo_shouldReturn422() throws Exception {
        mockMvc.perform( get( "/api/rewards/customer/" + c.getId( ) )
                        .param( "months", "3" )
                        .param( "from", "2025-01-01" )
                        .param( "to", "2025-02-01" ) )
                .andExpect( status( ).isUnprocessableEntity( ) );
    }


    @Test
    void getRewards_withInvalidMonths_shouldReturn422() throws Exception {
        mockMvc.perform( get( "/api/rewards/customer/" + c.getId( ) )
                        .param( "months", "0" ) )
                .andExpect( status( ).isUnprocessableEntity( ) );
    }


    @Test
    void getRewards_withInvalidDateRange_shouldReturn422() throws Exception {
        mockMvc.perform( get( "/api/rewards/customer/" + c.getId( ) )
                        .param( "from", "2025-03-01" )
                        .param( "to", "2025-01-01" ) )
                .andExpect( status( ).isUnprocessableEntity( ) );
    }


    @Test
    void getRewards_customerNotFound_shouldReturnNotFound() throws Exception {
        mockMvc.perform( get( "/api/rewards/customer/999" )
                        .param( "months", "1" ) )
                .andExpect( status( ).isNotFound( ) );
    }

    @Test
    void getRewards_withBadDateFormat_shouldReturnNotFound() throws Exception {
        mockMvc.perform( get( "/api/rewards/customer/" + c.getId( ) )
                        .param( "from", "invalid-date" )
                        .param( "to", "2025-01-01" ) )
                .andExpect( status( ).isBadRequest( ) );
    }

    @Test
    void getRewards_noRewardsFound_shouldReturnNotFound() throws Exception {
        txRepo.deleteAll( );

        mockMvc.perform( get( "/api/rewards/customer/" + c.getId( ) )
                        .param( "months", "1" ) )
                .andExpect( status( ).isNotFound( ) )
                .andExpect( jsonPath( "$.message" ).value( "No rewards found" ) );
    }

}
