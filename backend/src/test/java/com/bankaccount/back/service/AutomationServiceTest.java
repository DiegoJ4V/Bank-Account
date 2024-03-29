package com.bankaccount.back.service;

import com.bankaccount.back.domain.repository.AccountRepository;
import com.bankaccount.back.domain.repository.AutomationRepository;
import com.bankaccount.back.domain.service.AutomationService;
import com.bankaccount.back.exception.NotFoundException;
import com.bankaccount.back.persistence.entity.AccountEntity;
import com.bankaccount.back.persistence.entity.AutomationEntity;
import com.bankaccount.back.web.dto.AutomationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class AutomationServiceTest {

   @Autowired
   private AutomationService automationService;

   @MockBean
   private AutomationRepository automationRepository;

   @MockBean
   private AccountRepository accountRepository;

   private List<AutomationEntity> automationEntityList;

   @BeforeEach
   void setUp() {
      AutomationEntity automationEntity1 = AutomationEntity.builder()
              .idAutomation(43L)
              .idAccount(32)
              .name("For testing")
              .idTransferAccount(12)
              .hoursToNextExecution(4)
              .executionTime(LocalDateTime.of(2022, Month.DECEMBER, 11, 13, 12, 0))
              .status(true)
              .build();

      AutomationEntity automationEntity2 = AutomationEntity.builder()
              .idAutomation(12L)
              .idAccount(32)
              .name("For testing")
              .idTransferAccount(12)
              .hoursToNextExecution(4)
              .executionTime(LocalDateTime.of(2022, Month.DECEMBER, 11, 13, 12, 0))
              .status(false)
              .build();

      AutomationEntity automationEntity3 = AutomationEntity.builder()
              .idAutomation(23L)
              .idAccount(32)
              .name("For testing")
              .idTransferAccount(12)
              .hoursToNextExecution(4)
              .executionTime(LocalDateTime.of(2022, Month.DECEMBER, 11, 13, 12, 0))
              .status(false)
              .build();

      AutomationEntity automationEntity4 = AutomationEntity.builder()
              .idAutomation(76L)
              .idAccount(23)
              .name("For testing")
              .idTransferAccount(12)
              .hoursToNextExecution(4)
              .executionTime(LocalDateTime.of(2022, Month.DECEMBER, 11, 13, 12, 0))
              .status(true)
              .build();

      automationEntityList = List.of(automationEntity1, automationEntity2, automationEntity3, automationEntity4);
   }

   @Test
   @DisplayName("Should return one automationEntity with the specific id using the repository")
   void getAutomationById() {
      Mockito.when(automationRepository.getAutomationById(23L))
              .thenReturn(Optional.of(automationEntityList.get(2)));

      AutomationEntity automationEntity = automationService.getAutomationById(23L).get();

      assertEquals(23L, automationEntity.getIdAutomation());
   }

   @Test
   @DisplayName("Should return all automationEntity with the specific idAccount using the repository")
   void getByIdAccount() throws Exception {
      Mockito.when(automationRepository.getByIdAccount(32))
              .thenReturn(List.of(automationEntityList.get(0), automationEntityList.get(1), automationEntityList.get(2)));

      List<AutomationEntity> automationEntityByAccount = automationService.getByIdAccount(32);

      assertAll(
              () -> assertThat(automationEntityByAccount.size()).isEqualTo(3),
              () -> assertEquals(List.of(43L, 12L, 23L), automationEntityByAccount.stream().map(AutomationEntity::getIdAutomation).toList()),
              () -> assertEquals(List.of(32, 32, 32), automationEntityByAccount.stream().map(AutomationEntity::getIdAccount).toList()),
              () -> Mockito.verify(automationRepository, Mockito.times(1))
                      .getByIdAccountAndStatus(32, true)
      );
   }

   @Test
   @DisplayName("Should convert one automationDto to automationEntity to send to the repository and return it")
   public void saveAutomation() throws NotFoundException {
      AutomationDto automationDto = new AutomationDto(
              54,
              "For testing",
              new BigDecimal("4324.43"),
              321,
              213
      );

      AutomationDto automationError = new AutomationDto(
              2,
              "For testing",
              new BigDecimal("4324.43"),
              765,
              213
      );

      AutomationEntity automationEntity = AutomationEntity.builder()
              .idAutomation(3123L)
              .idAccount(54)
              .name("For testing")
              .amount(new BigDecimal("4324.43"))
              .idTransferAccount(321)
              .hoursToNextExecution(213)
              .executionTime(LocalDateTime.of(2023, Month.DECEMBER, 11, 13, 12, 0))
              .status(true)
              .build();

      Mockito.when(accountRepository.getAccountById(54))
              .thenReturn(Optional.of(AccountEntity.builder().build()));

      Mockito.when(accountRepository.getAccountById(321))
              .thenReturn(Optional.of(AccountEntity.builder().build()));

      automationService.saveAutomation(automationDto, Locale.getDefault());

      Exception exception = assertThrows(NotFoundException.class, () ->
              automationService.saveAutomation(automationError, Locale.getDefault()));

      String expectedMessage = "Account is not found";
      String actualMessage = exception.getMessage();

      assertAll(
              () -> Mockito.verify(automationRepository, Mockito.times(1)).saveAutomation(Mockito.any(AutomationEntity.class)),
              () -> assertTrue(actualMessage.contentEquals(expectedMessage))
      );
   }

   @Test
   @DisplayName("Should update an automation using the repository")
   public void updateAutomation() throws NotFoundException {
      AutomationEntity automationEntity = AutomationEntity.builder()
              .idAutomation(3123L)
              .idAccount(54)
              .name("For testing")
              .amount(new BigDecimal("4324.43"))
              .idTransferAccount(321)
              .hoursToNextExecution(213)
              .executionTime(LocalDateTime.of(2023, Month.DECEMBER, 11, 13, 12, 0))
              .status(true)
              .build();

      Mockito.when(automationRepository.existsById(3123L)).thenReturn(true);
      Mockito.when(accountRepository.idExist(54)).thenReturn(true);
      Mockito.when(accountRepository.idExist(321)).thenReturn(true);

      automationService.updateAutomation(automationEntity, Locale.getDefault());

      Exception exceptionAutomation = assertThrows(NotFoundException.class, () ->
              automationService.updateAutomation(
                      AutomationEntity.builder().idAutomation(4324234L).build(), Locale.getDefault()));

      Exception exceptionAccount = assertThrows(NotFoundException.class, () ->
              automationService.updateAutomation(
                      AutomationEntity.builder().idAutomation(3123L).idAccount(54353).build(), Locale.getDefault()));

      assertAll(
              () -> Mockito.verify(automationRepository, Mockito.times(1)).saveAutomation(Mockito.any(AutomationEntity.class)),
              () -> assertEquals(exceptionAutomation.getMessage(), "Automation is not found"),
              () -> assertEquals(exceptionAccount.getMessage(), "Account is not found")
      );
   }

   @Test
   @DisplayName("Should delete an automationEntity with its id using the repository")
   public void deleteById() throws NotFoundException {
      Mockito.when(automationRepository.existsById(31L)).thenReturn(true);

      automationService.deleteById(31L, Locale.getDefault());

      Exception exceptionAutomation = assertThrows(NotFoundException.class, () ->
              automationService.deleteById(53426L, Locale.getDefault()));

      assertAll(
              () -> Mockito.verify(automationRepository, Mockito.times(1)).deleteById(Mockito.isA(Long.class)),
              () -> assertEquals(exceptionAutomation.getMessage(), "Automation is not found")
      );
   }
}
