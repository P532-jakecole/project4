package com.project4.Managers;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.Protocol;
import com.project4.Resources.ProtocolStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProtocolManager {
    private final ResourceAccess resourceAccess;

    public ProtocolManager(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
    }

    public List<Protocol> getProtocols() {
        return resourceAccess.getAllProtocols();
    }

    public void createProtocol(Map<String, Object> inputs) {
        String name = (String) inputs.get("name");
        String description = (String) inputs.get("description");

        List<Map<String, Object>> stepsRaw =
                (List<Map<String, Object>>) inputs.get("steps");

        Protocol protocol = new Protocol();
        protocol.setName(name);
        protocol.setDescription(description);

        List<ProtocolStep> steps = new ArrayList<>();
        Map<String, ProtocolStep> stepMap = new HashMap<>();


        for (Map<String, Object> s : stepsRaw) {

            String stepName = (String) s.get("name");

            ProtocolStep step = new ProtocolStep();
            step.setName(stepName);
            step.setProtocol(protocol);

            // optional sub-protocol
            if (s.get("subProtocol") != null) {
                Integer subId = Integer.parseInt(s.get("subProtocol").toString());
                Protocol sub = resourceAccess.getProtocol(subId);
                step.setSubProtocol(sub);
            }

            steps.add(step);
            stepMap.put(stepName, step);
        }


        for (Map<String, Object> s : stepsRaw) {

            String stepName = (String) s.get("name");
            ProtocolStep currentStep = stepMap.get(stepName);

            List<String> dependsOnNames = (List<String>) s.get("dependsOn");

            if (dependsOnNames != null && !dependsOnNames.isEmpty()) {

                List<ProtocolStep> dependencies = new ArrayList<>();

                for (String depName : dependsOnNames) {
                    ProtocolStep depStep = stepMap.get(depName);

                    if (depStep == null) {
                        throw new RuntimeException(
                                "Invalid dependency: " + depName + " not found in protocol steps"
                        );
                    }

                    dependencies.add(depStep);
                }

                currentStep.setDependsOn(dependencies);
            }
        }

        protocol.setSteps(steps);

        resourceAccess.addProtocol(protocol);
    }


}
