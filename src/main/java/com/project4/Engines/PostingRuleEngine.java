package com.project4.Engines;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.Account;
import com.project4.Resources.Entry;
import com.project4.Resources.PostingRule;
import com.project4.Resources.ProposedAction;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

        for (PostingRule rule : rules) {
            applyRule(rule, entry);
        }
    }

    private void applyRule(PostingRule rule, Entry entry) {

        switch (rule.getStrategyType()) {
            case OVER_CONSUMPTION:
                handleOverConsumption(rule, entry.getAmount(), entry.getProposedAction());

            default:
                throw new RuntimeException("Unknown rule strategy");
        }
    }

    private void handleOverConsumption(PostingRule rule, Double amount, ProposedAction action) {
        resourceAccess.addBalance(rule.getTriggerAccount(), amount);

        Account pool = rule.getTriggerAccount();

        double balance = resourceAccess.getAccountBalance(pool.getId());

        if (balance < 0) {

            Account alert = rule.getOutputAccount();

            Entry alertEntry = new Entry();
            alertEntry.setAccount(alert);
            alertEntry.setAmount(0.0);
            //alertEntry.setDescription("ALERT: Pool below zero for " + pool.getName());
            alertEntry.setProposedAction(action);
            alertEntry.setBookedAt(new Date());
            alertEntry.setChargedAt(new Date());

            resourceAccess.saveEntry(alertEntry);
        }
    }
}
