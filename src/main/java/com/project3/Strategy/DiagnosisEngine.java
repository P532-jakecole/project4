package com.project3.Strategy;

import com.project3.DataTypes.AssociativeFunction;
import com.project3.DataTypes.Observation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class DiagnosisEngine {
    private HashMap<String, DiagnosisStrategy> strategies = new HashMap<>();

    public DiagnosisEngine() {}

    public void setDiagnosisStrategy(String strategy, String staff) {
        switch(strategy.toLowerCase()){
            case "simpleconjunctive":
                DiagnosisStrategy ds = new SimpleConjunctiveStrategy();
                strategies.put(staff, ds);
                break;
        }
    }

    public boolean evaluate(String user, AssociativeFunction af, List<Observation> observations) {
        if(strategies.containsKey(user)){
            DiagnosisStrategy ds = strategies.get(user);
            return ds.evaluate(af, observations);
        }else{
            DiagnosisStrategy ds = new SimpleConjunctiveStrategy();
            strategies.put(user, ds);
            return ds.evaluate(af, observations);
        }
    }
}
