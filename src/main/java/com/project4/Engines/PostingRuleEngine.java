package com.project4.Engines;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class PostingRuleEngine {

    private final ResourceAccess resourceAccess;

    public PostingRuleEngine(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
    }

    public void applyRules(Entry entry) {

        Account account = entry.getAccount();

        List<PostingRule> rules =
                resourceAccess.getRulesByTriggerAccount(account.getId());

        resourceAccess.addBalance(account, entry.getAmount());

        for (PostingRule rule : rules) {
            applyRule(rule, entry);
        }
    }

    private void applyRule(PostingRule rule, Entry entry) {
        if (Objects.equals(rule.getStrategyType().toString(), "OVER_CONSUMPTION")) {
            handleOverConsumption(rule, entry.getProposedAction(), entry.getAmount());
            return;
        }
        throw new RuntimeException("Unknown rule strategy 3");
    }

    private void handleOverConsumption(PostingRule rule, ProposedAction action, Double amount) {

        Account pool = rule.getTriggerAccount();

        double balance = resourceAccess.getAccountBalance(pool.getId());
        System.out.println("Balance: " + balance);

        if (balance < 0) {
            System.out.println("Setting alert");

            Account alert = rule.getOutputAccount();

            Entry alertEntry = new Entry();
            alertEntry.setAccount(alert);
            alertEntry.setAmount(amount);
            //alertEntry.setDescription("ALERT: Pool below zero for " + pool.getName());
            alertEntry.setProposedAction(action);
            alertEntry.setBookedAt(new Date());
            alertEntry.setChargedAt(new Date());

            resourceAccess.saveEntry(alertEntry);
        }
    }
}
