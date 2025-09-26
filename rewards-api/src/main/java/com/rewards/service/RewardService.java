package com.rewards.service;

import com.rewards.dto.RewardResponseDTO;

import java.time.LocalDate;

public interface RewardService {
    RewardResponseDTO calculateRewards(Long customerId, Integer months, LocalDate from, LocalDate to);
}
