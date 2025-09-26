package com.rewards.mapper;

import com.rewards.dto.TransactionResponseDTO;
import com.rewards.dto.RewardResponseDTO;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class RewardMapper {

    public TransactionResponseDTO maptoTransactionDTO(Transaction transaction,
                                                      int points) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO( );
        transactionResponseDTO.setTransactionId( transaction.getId( ) );
        transactionResponseDTO.setTransactionDate( transaction.getTransactionDate( ) );
        transactionResponseDTO.setTransactionAmount( transaction.getAmount( ) );
        transactionResponseDTO.setPoints( points );
        return transactionResponseDTO;
    }

    public RewardResponseDTO maptoRewardResponse(Customer customer,
                                                 LocalDate transactionDateFrom,
                                                 LocalDate transactionDateTo,
                                                 List<TransactionResponseDTO> transactions,
                                                 Map<String, Integer> monthlyRewards,
                                                 int total) {
        RewardResponseDTO dto = new RewardResponseDTO( );
        dto.setCustomerId( customer.getId( ) );
        dto.setCustomerName( customer.getCustomerName( ) );
        dto.setCustomerEmail( customer.getCustomerEmail( ) );
        dto.setFrom( transactionDateFrom );
        dto.setTo( transactionDateTo );
        dto.setTransactions( transactions );
        dto.setMonthlyRewards( monthlyRewards );
        dto.setTotalRewards( total );
        return dto;
    }
}
