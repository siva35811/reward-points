package com.rewards.controller;

import com.rewards.dto.TransactionRequestDTO;
import com.rewards.model.Transaction;
import com.rewards.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.net.URI;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequestDTO req) {
        Transaction saved = transactionService.createTransaction( req );
        return ResponseEntity
                .created( URI.create( "/api/transactions/" + saved.getId( ) ) )
                .body( saved );
    }
}
