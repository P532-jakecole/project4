package com.project4.Managers;

import com.project4.Resources.*;
import com.project4.Iterator.DepthFirstPlanIterator;
import com.project4.Repositories.ResourceAccess;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlanManager {

    private final ResourceAccess resourceAccess;

    public PlanManager(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
    }


    public void createPlan(Map<String, Object> planData) {

        Plan plan = new Plan();

        plan.setName((String) planData.get("name"));

        if (planData.get("targetStartDate") != null) {
            plan.setTargetStartDate(
                    new Date(Long.parseLong(planData.get("targetStartDate").toString()))
            );
        }


        if (planData.get("protocolId") != null) {

            Integer protocolId = Integer.parseInt(planData.get("protocolId").toString());
            Protocol protocol = resourceAccess.getProtocol(protocolId);

            plan.setSourceProtocol(protocol);

            List<PlanNode> children = generateFromProtocol(protocol, plan);
            plan.setChildren(children);
        } else {
            plan.setChildren(new ArrayList<>());
        }

        resourceAccess.savePlan(plan);
    }


    private List<PlanNode> generateFromProtocol(Protocol protocol, Plan parentPlan) {

        Map<ProtocolStep, ProposedAction> stepMap = new HashMap<>();
        List<PlanNode> rootNodes = new ArrayList<>();

        /* ===== PASS 1: Create all ProposedActions ===== */
        for (ProtocolStep step : protocol.getSteps()) {

            ProposedAction action = new ProposedAction();
            action.setName(step.getName());
            action.setProtocol(protocol);
            action.setStatus(ActionStatus.PROPOSED);

            resourceAccess.saveProposedAction(action);

            stepMap.put(step, action);
        }

        /* ===== PASS 2: Build dependency-respecting order using DFS ===== */

        Map<String, ProtocolStep> stepByName = new HashMap<>();
        for (ProtocolStep step : protocol.getSteps()) {
            stepByName.put(step.getName(), step);
        }

        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        List<PlanNode> orderedNodes = new ArrayList<>();

        for (ProtocolStep step : protocol.getSteps()) {
            dfsBuild(step, stepByName, stepMap, visited, visiting, orderedNodes);
        }

        return rootNodes;
    }

    private void dfsBuild(
            ProtocolStep step,
            Map<String, ProtocolStep> stepByName,
            Map<ProtocolStep, ProposedAction> stepMap,
            Set<String> visited,
            Set<String> visiting,
            List<PlanNode> result
    ) {
        String name = step.getName();

        if (visited.contains(name)) return;

        if (visiting.contains(name)) {
            throw new RuntimeException("Cycle detected in protocol steps at: " + name);
        }

        visiting.add(name);

        /* ===== Visit dependencies FIRST ===== */
        if (step.getDependsOn() != null) {
            for (ProtocolStep dep : step.getDependsOn()) {
                String depName = dep.getName();

                ProtocolStep depStep = stepByName.get(depName);

                if (depStep == null) {
                    throw new RuntimeException("Missing dependency: " + depName);
                }

                dfsBuild(depStep, stepByName, stepMap, visited, visiting, result);
            }
        }

        visiting.remove(name);
        visited.add(name);

        result.add(stepMap.get(step));
    }


    public Plan getPlanTree(Integer id) {
        return resourceAccess.getPlan(id);
    }


    public List<Plan> getPlans() {
        return resourceAccess.getPlans();
    }


    public List<String> generateDepthFirstReport(Integer planId) {

        Plan plan = resourceAccess.getPlan(planId);

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
