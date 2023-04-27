package com.bankaccount.oauthresource.service;

import com.bankaccount.oauthresource.domain.TransactionDomain;
import com.bankaccount.oauthresource.domain.repository.TransactionRepository;
import com.bankaccount.oauthresource.domain.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    private List<TransactionDomain> transactionDomainList;

    @BeforeEach
    void setUp() {
        TransactionDomain transactionDomain1 = TransactionDomain.builder()
                .idTransaction(564326l)
                .idAccount(885748l)
                .transactionAmount(new BigDecimal("87523.45"))
                .transactionTimestamp(LocalDateTime.of(2022, Month.OCTOBER, 12, 13, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        TransactionDomain transactionDomain2 = TransactionDomain.builder()
                .idTransaction(87686l)
                .idAccount(54365l)
                .transactionAmount(new BigDecimal("7657.75"))
                .transactionTimestamp(LocalDateTime.of(2022, Month.JANUARY, 20, 20, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        TransactionDomain transactionDomain3 = TransactionDomain.builder()
                .idTransaction(6546l)
                .idAccount(885748l)
                .transactionAmount(new BigDecimal("6546734.76"))
                .transactionTimestamp(LocalDateTime.of(2022, Month.DECEMBER, 11, 13, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        TransactionDomain transactionDomain4 = TransactionDomain.builder()
                .idTransaction(67582l)
                .idAccount(885748l)
                .transactionAmount(new BigDecimal("5464.76"))
                .transactionTimestamp(LocalDateTime.of(2022, Month.FEBRUARY, 11, 13, 12, 00)
                        .atZone(ZoneId.of("America/Mexico_City")).toInstant())
                .build();

        transactionDomainList = Arrays.asList(transactionDomain1, transactionDomain2, transactionDomain3, transactionDomain4);
    }

    @Test
    @DisplayName("Should return one transactionDomain with the specific id using the repository")
    void getTransactionById() {
        Mockito.when(transactionRepository.getTransactionById(6546l))
                .thenReturn(Optional.of(transactionDomainList.get(2)));

        TransactionDomain transactionDomain = transactionService.getTransactionById(6546l).get();

        assertEquals(6546l, transactionDomain.getIdTransaction());
    }

    @Test
    @DisplayName("Should return all transactionDomain with the specific idAccount using the repository")
    void getByIdAccount() {
        Mockito.when(transactionRepository.getByIdAccount(343l))
                .thenReturn(Arrays.asList(transactionDomainList.get(0), transactionDomainList.get(2), transactionDomainList.get(3)));

        List<TransactionDomain> transactionList = transactionService.getByIdAccount(343l);

        assertAll(
                () -> assertThat(transactionList.size()).isEqualTo(3),
                () -> assertEquals(Arrays.asList(564326l, 6546l, 67582l), transactionList.stream().map(TransactionDomain::getIdTransaction).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(885748l, 885748l, 885748l), transactionList.stream().map(TransactionDomain::getIdAccount).collect(Collectors.toList()))
        );
    }

    @Test
    @DisplayName("Should return all transactionDomain with the specific idAccount and with the timestamp condition using the repository")
    void getByTimeAndIdAccount() {
        Mockito.when(transactionRepository.getByTimeAndIdAccount(Instant.parse("2021-10-09T20:10:00Z"), 54365l))
                .thenReturn(Arrays.asList(transactionDomainList.get(1)));

        List<TransactionDomain> transactionList = transactionService.getByTimeAndIdAccount(Instant.parse("2021-10-09T20:10:00Z"), 54365l);

        assertAll(
                () -> assertThat(transactionList.size()).isEqualTo(1),
                () -> assertEquals(Arrays.asList(87686l), transactionList.stream().map(TransactionDomain::getIdTransaction).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(54365l), transactionList.stream().map(TransactionDomain::getIdAccount).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList("7657.75"), transactionList.stream().map(transaction -> transaction.getTransactionAmount().toString()).collect(Collectors.toList())),
                () -> assertEquals(Arrays.asList(transactionDomainList.get(1).getTransactionTimestamp()), transactionList.stream().map(TransactionDomain::getTransactionTimestamp).collect(Collectors.toList()))
        );
    }
}