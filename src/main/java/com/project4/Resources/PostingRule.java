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

    public Integer getId() {
        return id;
    }

    public Account getTriggerAccount() {
        return triggerAccount;
    }

    public void setTriggerAccount(Account triggerAccount) {
        this.triggerAccount = triggerAccount;
    }

    public Account getOutputAccount() {
        return outputAccount;
    }

    public void setOutputAccount(Account outputAccount) {
        this.outputAccount = outputAccount;
    }

    public StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(StrategyType strategyType) {
        this.strategyType = strategyType;
    }
}
