package com.rewards.service.impl;

import com.rewards.dto.RewardResponseDTO;
import com.rewards.dto.TransactionResponseDTO;
import com.rewards.mapper.RewardMapper;
import com.rewards.model.Customer;
import com.rewards.model.Transaction;
import com.rewards.repository.CustomerRepository;
import com.rewards.repository.TransactionRepository;
import com.rewards.service.RewardService;
import com.rewards.util.RewardProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {

    private static final Logger log = LoggerFactory.getLogger( RewardServiceImpl.class );

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final RewardMapper rewardMapper;
    private final RewardProperties rewardProperties;

    public RewardServiceImpl(CustomerRepository customerRepository,
                             TransactionRepository transactionRepository,
                             RewardMapper rewardMapper,
                             RewardProperties rewardProperties) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.rewardMapper = rewardMapper;
        this.rewardProperties = rewardProperties;
    }

    @Override
    public RewardResponseDTO calculateRewards(Long customerId, Integer months, LocalDate from, LocalDate to) {

        // Resolve date range
        LocalDate startDate;
        LocalDate endDate;

        if (months != null) {
            endDate = LocalDate.now();
            startDate = endDate.minusMonths(months);
        } else {
            startDate = from;
            endDate = to;
        }

        Customer customer = customerRepository.findById( customerId )
                .orElseThrow( () -> new NoSuchElementException( "Customer not found with id: " + customerId ) );


        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetween( customerId, startDate, endDate );

        // Map transactions to DTOs with points
        List<TransactionResponseDTO> transactionDTOs = transactions.stream( )
                .map( tx -> rewardMapper.maptoTransactionDTO( tx, calculatePoints( tx.getAmount( ) ) ) )
                .toList( );

        // Calculate monthly rewards (group by Year-Month)
        Map<String, Integer> monthlyRewards = transactionDTOs.stream( )
                .filter( Objects::nonNull )
                .collect( Collectors.groupingBy(
                        tx -> String.format( "%04d-%02d",
                                tx.getTransactionDate( ).getYear( ),
                                tx.getTransactionDate( ).getMonthValue( ) ),
                        LinkedHashMap::new,
                        Collectors.summingInt( TransactionResponseDTO::getPoints )
                ) );

        // Calculate total rewards
        int totalRewards = transactionDTOs.stream( )
                .filter( Objects::nonNull )
                .mapToInt( TransactionResponseDTO::getPoints )
                .sum( );

        log.info( "Calculated rewards for customer {} from {} to {} => total {} points",
                customerId, startDate, endDate, totalRewards );

        // Build response DTO
        return rewardMapper.maptoRewardResponse( customer, startDate, endDate, transactionDTOs, monthlyRewards, totalRewards );
    }

    /**
     * Calculates reward points for a given transaction amount.
     */
    private int calculatePoints(BigDecimal amount) {
        if ( amount == null ) return 0;

        BigDecimal truncated = amount.setScale( 0, RoundingMode.DOWN );

        int transactionAmount = truncated.intValueExact( );

        int minSpendForPoints = rewardProperties.getMinAmtSpendForPoints( );
        int minSpendForBonus = rewardProperties.getMinAmtSpendForBonus( );
        int multiplier = rewardProperties.getMultiplier( );

        if ( transactionAmount > minSpendForBonus ) {
            int bonusPoints = (transactionAmount - minSpendForBonus) * multiplier;
            int regular = minSpendForBonus - minSpendForPoints;
            return bonusPoints + regular;
        }

        if ( transactionAmount > minSpendForPoints ) {
            return transactionAmount - minSpendForPoints;
        }

        return 0;
    }
}

