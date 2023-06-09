package com.bankaccount.back.persistence;

import com.bankaccount.back.domain.repository.AccountRepository;
import com.bankaccount.back.persistence.crud.AccountCrudRepository;
import com.bankaccount.back.persistence.crud.VerificationTokenCrudRepository;
import com.bankaccount.back.persistence.entity.AccountEntity;
import com.bankaccount.back.persistence.entity.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    @Autowired
    private AccountCrudRepository accountCrudRepository;

    @Autowired
    private VerificationTokenCrudRepository tokenCrudRepository;

    @Override
    public Optional<AccountEntity> getAccountById(int id) {
        return accountCrudRepository.findById(id);
    }

    @Override
    public Optional<AccountEntity> getAccountByEmail(String email) {
        return accountCrudRepository.findByEmail(email);
    }

    @Override
    public void updateBalance(BigDecimal bigDecimal, int id) {
        accountCrudRepository.updateBalanceById(bigDecimal, id);
    }

    @Override
    public void updateStatus(int id) {
        accountCrudRepository.updateStatusById(id);
    }

    @Override
    public void updatePassword(String newPassword, int id) {
        accountCrudRepository.updatePassword(newPassword, id);
    }

    @Override
    public AccountEntity saveAccount(AccountEntity accountEntity) {
        return accountCrudRepository.save(accountEntity);
    }

    @Override
    public void saveVerificationToken(String token, AccountEntity accountEntity) {
        VerificationToken verificationToken = VerificationToken.builder()
                .accountEntity(accountEntity)
                .token(token)
                .build();

        tokenCrudRepository.save(verificationToken);
    }

    @Override
    public boolean emailExist(String email) {
        return accountCrudRepository.existsByEmail(email);
    }

    @Override
    public boolean idExist(int id) {
        return accountCrudRepository.existsById(id);
    }
}
