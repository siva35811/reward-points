package com.rewards.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RewardResponseDTO {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private LocalDate from;
    private LocalDate to;
    private List<TransactionResponseDTO> transactions;
    private Map<String, Integer> monthlyRewards;
    private double totalRewards;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public List<TransactionResponseDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponseDTO> transactions) {
        this.transactions = transactions;
    }

    public Map<String, Integer> getMonthlyRewards() {
        return monthlyRewards;
    }

    public void setMonthlyRewards(Map<String, Integer> monthlyRewards) {
        this.monthlyRewards = monthlyRewards;
    }

    public double getTotalRewards() {
        return totalRewards;
    }

    public void setTotalRewards(double totalRewards) {
        this.totalRewards = totalRewards;
    }
}
