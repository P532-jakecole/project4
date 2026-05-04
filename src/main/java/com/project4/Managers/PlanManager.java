package com.project4.Managers;

import com.project4.Engines.ReportingEngine;
import com.project4.Iterator.LazySubtreeIterator;
import com.project4.Resources.*;
import com.project4.Iterator.DepthFirstPlanIterator;
import com.project4.Repositories.ResourceAccess;
import com.project4.State.ProposedState;
import com.project4.Visitor.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PlanManager {

    private final ResourceAccess resourceAccess;
    private final ReportingEngine reportingEngine;
    private final ProposedState proposedState;
    private final Integer max_depth = 10;

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
            } catch (Exception e) {
            }
        }else{
            plan.setTargetStartDate(new Date());
        }

        if (planData.get("protocolId") != null) {
            Integer protocolId = Integer.parseInt(planData.get("protocolId").toString());
            Protocol protocol = resourceAccess.getProtocol(protocolId);
            plan.setSourceProtocol(protocol);
            List<PlanNode> children = generateFromProtocol(protocol, plan, (String) planData.get("location"), (String) planData.get("party"));
            plan.setChildren(children);
            resourceAccess.savePlan(plan);

        } else if (planData.get("children") != null) {
            List<Map<String, Object>> childData =
                    (List<Map<String, Object>>) planData.get("children");
            List<PlanNode> nodes = buildPlanNodes(childData, plan);
            plan.setChildren(nodes);
            resourceAccess.savePlan(plan);
        }else{
            resourceAccess.savePlan(plan);
        }
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
                action.setLocation((String) data.get("location"));
                action.setParty((String) data.get("party"));
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

//    private List<PlanNode> generateFromProtocol(Protocol protocol, Plan parentPlan, String location, String party) {
//        Map<ProtocolStep, ProposedAction> stepMap = new HashMap<>();
//
//        for (ProtocolStep step : protocol.getSteps()) {
//            ProposedAction action = new ProposedAction();
//            action.setName(step.getName());
//            action.setProtocol(protocol);
//            action.setStatus(ActionStatus.PROPOSED);
//            action.setState(proposedState);
//            action.setLocation(location);
//            action.setParty(party);
//
//            // Likely to change
//            action.setTimeRef(parentPlan.getTargetStartDate());
//            action.setParent(parentPlan);
//            stepMap.put(step, action);
//        }
//
//        Set<ProtocolStep> visited = new HashSet<>();
//        Set<ProtocolStep> visiting = new HashSet<>();
//        List<PlanNode> orderedNodes = new ArrayList<>();
//
//        for (ProtocolStep step : protocol.getSteps()) {
//            dfsBuild(step, stepMap, visited, visiting, orderedNodes);
//        }
//
//        return orderedNodes;
//    }

    private List<PlanNode> generateFromProtocol(Protocol protocol, Plan parentPlan, String location, String party) {
        Map<ProtocolStep, PlanNode> stepMap = new HashMap<>();

        for (ProtocolStep step : protocol.getSteps()) {
            if (step.getSubProtocol() != null) {
                // Step references a sub-protocol — generate a child Plan recursively
                Plan subPlan = new Plan();
                subPlan.setName(step.getName());
                subPlan.setSourceProtocol(step.getSubProtocol());
                subPlan.setTargetStartDate(parentPlan.getTargetStartDate());
                subPlan.setParent(parentPlan);

                List<PlanNode> subChildren = generateFromProtocol(
                        step.getSubProtocol(), subPlan, location, party
                );
                subPlan.setChildren(subChildren);
                stepMap.put(step, subPlan);
            } else {
                // Regular step — generate a ProposedAction
                ProposedAction action = new ProposedAction();
                action.setName(step.getName());
                action.setProtocol(protocol);
                action.setStatus(ActionStatus.PROPOSED);
                action.setState(proposedState);
                action.setLocation(location);
                action.setParty(party);
                action.setTimeRef(parentPlan.getTargetStartDate());
                action.setParent(parentPlan);
                stepMap.put(step, action);
            }
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
            Map<ProtocolStep, PlanNode> stepMap,
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

        PlanNode node = stepMap.get(step);
        if (node != null) {
            result.add(node);
        }
    }


    public Object getPlanTree(Integer id, int depthLimit) {
        if(depthLimit < max_depth && depthLimit >= 0) {
            return getLazyPlanNodes(resourceAccess.getPlan(id), depthLimit);
        }
        return resourceAccess.getPlan(id);
    }

    public List<Map<String, Object>> getLazyPlanNodes(Plan plan, int depthLimit) {

        List<Map<String, Object>> nodes = new ArrayList<>();
        LazySubtreeIterator iterator = new LazySubtreeIterator(plan, depthLimit);

        while (iterator.hasNext()) {
            PlanNode node = iterator.next();

            int depth = getDepth(node);
            boolean isCollapsed = depth == depthLimit && node instanceof Plan;

            Map<String, Object> entry = new HashMap<>();
            entry.put("id", node.getId());
            entry.put("name", node.getName());
            entry.put("status", node.getStatus());
            entry.put("depth", depth);
            entry.put("type", node instanceof Plan ? "PLAN" : "ACTION");
            entry.put("collapsed", isCollapsed);
            entry.put("hasChildren", node instanceof Plan p &&
                    p.getChildren() != null &&
                    !p.getChildren().isEmpty());

            nodes.add(entry);
        }

        return nodes;
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


    public List<Plan> getPlans() {
        return resourceAccess.getPlans();
    }


    public List<Map<String, Object>> generateDepthFirstReport(Integer planId, String status) {
        Plan plan = resourceAccess.getPlan(planId);

        return reportingEngine.generateDepthFirstReport(plan, status);
    }

    public Map<String, Object> getMetrics(Integer id) {
        PlanNode node = resourceAccess.getPlanNode(id);
        if (node == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        CompletionRatioVisitor completion = new CompletionRatioVisitor();
        ResourceCostVisitor cost = new ResourceCostVisitor(resourceAccess);
        RiskScoreVisitor risk = new RiskScoreVisitor();

        node.accept(completion);
        node.accept(cost);
        node.accept(risk);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("completionRatio", completion.getRatio());
        metrics.put("totalCost", cost.getTotalCost());
        metrics.put("riskScore", risk.getScore());

        return metrics;
    }
}
