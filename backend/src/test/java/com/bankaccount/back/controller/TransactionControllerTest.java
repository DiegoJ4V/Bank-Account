package com.bankaccount.back.controller;

import com.bankaccount.back.constants.AccountRoles;
import com.bankaccount.back.constants.TransactionType;
import com.bankaccount.back.domain.service.TransactionService;
import com.bankaccount.back.domain.service.TransactionTypeService;
import com.bankaccount.back.persistence.entity.TransactionEntity;
import com.bankaccount.back.web.TransactionController;
import com.bankaccount.back.web.config.JwtUtil;
import com.bankaccount.back.web.dto.DateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

   private static final String ADMIN = AccountRoles.ADMIN.toString();
   private static final String USER = AccountRoles.USER.toString();

   @Autowired
   private MockMvc mockMvc;

   @MockBean
   private TransactionService transactionService;

   @MockBean
   private TransactionTypeService onlyForBean;

   @MockBean
   private JwtUtil jwtUtil;

   private List<TransactionEntity> transactionEntityList;

   @BeforeEach
   void setUp() {
      TransactionEntity transactionEntity1 = TransactionEntity.builder()
              .idTransaction(564326L)
              .idTransferAccount(312421)
              .receiverName("Maria")
              .transactionAmount(new BigDecimal("87523.45"))
              .transactionType(TransactionType.DEPOSIT)
              .transactionTimestamp(LocalDateTime.of(2022, Month.OCTOBER, 12, 13, 12, 0))
              .build();

      TransactionEntity transactionEntity2 = TransactionEntity.builder()
              .idTransaction(87686L)
              .idTransferAccount(312421)
              .receiverName("Maria")
              .transactionAmount(new BigDecimal("7657.75"))
              .transactionType(TransactionType.WIRE_TRANSFER)
              .transactionTimestamp(LocalDateTime.of(2022, Month.JANUARY, 20, 20, 12, 12))
              .build();

      TransactionEntity transactionEntity3 = TransactionEntity.builder()
              .idTransaction(6546L)
              .idTransferAccount(34535)
              .receiverName("Maria")
              .transactionAmount(new BigDecimal("6546734.76"))
              .transactionType(TransactionType.ONLINE_PAYMENT)
              .transactionTimestamp(LocalDateTime.of(2022, Month.DECEMBER, 11, 13, 12, 0))
              .build();

      TransactionEntity transactionEntity4 = TransactionEntity.builder()
              .idTransaction(67582L)
              .idTransferAccount(312421)
              .receiverName("Maria")
              .transactionAmount(new BigDecimal("5464.76"))
              .transactionType(TransactionType.ONLINE_PAYMENT)
              .transactionTimestamp(LocalDateTime.of(2022, Month.FEBRUARY, 11, 11, 12, 11))
              .build();

      transactionEntityList = Arrays.asList(transactionEntity1, transactionEntity2, transactionEntity3, transactionEntity4);
   }

   @Test
   @DisplayName("Should return one transactionEntity in json format with a specific id using the service or return a not found if authorized")
   void getTransactionById() {
      Mockito.when(transactionService.getTransactionById(67582L))
              .thenReturn(Optional.of(transactionEntityList.get(3)));

      assertAll(
              () -> mockMvc.perform(get("/transactions/67582")
                              .contentType(MediaType.APPLICATION_JSON)
                              .with(user("user").roles(ADMIN)))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.idTransaction")
                              .value(transactionEntityList.get(3).getIdTransaction()))
                      .andExpect(jsonPath("$.transactionAmount")
                              .value(transactionEntityList.get(3).getTransactionAmount()))
                      .andExpect(jsonPath("$.transactionType")
                              .value(transactionEntityList.get(3).getTransactionType().toString()))
                      .andExpect(jsonPath("$.transactionTimestamp")
                              .value(transactionEntityList.get(3).getTransactionTimestamp().toString())),

              () -> mockMvc.perform(get("/transactions/7658")
                              .contentType(MediaType.APPLICATION_JSON)
                              .with(user("user").roles(ADMIN)))
                      .andExpect(status().isNotFound()),

              () -> mockMvc.perform(get("/transactions/54")
                              .contentType(MediaType.APPLICATION_JSON))
                      .andExpect(status().isUnauthorized())
      );
   }

   @Test
   @DisplayName("Should return all transactionEntities in json format with a specific idAccount using the service or return a not found if authorized")
   void getByIdAccount() {
      Mockito.when(transactionService.getByIdAccount(885748, 0))
              .thenReturn(Optional.of(new PageImpl<>(
                      List.of(transactionEntityList.get(0), transactionEntityList.get(1), transactionEntityList.get(2)))));

      Mockito.when(transactionService.getByIdAccount(885748, 1))
              .thenReturn(Optional.of(new PageImpl<>(List.of())));

      assertAll(
              () -> mockMvc.perform(get("/transactions/account")
                              .param("id", "885748")
                              .param("page", "0")
                              .contentType(MediaType.APPLICATION_JSON)
                              .with(user("user").roles(USER)))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.totalElements")
                              .value(3))

                      .andExpect(jsonPath("$.content[0].idTransaction")
                              .value(transactionEntityList.get(0).getIdTransaction()))
                      .andExpect(jsonPath("$.content[0].idTransferAccount")
                              .value(transactionEntityList.get(0).getIdTransferAccount()))
                      .andExpect(jsonPath("$.content[0].transactionAmount")
                              .value(transactionEntityList.get(0).getTransactionAmount()))
                      .andExpect(jsonPath("$.content[0].transactionType")
                              .value(transactionEntityList.get(0).getTransactionType().toString()))

                      .andExpect(jsonPath("$.content[1].idTransaction")
                              .value(transactionEntityList.get(1).getIdTransaction()))
                      .andExpect(jsonPath("$.content[1].idTransferAccount")
                              .value(transactionEntityList.get(1).getIdTransferAccount()))
                      .andExpect(jsonPath("$.content[1].transactionAmount")
                              .value(transactionEntityList.get(1).getTransactionAmount()))
                      .andExpect(jsonPath("$.content[1].transactionType")
                              .value(transactionEntityList.get(1).getTransactionType().toString()))

                      .andExpect(jsonPath("$.content[2].idTransaction")
                              .value(transactionEntityList.get(2).getIdTransaction()))
                      .andExpect(jsonPath("$.content[2].idTransferAccount")
                              .value(transactionEntityList.get(2).getIdTransferAccount()))
                      .andExpect(jsonPath("$.content[2].transactionAmount")
                              .value(transactionEntityList.get(2).getTransactionAmount()))
                      .andExpect(jsonPath("$.content[2].transactionType")
                              .value(transactionEntityList.get(2).getTransactionType().toString())),

              () -> mockMvc.perform(get("/transactions/account")
                              .param("id", "885748")
                              .param("page", "1")
                              .contentType(MediaType.APPLICATION_JSON)
                              .with(user("user").roles(USER)))
                      .andExpect(status().isNotFound()),

              () -> mockMvc.perform(get("/transactions/account/885748")
                              .contentType(MediaType.APPLICATION_JSON))
                      .andExpect(status().isUnauthorized())
      );
   }

   @Test
   @DisplayName("Should return all transactionEntities in json format with a specific idAccount and name using the service or return a not found if authorized")
   void getByFilterWithName() {
      DateDto dateDto = new DateDto(0, null, null);
      Mockito.when(transactionService.getByFilter(885748, null, "ma", dateDto, 0))
              .thenReturn(Optional.of(new PageImpl<>(
                      List.of(transactionEntityList.get(1), transactionEntityList.get(2)))));

      Mockito.when(transactionService.getByFilter(885748, null, "ma", dateDto, 1))
              .thenReturn(Optional.of(new PageImpl<>(List.of())));

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      assertAll(
              () -> mockMvc.perform(post("/transactions/filter")
                              .param("id", "885748")
                              .param("name", "ma")
                              .param("page", "0")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(dateDto))
                              .with(user("user").roles(USER))
                              .with(csrf()))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.totalElements")
                              .value(2))

                      .andExpect(jsonPath("$.content[0].idTransaction")
                              .value(transactionEntityList.get(1).getIdTransaction()))
                      .andExpect(jsonPath("$.content[0].idTransferAccount")
                              .value(transactionEntityList.get(1).getIdTransferAccount()))
                      .andExpect(jsonPath("$.content[0].transactionAmount")
                              .value(transactionEntityList.get(1).getTransactionAmount()))
                      .andExpect(jsonPath("$.content[0].transactionType")
                              .value(transactionEntityList.get(1).getTransactionType().toString()))

                      .andExpect(jsonPath("$.content[1].idTransaction")
                              .value(transactionEntityList.get(2).getIdTransaction()))
                      .andExpect(jsonPath("$.content[1].idTransferAccount")
                              .value(transactionEntityList.get(2).getIdTransferAccount()))
                      .andExpect(jsonPath("$.content[1].transactionAmount")
                              .value(transactionEntityList.get(2).getTransactionAmount()))
                      .andExpect(jsonPath("$.content[1].transactionType")
                              .value(transactionEntityList.get(2).getTransactionType().toString())),

              () -> mockMvc.perform(post("/transactions/filter")
                              .param("id", "885748")
                              .param("name", "ma")
                              .param("page", "1")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(dateDto))
                              .with(user("user").roles(USER))
                              .with(csrf()))
                      .andExpect(status().isNotFound()),

              () -> mockMvc.perform(post("/transactions/filter")
                              .contentType(MediaType.APPLICATION_JSON)
                              .with(csrf()))
                      .andExpect(status().isUnauthorized())
      );
   }

   @Test
   @DisplayName("Should return all transactionEntities in json format with a specific idAccount and with the timestamp condition using the service or return a not found if authorized")
   void getByFilterWithDate() {
      DateDto dateDto = new DateDto(2021, Month.JANUARY, null);
      Mockito.when(transactionService.getByFilter(54365, null, "ma", dateDto, 0))
              .thenReturn(Optional.of(new PageImpl<>(
                      Collections.singletonList(transactionEntityList.get(1)))));

      Mockito.when(transactionService.getByFilter(54365, null, "ma", dateDto,1))
              .thenReturn(Optional.of(new PageImpl<>(List.of())));

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      assertAll(
              () -> mockMvc.perform(post("/transactions/filter")
                              .param("id", "54365")
                              .param("name", "ma")
                              .param("page", "0")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(dateDto))
                              .with(user("user").roles(USER))
                              .with(csrf()))
                      .andExpect(status().isOk())
                      .andExpect(jsonPath("$.totalElements").value(1))
                      .andExpect(jsonPath("$.content[0].idTransaction")
                              .value(transactionEntityList.get(1).getIdTransaction()))
                      .andExpect(jsonPath("$.content[0].idTransferAccount")
                              .value(transactionEntityList.get(1).getIdTransferAccount()))
                      .andExpect(jsonPath("$.content[0].receiverName")
                              .value(transactionEntityList.get(1).getReceiverName()))
                      .andExpect(jsonPath("$.content[0].transactionAmount")
                              .value(transactionEntityList.get(1).getTransactionAmount()))
                      .andExpect(jsonPath("$.content[0].transactionType")
                              .value(transactionEntityList.get(1).getTransactionType().toString()))
                      .andExpect(jsonPath("$.content[0].transactionTimestamp")
                              .value(transactionEntityList.get(1).getTransactionTimestamp().toString())),

              () -> mockMvc.perform(post("/transactions/filter")
                              .param("id", "54365")
                              .param("name", "ma")
                              .param("page", "1")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(dateDto))
                              .with(user("user").roles(USER))
                              .with(csrf()))
                      .andExpect(status().isNotFound()),

              () -> mockMvc.perform(post("/transactions/filter")
                              .param("id", "423")
                              .param("name", "ma")
                              .param("page", "0")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(new DateDto(2034, Month.JANUARY, null)))
                              .with(csrf()))
                      .andExpect(status().isUnauthorized())
      );
   }
}
