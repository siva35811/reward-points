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
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RewardServiceImplTest {

    private CustomerRepository customerRepo;
    private TransactionRepository txRepo;
    private RewardMapper rewardMapper;
    private RewardProperties rewardProperties;
    private RewardServiceImpl service;

    private Customer customer;

    @BeforeEach
    void setup() {
        customerRepo = mock(CustomerRepository.class);
        txRepo = mock(TransactionRepository.class);
        rewardMapper = mock(RewardMapper.class);
        rewardProperties = mock(RewardProperties.class);


        when( rewardProperties.getMinAmtSpendForPoints( ) ).thenReturn( 50 );
        when( rewardProperties.getMinAmtSpendForBonus( ) ).thenReturn( 100 );
        when( rewardProperties.getMultiplier( ) ).thenReturn( 2 );

        service = new RewardServiceImpl(customerRepo, txRepo, rewardMapper, rewardProperties);

        customer = new Customer( );
        customer.setId(1L);
        customer.setCustomerName("Test");
        customer.setCustomerEmail("t@test.com");
        customer.setCustomerContactNumber("1234567890");

        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));

        when( rewardMapper.maptoTransactionDTO( any( Transaction.class ), anyInt( ) ) )
                .thenAnswer( inv -> {
                    Transaction tx = inv.getArgument( 0 );
                    int points = inv.getArgument( 1 );
                    TransactionResponseDTO dto = new TransactionResponseDTO( );
                    dto.setTransactionDate( tx.getTransactionDate( ) );
                    dto.setTransactionAmount( tx.getAmount( ) );
                    dto.setPoints( points );
                    return dto;
                } );

        when( rewardMapper.maptoRewardResponse( any( ), any( ), any( ), anyList( ), anyMap( ), anyInt( ) ) )
                .thenAnswer( inv -> {
                    RewardResponseDTO dto = new RewardResponseDTO( );
                    dto.setCustomerName( ((Customer) inv.getArgument( 0 )).getCustomerName( ) );
                    dto.setTotalRewards( (Integer) inv.getArgument( 5 ) );
                    dto.setMonthlyRewards( inv.getArgument( 4 ) );
                    return dto;
                } );
    }

    @Test
    void throwsException_whenCustomerNotFound() {
        when( customerRepo.findById( 99L ) ).thenReturn( Optional.empty( ) );

        assertThatThrownBy( () -> service.calculateRewards( 99L, 1, null, null ) )
                .isInstanceOf( NoSuchElementException.class )
                .hasMessageContaining( "Customer not found" );
    }

    @Test
    void noTransactions_returnsZero() {
        when( txRepo.findByCustomerIdAndTransactionDateBetween( anyLong( ), any( ), any( ) ) )
                .thenReturn( Collections.emptyList( ) );

        RewardResponseDTO result = service.calculateRewards( 1L, 1, null, null );

        assertThat( result.getTotalRewards( ) ).isZero( );
        assertThat( result.getMonthlyRewards( ) ).isEmpty( );
    }

    @Test
    void transactionBelowThreshold_returnsZeroPoints() {
        Transaction t = new Transaction( BigDecimal.valueOf( 40 ), LocalDate.now( ), customer );
        when( txRepo.findByCustomerIdAndTransactionDateBetween( anyLong( ), any( ), any( ) ) )
                .thenReturn( List.of( t ) );

        RewardResponseDTO result = service.calculateRewards( 1L, 1, null, null );
        assertThat( result.getTotalRewards( ) ).isEqualTo( 0 );
    }

    @Test
    void transactionAbovePointsThresholdButBelowBonus_calculatesCorrectPoints() {
        Transaction t = new Transaction( BigDecimal.valueOf( 70 ), LocalDate.now( ), customer );
        when( txRepo.findByCustomerIdAndTransactionDateBetween( anyLong( ), any( ), any( ) ) )
                .thenReturn( List.of( t ) );

        RewardResponseDTO result = service.calculateRewards( 1L, 1, null, null );
        assertThat( result.getTotalRewards( ) ).isEqualTo( 20 );
    }

    @Test
    void transactionAboveBonusThreshold() {
        Transaction t = new Transaction( BigDecimal.valueOf( 120 ), LocalDate.now( ), customer );
        when( txRepo.findByCustomerIdAndTransactionDateBetween( anyLong( ), any( ), any( ) ) )
                .thenReturn( List.of( t ) );

        RewardResponseDTO result = service.calculateRewards( 1L, 1, null, null );
        assertThat( result.getTotalRewards( ) ).isEqualTo( 90 );
    }

    @Test
    void multipleTransactions_groupedByMonth() {
        Transaction t1 = new Transaction( BigDecimal.valueOf( 120 ), LocalDate.of( 2025, 8, 15 ), customer );
        Transaction t2 = new Transaction( BigDecimal.valueOf( 70 ), LocalDate.of( 2025, 9, 5 ), customer );

        when( txRepo.findByCustomerIdAndTransactionDateBetween( anyLong( ), any( ), any( ) ) )
                .thenReturn(List.of(t1, t2));

        RewardResponseDTO result = service.calculateRewards( 1L, null,
                LocalDate.of( 2025, 8, 1 ), LocalDate.of( 2025, 9, 30 ) );

        assertThat( result.getTotalRewards( ) ).isEqualTo( 110 ); 
        assertThat(result.getMonthlyRewards())
                .containsEntry( "2025-08", 90 )
                .containsEntry( "2025-09", 20 );
    }

    @Test
    void decimalAmount_isTruncated() {
        Transaction t = new Transaction( BigDecimal.valueOf( 120.75 ), LocalDate.now( ), customer );
        when( txRepo.findByCustomerIdAndTransactionDateBetween( anyLong( ), any( ), any( ) ) )
                .thenReturn( List.of( t ) );

        RewardResponseDTO result = service.calculateRewards( 1L, 1, null, null );
        assertThat( result.getTotalRewards( ) ).isEqualTo( 90 );
    }
}
