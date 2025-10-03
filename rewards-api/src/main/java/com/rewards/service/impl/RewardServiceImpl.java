package com.rewards.service.impl;

import com.rewards.dto.RewardResponseDTO;
import com.rewards.dto.TransactionResponseDTO;
import com.rewards.mapper.RewardMapper;
import com.rewards.model.Customer;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {

    private static final Logger log = LoggerFactory.getLogger( RewardServiceImpl.class );
	

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final RewardMapper rewardMapper;
    private final RewardProperties rewardProperties;
	
	private record LocalDateRange(LocalDate start, LocalDate end) {
    }



    /**
     * Resolves a date range either from the given {@code months} or from explicit {@code from}/{@code to} values.
     */
    private LocalDateRange resolveDateRange(Integer months, LocalDate from, LocalDate to) {
        if ( months != null ) {
            LocalDate end = LocalDate.now( );
            return new LocalDateRange( end.minusMonths( months ), end );
        }
        return new LocalDateRange( from, to );
    }

    public RewardServiceImpl(CustomerRepository customerRepository,
                             TransactionRepository transactionRepository,
                             RewardMapper rewardMapper,
                             RewardProperties rewardProperties) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.rewardMapper = rewardMapper;
        this.rewardProperties = rewardProperties;
    }

    /**
     * Finds a customer by ID or throws NoSuchElementException if not found.
     */
    private Customer findCustomer(Long customerId) {
        return customerRepository.findById( customerId )
                .orElseThrow( () -> new NoSuchElementException( "Customer not found with id: " + customerId ) );
    }

    /**
     * Fetches all transactions for the given customer in a date range and maps them into DTOs with calculated points.
     */
    private List<TransactionResponseDTO> fetchAndMapTransactions(Long customerId, LocalDateRange range) {
        return transactionRepository.findByCustomerIdAndTransactionDateBetween( customerId, range.start( ), range.end( ) )
                .stream( )
                .map( tx -> rewardMapper.maptoTransactionDTO( tx, calculatePoints( tx.getAmount( ) ) ) )
                .toList( );
    }

    /**
     * Calculates reward points for a given transaction amount based on configured thresholds and multipliers.
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
            int regularPoints = minSpendForBonus - minSpendForPoints;
            return bonusPoints + regularPoints;
        }

        if ( transactionAmount > minSpendForPoints ) {
            return transactionAmount - minSpendForPoints;
        }

        return 0;
    }

    /**
     * Groups transactions by Year-Month and sums their reward points.
     */
    private Map<String, Integer> calculateMonthlyRewards(List<TransactionResponseDTO> transactionDTOs) {
        return transactionDTOs.stream( )
                .collect( Collectors.groupingBy(
                        tx -> String.format( "%04d-%02d",
                                tx.getTransactionDate( ).getYear( ),
                                tx.getTransactionDate( ).getMonthValue( ) ),
                        LinkedHashMap::new,
                        Collectors.summingInt( TransactionResponseDTO::getPoints )
                ) );
    }

    /**
     * Sums up total reward points from all transactions.
     */
    private int calculateTotalRewards(List<TransactionResponseDTO> transactionDTOs) {
        return transactionDTOs.stream( )
                .mapToInt( TransactionResponseDTO::getPoints )
                .sum( );
    }

    /**
     * Calculates rewards for a customer within a date range.
     *
     * @param customerId ID of the customer
     * @param months     optional number of months to look back;
     * @param from       start date (used only if months is null)
     * @param to         end date (used only if months is null)
     * @return RewardResponseDTO containing transactions, monthly and total reward summary
     */
    @Override
    public RewardResponseDTO calculateRewards(Long customerId, Integer months, LocalDate from, LocalDate to) {
        LocalDateRange range = resolveDateRange( months, from, to );
        Customer customer = findCustomer( customerId );
        List<TransactionResponseDTO> transactionDTOs = fetchAndMapTransactions( customerId, range );

        Map<String, Integer> monthlyRewards = calculateMonthlyRewards( transactionDTOs );
        int totalRewards = calculateTotalRewards( transactionDTOs );

        log.info( "Calculated rewards for customer {} from {} to {} => total {} points",
                customerId, range.start( ), range.end( ), totalRewards );

        return rewardMapper.maptoRewardResponse(
                customer, range.start( ), range.end( ), transactionDTOs, monthlyRewards, totalRewards
        );
    }

   
}
