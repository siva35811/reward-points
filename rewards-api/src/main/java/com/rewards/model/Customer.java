package com.rewards.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String customerName;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String customerEmail;
    @NotNull
    private String customerContactNumber;
    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>( );

    public Customer(String alice, String mail, String number) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerContactNumber = customerContactNumber;
    }

    public @NotNull String getCustomerContactNumber() {
        return customerContactNumber;
    }

    public void setCustomerContactNumber(@NotNull String customerContactNumber) {
        this.customerContactNumber = customerContactNumber;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(@NotBlank String customerName) {
        this.customerName = customerName;
    }

    public @Email @NotBlank String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(@Email @NotBlank String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions.clear( );
        if ( transactions != null ) {
            transactions.forEach( t -> t.setCustomer( this ) );
            this.transactions.addAll( transactions );
        }
    }

    public void addTransaction(Transaction tx) {
        tx.setCustomer( this );
        this.transactions.add( tx );
    }
}
