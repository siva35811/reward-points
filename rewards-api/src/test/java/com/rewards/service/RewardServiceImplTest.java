package com.rewards.service;

import com.rewards.dto.RewardResponseDTO;
import com.rewards.dto.TransactionResponseDTO;
import com.rewards.mapper.RewardMapper;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.repository.CustomerRepository;
import com.rewards.repository.TransactionRepository;
import com.rewards.service.impl.RewardServiceImpl;
import com.rewards.util.RewardProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RewardServiceImplTest {

    private CustomerRepository customerRepo;
    private TransactionRepository txRepo;
    private RewardServiceImpl service;
    private RewardMapper rewardMapper;
    private RewardProperties rewardProperties;

    @BeforeEach
    void setup() {
        customerRepo = mock(CustomerRepository.class);
        txRepo = mock(TransactionRepository.class);
        rewardMapper = mock(RewardMapper.class);
        rewardProperties = mock(RewardProperties.class);
        service = new RewardServiceImpl(customerRepo, txRepo, rewardMapper, rewardProperties);
    }

    @Test
    void calculateRewards_sampleData() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("Test");
        customer.setCustomerEmail("t@test.com");
        customer.setCustomerContactNumber("1234567890");

        Transaction t1 = new Transaction();
        t1.setAmount(BigDecimal.valueOf(120.0));
        t1.setTransactionDate(LocalDate.of(2025, 6, 15));
        t1.setCustomer(customer);

        Transaction t2 = new Transaction();
        t2.setAmount(BigDecimal.valueOf(75.0));
        t2.setTransactionDate(LocalDate.of(2025, 7, 5));
        t2.setCustomer(customer);

        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(txRepo.findByCustomerIdAndTransactionDateBetween(eq(1L), any(), any()))
                .thenReturn(List.of(t1, t2));


        TransactionResponseDTO dto1 = new TransactionResponseDTO();
        dto1.setTransactionDate(LocalDate.of(2025, 6, 15));
        dto1.setTransactionAmount(BigDecimal.valueOf(120.0));

        TransactionResponseDTO dto2 = new TransactionResponseDTO();
        dto2.setTransactionDate(LocalDate.of(2025, 7, 5));
        dto2.setTransactionAmount(BigDecimal.valueOf(75.0));


        RewardResponseDTO expectedResponse = new RewardResponseDTO();
        expectedResponse.setCustomerName("Test");
        expectedResponse.setTotalRewards(115); // 90 + 25
        expectedResponse.setMonthlyRewards(Map.of(
                "2025-06", 90,
                "2025-07", 25
        ));

        when(rewardMapper.maptoRewardResponse(
                eq(customer),
                any(),     // fromDate
                any(),     // toDate
                any(),     // transactions
                anyMap(),  // monthly rewards
                anyInt()   // total
        )).thenReturn(expectedResponse);


        RewardResponseDTO result = service.calculateRewards(1L, 3, null, null);


        assertThat(result).isNotNull();
        assertThat(result.getCustomerName()).isEqualTo("Test");
        assertThat(result.getTotalRewards()).isEqualTo(115);
        assertThat(result.getMonthlyRewards())
                .containsEntry("2025-06", 90)
                .containsEntry("2025-07", 25);


        verify(customerRepo).findById(1L);
        verify(txRepo).findByCustomerIdAndTransactionDateBetween(eq(1L), any(), any());
        verify(rewardMapper, times(2)).maptoTransactionDTO(any(Transaction.class), anyInt());
        verify(rewardMapper).maptoRewardResponse(any(), any(), any(), any(), anyMap(), anyInt());
    }

}
