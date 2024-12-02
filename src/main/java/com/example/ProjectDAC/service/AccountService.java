package com.example.ProjectDAC.service;

import com.example.ProjectDAC.domain.Account;
import com.example.ProjectDAC.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean isExistedAccountById(long id) {
        return this.accountRepository.existsById(id);
    }

    public boolean checkPermission(long accountId, List<Long> ids) {
        Account account = this.accountRepository.findById(accountId);
        if(account.getAnken() == null) {
            return false;
        }
        long ankenId = account.getAnken().getId();
        return ids != null && ids.contains(ankenId);
    }
}
