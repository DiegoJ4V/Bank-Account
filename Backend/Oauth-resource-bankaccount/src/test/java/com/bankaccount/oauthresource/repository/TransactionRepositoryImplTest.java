package com.bankaccount.oauthresource.repository;

import com.bankaccount.oauthresource.domain.TransactionDomain;
import com.bankaccount.oauthresource.domain.repository.TransactionRepository;
import com.bankaccount.oauthresource.persistence.crud.TransactionCrudRepository;
import com.bankaccount.oauthresource.persistence.entity.TransactionEntity;
import com.bankaccount.oauthresource.persistence.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
class TransactionRepositoryImplTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @MockBean
    private TransactionCrudRepository transactionCrudRepository;

    private List<TransactionEntity> transactionEntityList;

    @BeforeEach
    void setUp() {
        TransactionEntity transactionEntity1 = TransactionEntity.builder()
                .idTransaction(432l)
                .idAccount(343l)
                .transactionAmount(new BigDecimal("87523.45"))
                .transactionTimestamp(LocalDateTime.of(2022, Month.OCTOBER, 12, 13, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        TransactionEntity transactionEntity2 = TransactionEntity.builder()
                .idTransaction(342l)
                .idAccount(343l)
                .transactionAmount(new BigDecimal("7657.75"))
                .transactionTimestamp(LocalDateTime.of(2022, Month.JANUARY, 20, 20, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        TransactionEntity transactionEntity3 = TransactionEntity.builder()
                .idTransaction(6546l)
                .idAccount(84l)
                .transactionAmount(new BigDecimal("6546734.76"))
                .transactionTimestamp(LocalDateTime.of(2022, Month.DECEMBER, 11, 13, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        transactionEntityList = Arrays.asList(transactionEntity1, transactionEntity2, transactionEntity3);
    }

    @Test
    @DisplayName("Should return one transactionEntity with the specific id of the database and the mapper should transform to transactionDomain")
    void getTransactionById() {
        Mockito.when(transactionCrudRepository.findById(6546l))
                .thenReturn(Optional.of(transactionEntityList.get(2)));

        TransactionDomain transactionDomain = transactionRepository.getTransactionById(6546l).get();

        assertEquals(6546l, transactionDomain.getIdTransaction());
    }

    @Test
    @DisplayName("Should return all transactionEntities with the specific idAccount of the database and the mapper should transform to transactionDomain")
    void getByIdAccount() {
        Mockito.when(transactionCrudRepository.findByIdAccount(343l))
                .thenReturn(Arrays.asList(transactionEntityList.get(0), transactionEntityList.get(1)));

        List<TransactionDomain> transactionDomainList = transactionRepository.getByIdAccount(343l);

        assertAll(
                () -> assertEquals(Arrays.asList(432l, 342l), transactionDomainList.stream().map(TransactionDomain::getIdTransaction).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(343l, 343l), transactionDomainList.stream().map(TransactionDomain::getIdAccount).collect(Collectors.toList()))
        );
    }

    @Test
    @DisplayName("Should return all transactionEntities of the database with the specific idAccount and with the timestamp condition and the mapper should transform to transactionDomain")
    void getByAfterTransactionTimeAndIdAccount() {
        Mockito.when(transactionCrudRepository.findByTransactionTimestampAfterAndIdAccount(Instant.parse("2021-10-09T20:10:00Z"), 84l))
                .thenReturn(Arrays.asList(transactionEntityList.get(2)));

        List<TransactionDomain> transactionDomainList = transactionRepository.getByTimeAndIdAccount(Instant.parse("2021-10-09T20:10:00Z"), 84l);

        assertAll(
                () -> assertEquals(Arrays.asList(6546l), transactionDomainList.stream().map(TransactionDomain::getIdTransaction).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(84l), transactionDomainList.stream().map(TransactionDomain::getIdAccount).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList("6546734.76"), transactionDomainList.stream().map(transaction -> transaction.getTransactionAmount().toString()).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(transactionEntityList.get(2).getTransactionTimestamp()), transactionDomainList.stream().map(TransactionDomain::getTransactionTimestamp).collect(Collectors.toList()))
        );
    }

    @Test
    @DisplayName("Should save one transactionEntity in the database and return it with the mapper to transactionDomain")
    void saveTransaction() {
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .idTransaction(87658l)
                .idAccount(34654363l)
                .transactionAmount(new BigDecimal("6545643.45"))
                .transactionTimestamp(LocalDateTime.of(2065, Month.OCTOBER, 12, 13, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        Mockito.when(transactionCrudRepository.save(ArgumentMatchers.any())).thenReturn(transactionEntity);

        TransactionDomain transactionSave = transactionRepository.saveTransaction(transactionEntity);

        assertAll(
                () -> assertEquals(transactionEntity.getIdTransaction(), transactionSave.getIdTransaction()),
                () -> assertEquals(transactionEntity.getIdAccount(), transactionSave.getIdAccount()),
                () -> assertEquals(transactionEntity.getTransactionAmount(), transactionSave.getTransactionAmount()),
                () -> assertEquals(transactionEntity.getTransactionTimestamp(), transactionSave.getTransactionTimestamp())
        );
    }
}