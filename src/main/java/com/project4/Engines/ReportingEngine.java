package com.project4.Engines;

import com.project4.Iterator.DepthFirstPlanIterator;
import com.project4.Resources.Plan;
import com.project4.Resources.PlanNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportingEngine {

    public List<String> generateDepthFirstReport(Plan plan) {

        List<String> report = new ArrayList<>();

        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(plan);

        while (iterator.hasNext()) {

            PlanNode node = iterator.next();

            String type = (node instanceof Plan) ? "PLAN" : "ACTION";

            String line = String.format(
                    "%s | %s | Status: %s",
                    type,
                    node.getName(),
                    node.getStatus()
            );

            report.add(line);
        }

        return report;
    }
}
