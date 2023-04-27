package com.bankaccount.oauthresource.mapper;

import com.bankaccount.oauthresource.constants.TransactionType;
import com.bankaccount.oauthresource.domain.TransactionDomain;
import com.bankaccount.oauthresource.persistence.entity.TransactionEntity;
import com.bankaccount.oauthresource.persistence.mapper.TransactionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
public class TransactionMapperTest {

    @Autowired
    private TransactionMapper transactionMapper;

    @Test
    @DisplayName("Should transform the entity information to domain information")
    public void testEntityToDomain() {
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .idTransaction(432l)
                .idAccount(5l)
                .transactionAmount(new BigDecimal(333))
                .transactionType(TransactionType.DEPOSIT)
                .transactionTimestamp(LocalDateTime.of(2022, Month.DECEMBER, 1, 13, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .accountEntity(null)
                .build();

        TransactionDomain transactionDomain = transactionMapper.toTransactionDomain(transactionEntity);

        assertAll(
                () -> assertEquals(transactionEntity.getIdTransaction(), transactionDomain.getIdTransaction()),
                () -> assertEquals(transactionEntity.getIdAccount(), transactionDomain.getIdAccount()),
                () -> assertEquals(transactionEntity.getTransactionAmount(), transactionDomain.getTransactionAmount()),
                () -> assertEquals(transactionEntity.getTransactionTimestamp(), transactionDomain.getTransactionTimestamp())
        );
    }

    @Test
    @DisplayName("Should transform the domain information to entity information")
    public void testDomainToEntity() {
        TransactionDomain transactionDomain = TransactionDomain.builder()
                .idTransaction(4324l)
                .idAccount(986l)
                .transactionAmount(new BigDecimal(5646.99))
                .transactionTimestamp(Instant.now())
                .build();

        TransactionEntity transactionEntity = transactionMapper.toTransactionEntity(transactionDomain);

        assertAll(
                () -> assertEquals(transactionDomain.getIdTransaction(), transactionEntity.getIdTransaction()),
                () -> assertEquals(transactionDomain.getIdAccount(), transactionEntity.getIdAccount()),
                () -> assertEquals(transactionDomain.getTransactionAmount(), transactionEntity.getTransactionAmount()),
                () -> assertEquals(transactionDomain.getTransactionTimestamp(), transactionEntity.getTransactionTimestamp())
        );
    }
}
