package com.project3.Strategy;

import com.project3.DataTypes.AssociativeFunction;
import com.project3.DataTypes.Observation;
import com.project3.DataTypes.Strategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiagnosisEngine {
    //private HashMap<String, DiagnosisStrategy> strategies = new HashMap<>();

    private final StrategyFactory strategyFactory;

    public DiagnosisEngine(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

//    public void setDiagnosisStrategy(String strategy, String staff) {
//        switch(strategy.toLowerCase()){
//            case "simpleconjunctive":
//                DiagnosisStrategy ds = new SimpleConjunctiveStrategy();
//                strategies.put(staff, ds);
//                break;
//            case "weightedscoring":
//                DiagnosisStrategy weighted = new WeightedScoringStrategy();
//                strategies.put(staff, weighted);
//                break;
//        }
//    }

    public List<Observation> evaluate(AssociativeFunction af, List<Observation> observations) {
        DiagnosisStrategy ds = strategyFactory.getStrategy(af.getStrategy());
        return ds.evaluate(af, observations);
    }
}
