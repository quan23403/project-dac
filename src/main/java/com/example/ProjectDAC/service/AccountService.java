package com.example.ProjectDAC.service;

import com.example.ProjectDAC.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean isExistedAccountById(long id) {
        return this.accountRepository.existsById(id);
    }
}
