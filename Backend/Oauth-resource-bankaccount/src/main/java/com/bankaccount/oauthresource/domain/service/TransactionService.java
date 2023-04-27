package com.bankaccount.oauthresource.domain.service;

import com.bankaccount.oauthresource.domain.TransactionDomain;
import com.bankaccount.oauthresource.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Optional<TransactionDomain> getTransactionById(long id) {
        return transactionRepository.getTransactionById(id);
    }

    public List<TransactionDomain> getByIdAccount(long idAccount) {
        return transactionRepository.getByIdAccount(idAccount);
    }

    public List<TransactionDomain> getByTimeAndIdAccount(Instant timestamp, long idAccount) {
        return transactionRepository.getByTimeAndIdAccount(timestamp, idAccount);
    }
}
