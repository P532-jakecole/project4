package com.project4.Managers;

import com.project4.Repositories.ResourceAccess;
import com.project4.Resources.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountManager {
    private final ResourceAccess resourceAccess;

    public AccountManager(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
    }

    public List<Entry> getEntriesForAccount(Integer id){
        return resourceAccess.getEntriesByAccount(id);
    }

    public List<Account> getAllAccounts(){
        return resourceAccess.getAllAccounts();
    }
}
