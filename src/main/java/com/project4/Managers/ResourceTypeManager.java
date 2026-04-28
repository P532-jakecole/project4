package com.project4.Managers;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.Account;
import com.project4.Resources.AccountKind;
import com.project4.Resources.ResourceType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResourceTypeManager {

    private final ResourceAccess resourceAccess;

    public ResourceTypeManager(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
    }

    public List<ResourceType> getAllResourceTypes() {
        return resourceAccess.getAllResourceTypes();
    }

    public void createResourceType(Map<String, Object> inputs) {
        String name = (String) inputs.get("name");
        String kind = (String) inputs.get("kind");
        String unit = (String) inputs.get("unit");
//        Integer accountId = (Integer) inputs.get("accountId");
//        Account account = resourceAccess.getAccount(accountId);


        resourceAccess.createResourceType(name, kind, unit);
    }
}
