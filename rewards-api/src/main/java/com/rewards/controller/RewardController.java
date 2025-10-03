package com.rewards.controller;

import com.rewards.dto.RewardResponseDTO;
import com.rewards.service.RewardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/rewards/customer")
public class RewardController {
    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getRewards(
            @PathVariable Long customerId,
            @RequestParam(required = false) Integer months,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if ( months != null && (from != null || to != null) ) {
            throw new IllegalArgumentException( "Provide either 'months' OR ('from' and 'to'), not both." );
        }
        if ( months != null && months <= 0 ) {
            throw new IllegalArgumentException( "'months' must be greater than 0" );
        }
        if ( from != null && to != null && from.isAfter( to ) ) {
            throw new IllegalArgumentException( "'from' date cannot be after 'to' date" );
        }

        RewardResponseDTO dto = rewardService.calculateRewards( customerId, months, from, to );
        if ( dto.getTotalRewards( ) == 0 && (dto.getTransactions( ) == null || dto.getTransactions( ).isEmpty( )) ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND )
                    .body( Map.of( "message", "No rewards found" ) );
        }
        return ResponseEntity.ok( dto );
    }

}
