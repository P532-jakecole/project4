package com.project4.Resources;

import jakarta.persistence.*;

@Entity
public class PostingRule {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Account triggerAccount;

    @ManyToOne
    private Account outputAccount;

    @Enumerated(EnumType.STRING)
    private StrategyType strategyType;
}
