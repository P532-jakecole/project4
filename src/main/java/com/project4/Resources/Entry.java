package com.project4.Resources;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Entry {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Transaction transaction;

    @ManyToOne
    private Account account;

    private Double amount;

    private Date chargedAt;
    private Date bookedAt;

    public Integer getId() {
        return id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getChargedAt() {
        return chargedAt;
    }

    public void setChargedAt(Date chargedAt) {
        this.chargedAt = chargedAt;
    }

    public Date getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(Date bookedAt) {
        this.bookedAt = bookedAt;
    }
}
