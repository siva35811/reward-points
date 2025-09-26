package com.rewards.service;

import com.rewards.dto.TransactionRequestDTO;
import com.rewards.model.Transaction;

public interface TransactionService {
    Transaction createTransaction(TransactionRequestDTO transactionRequestDTO);
}
