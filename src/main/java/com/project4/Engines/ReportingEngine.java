package com.project4.Engines;

import com.project4.Iterator.DepthFirstPlanIterator;
import com.project4.Managers.ResourceTypeManager;
import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.Plan;
import com.project4.Resources.PlanNode;
import com.project4.Resources.ResourceType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportingEngine {
    private final ResourceAccess resourceAccess;

    public ReportingEngine(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
    }

    public List<Map<String, Object>> generateDepthFirstReport(Plan plan) {

        List<Map<String, Object>> report = new ArrayList<>();
        List<ResourceType> allResourceTypes = resourceAccess.getAllResourceTypes();

        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(plan);

        while (iterator.hasNext()) {

            PlanNode node = iterator.next();

            String type = (node instanceof Plan) ? "PLAN" : "ACTION";

            Map<String, Double> allocations = new LinkedHashMap<>();
            for (ResourceType rt : allResourceTypes) {
                double qty = node.getTotalAllocatedQuantity(rt);
                if (qty > 0) {
                    allocations.put(rt.getName() + " (" + rt.getUnit() + ")", qty);
                }
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("type", type);
            entry.put("name", node.getName());
            entry.put("status", node.getStatus());
            entry.put("allocations", allocations);
            entry.put("depth", getDepth(node));

            report.add(entry);
        }

        return report;
    }

    private int getDepth(PlanNode node) {
        int depth = 0;
        PlanNode current = node;
        while (current.getParent() != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }
}
