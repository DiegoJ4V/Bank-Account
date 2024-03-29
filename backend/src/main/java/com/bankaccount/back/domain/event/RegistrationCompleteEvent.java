package com.bankaccount.back.domain.event;

import com.bankaccount.back.persistence.entity.AccountEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

/**
 * Class to handle the register endpoint has been complete.
 * <p>Extends {@link ApplicationEvent}
 */
@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

   private AccountEntity accountEntity;

   private Locale locale;

   /**
    * Constructor for {@link RegistrationCompleteEvent}.
    * @param accountEntity the specific account
    * @param locale the language desired
    */
   public RegistrationCompleteEvent(AccountEntity accountEntity, Locale locale) {
      super(accountEntity);
      this.accountEntity = accountEntity;
      this.locale = locale;
   }
}
