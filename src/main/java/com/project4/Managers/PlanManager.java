package com.project4.Managers;

import com.project4.Engines.ReportingEngine;
import com.project4.Resources.*;
import com.project4.Iterator.DepthFirstPlanIterator;
import com.project4.Repositories.ResourceAccess;
import com.project4.State.ProposedState;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PlanManager {

    private final ResourceAccess resourceAccess;
    private final ReportingEngine reportingEngine;
    private final ProposedState proposedState;

    public PlanManager(ResourceAccess resourceAccess, ReportingEngine reportingEngine, ProposedState proposedState) {
        this.resourceAccess = resourceAccess;
        this.reportingEngine = reportingEngine;
        this.proposedState = proposedState;
    }


    public void createPlan(Map<String, Object> planData) {
        Plan plan = new Plan();
        plan.setName((String) planData.get("name"));

        if (planData.get("targetStartDate") != null && !planData.get("targetStartDate").toString().isEmpty()) {
            try {
                String dateStr = planData.get("targetStartDate").toString();
                Date date = dateStr.contains("-")
                        ? new SimpleDateFormat("yyyy-MM-dd").parse(dateStr)
                        : new Date(Long.parseLong(dateStr));
                plan.setTargetStartDate(date);
            } catch (Exception e) {}
        }

        if (planData.get("protocolId") != null) {
            Integer protocolId = Integer.parseInt(planData.get("protocolId").toString());
            Protocol protocol = resourceAccess.getProtocol(protocolId);
            plan.setSourceProtocol(protocol);
            List<PlanNode> children = generateFromProtocol(protocol, plan);
            plan.setChildren(children);

        } else if (planData.get("children") != null) {
            List<Map<String, Object>> childData =
                    (List<Map<String, Object>>) planData.get("children");
            List<PlanNode> nodes = buildPlanNodes(childData, plan);
            plan.setChildren(nodes);
        }

        resourceAccess.savePlan(plan);
    }

    private List<PlanNode> buildPlanNodes(List<Map<String, Object>> dataList, PlanNode parent) {
        List<PlanNode> nodes = new ArrayList<>();

        for (Map<String, Object> data : dataList) {
            String type = (String) data.get("type");

            if ("ACTION".equalsIgnoreCase(type)) {
                ProposedAction action = new ProposedAction();
                action.setName((String) data.get("name"));
                action.setStatus(ActionStatus.PROPOSED);
                action.setState(proposedState);
                // Likely to change
                action.setTimeRef(new Date());
                action.setParent(parent);
                nodes.add(action);

            } else if ("PLAN".equalsIgnoreCase(type)) {
                Plan subPlan = new Plan();
                subPlan.setName((String) data.get("name"));
                subPlan.setParent(parent);

                if (data.get("children") != null) {
                    List<Map<String, Object>> childData =
                            (List<Map<String, Object>>) data.get("children");
                    subPlan.setChildren(buildPlanNodes(childData, subPlan));
                }

                nodes.add(subPlan);
            }
        }
        return nodes;
    }

    private List<PlanNode> generateFromProtocol(Protocol protocol, Plan parentPlan) {
        Map<ProtocolStep, ProposedAction> stepMap = new HashMap<>();

        for (ProtocolStep step : protocol.getSteps()) {
            ProposedAction action = new ProposedAction();
            action.setName(step.getName());
            action.setProtocol(protocol);
            action.setStatus(ActionStatus.PROPOSED);
            action.setState(proposedState);
            // Likely to change
            action.setTimeRef(parentPlan.getTargetStartDate());
            action.setParent(parentPlan);
            stepMap.put(step, action);
        }

        Set<ProtocolStep> visited = new HashSet<>();
        Set<ProtocolStep> visiting = new HashSet<>();
        List<PlanNode> orderedNodes = new ArrayList<>();

        for (ProtocolStep step : protocol.getSteps()) {
            dfsBuild(step, stepMap, visited, visiting, orderedNodes);
        }

        return orderedNodes;
    }

    private void dfsBuild(
            ProtocolStep step,
            Map<ProtocolStep, ProposedAction> stepMap,
            Set<ProtocolStep> visited,
            Set<ProtocolStep> visiting,
            List<PlanNode> result
    ) {
        if (visited.contains(step)) return;

        if (visiting.contains(step)) {
            throw new RuntimeException("Cycle detected in protocol at step: " + step.getName());
        }

        visiting.add(step);

        if (step.getDependsOn() != null) {
            for (ProtocolStep dep : step.getDependsOn()) {
                if (dep == null) {
                    throw new RuntimeException("Null dependency in step: " + step.getName());
                }
                dfsBuild(dep, stepMap, visited, visiting, result);
            }
        }

        visiting.remove(step);
        visited.add(step);
        result.add(stepMap.get(step));
    }


    public Plan getPlanTree(Integer id) {
        return resourceAccess.getPlan(id);
    }


    public List<Plan> getPlans() {
        return resourceAccess.getPlans();
    }


    public List<Map<String, Object>> generateDepthFirstReport(Integer planId) {

        Plan plan = resourceAccess.getPlan(planId);

        return reportingEngine.generateDepthFirstReport(plan);
    }
}
