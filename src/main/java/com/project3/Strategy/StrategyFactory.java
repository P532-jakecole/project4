package com.project3.Strategy;

import com.project3.DataTypes.Strategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StrategyFactory {

    private final Map<Strategy, DiagnosisStrategy> strategies;

    public StrategyFactory(Map<String, DiagnosisStrategy> strategyBeans) {

        this.strategies = new HashMap<>();

        for (Map.Entry<String, DiagnosisStrategy> entry : strategyBeans.entrySet()) {
            Strategy type = Strategy.valueOf(entry.getKey());
            strategies.put(type, entry.getValue());
        }

    }

    public DiagnosisStrategy getStrategy(Strategy type) {
        return strategies.get(type);
    }
}
