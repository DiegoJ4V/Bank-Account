package com.bankaccount.back.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "verification_token")
public class VerificationToken {

   private static final int EXPIRATION_TIME = 10;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id_token")
   private Long idToken;

   @Column(nullable = false)
   private String token;

   @Column(name = "expiration_time", nullable = false)
   private Date expirationTime;

   @OneToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "id_account", nullable = false)
   private AccountEntity accountEntity;

   public static VerificationTokenBuilder builder() {
      return new CustomBuilder();
   }

   private static class CustomBuilder extends VerificationTokenBuilder {

      public VerificationToken build() {
         super.expirationTime = calculateExpirationDate();
         return super.build();
      }

      private Date calculateExpirationDate() {
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(new Date().getTime());
         calendar.add(Calendar.MINUTE, VerificationToken.EXPIRATION_TIME);
         return new Date(calendar.getTime().getTime());
      }
   }
}
