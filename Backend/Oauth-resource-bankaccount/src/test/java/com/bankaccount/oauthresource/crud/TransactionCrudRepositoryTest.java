package com.bankaccount.oauthresource.crud;

import com.bankaccount.oauthresource.constants.TransactionType;
import com.bankaccount.oauthresource.persistence.crud.TransactionCrudRepository;
import com.bankaccount.oauthresource.persistence.entity.TransactionEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("dev")
@Sql("/db/bankaccount_data.sql")
class TransactionCrudRepositoryTest {

    @Autowired
    private TransactionCrudRepository transactionCrudRepository;

    @Test
    @DisplayName("Should return a transactionEntity with a specific id of the data.sql")
    public void findById() {
        TransactionEntity transactionEntity = transactionCrudRepository.findById(3L).get();

        Optional<TransactionEntity> errorTransactionEntity = transactionCrudRepository.findById(453L);

        assertAll(
                () -> assertFalse(errorTransactionEntity.isPresent()),
                () -> assertThat(transactionEntity.getIdTransaction()).isEqualTo(3l),
                () -> assertThat(transactionEntity.getIdAccount()).isEqualTo(1),
                () -> assertThat(transactionEntity.getTransactionAmount().toString()).isEqualTo("400.00"),
                () -> Assertions.assertThat(transactionEntity.getTransactionType()).isEqualTo(TransactionType.ONLINE_PAYMENT),
                () -> assertThat(transactionEntity.getTransactionTimestamp()).isEqualTo(LocalDateTime.of(2022, 10, 9, 20, 10, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
        );
    }

    @Test
    @DisplayName("Should return all transactionEntities with a specific idAccount of the data.sql")
    void findByIdAccount() {
        List<TransactionEntity> transactionEntityList = transactionCrudRepository.findByIdAccount(2l);

        List<TransactionEntity> errorTransactionEntity = transactionCrudRepository.findByIdAccount(453L);

        assertAll(
                () -> assertTrue(errorTransactionEntity.isEmpty()),
                () -> assertThat(transactionEntityList.size()).isEqualTo(2),
                () -> assertEquals(Arrays.asList(1l, 4l), transactionEntityList.stream().map(TransactionEntity::getIdTransaction).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(2l, 2l), transactionEntityList.stream().map(TransactionEntity::getIdAccount).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList("6000.00", "0.00"), transactionEntityList.stream().map(transaction -> transaction.getTransactionAmount().toString()).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(TransactionType.WIRE_TRANSFER, TransactionType.CHECK),
                        transactionEntityList.stream().map(TransactionEntity::getTransactionType).collect(Collectors.toList())),

                () -> assertEquals(Arrays.asList(LocalDateTime.of(2022, Month.OCTOBER, 9, 20, 10, 00)
                                        .atZone(ZoneId.of("America/Mexico_City")).toInstant(),
                                LocalDateTime.of(2022, Month.OCTOBER, 9, 20, 10, 00)
                                        .atZone(ZoneId.of("America/Mexico_City")).toInstant()),
                        transactionEntityList.stream().map(TransactionEntity::getTransactionTimestamp).collect(Collectors.toList()))
        );
    }

    @Test
    @DisplayName("Should return all transactionEntities with one timestamp after the specified of the data.sql")
    void findByTransactionTimestampAfterAndIdAccount() {
        Instant instant = LocalDateTime.of(2021, Month.OCTOBER, 9, 20, 10, 00)
                .atZone(ZoneId.of("America/Mexico_City")).toInstant();

        List<TransactionEntity> transactionEntityList = transactionCrudRepository.findByTransactionTimestampAfterAndIdAccount(
                instant, 1);

        List<TransactionEntity> errorTransactionEntity = transactionCrudRepository.findByTransactionTimestampAfterAndIdAccount(
                LocalDateTime.of(2022, Month.OCTOBER, 9, 20, 10, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant(), 1);

        assertAll(
                () -> assertTrue(errorTransactionEntity.isEmpty()),
                () -> assertThat(transactionEntityList.size()).isEqualTo(1),
                () -> assertEquals(Arrays.asList(3l), transactionEntityList.stream().map(TransactionEntity::getIdTransaction).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(1l), transactionEntityList.stream().map(TransactionEntity::getIdAccount).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList("400.00"), transactionEntityList.stream().map(transaction -> transaction.getTransactionAmount().toString()).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(TransactionType.ONLINE_PAYMENT),
                        transactionEntityList.stream().map(TransactionEntity::getTransactionType).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(LocalDateTime.of(2022, Month.OCTOBER, 9, 20, 10, 00)
                                .atZone(ZoneId.of("America/Mexico_City")).toInstant()),
                        transactionEntityList.stream().map(TransactionEntity::getTransactionTimestamp).collect(Collectors.toList()))
        );
    }

    @Test
    @DisplayName("Should save a transaction in the database")
    public void saveAccount() {
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .idAccount(5l)
                .transactionAmount(new BigDecimal(333))
                .transactionType(TransactionType.DEPOSIT)
                .transactionTimestamp(LocalDateTime.of(2022, Month.DECEMBER, 1, 13, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        TransactionEntity transactionSave = transactionCrudRepository.save(transactionEntity);

        assertAll(
                () -> assertThat(transactionEntity.getIdTransaction()).isEqualTo(5l),
                () -> assertEquals(transactionEntity.getIdAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionEntity.getTransactionAmount(), transactionSave.getTransactionAmount()),
                () -> org.junit.jupiter.api.Assertions.assertEquals(transactionEntity.getTransactionType(), transactionSave.getTransactionType()),
                () -> assertEquals(transactionEntity.getTransactionTimestamp(), transactionSave.getTransactionTimestamp())
        );
    }
}