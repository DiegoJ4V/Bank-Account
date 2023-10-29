package com.bankaccount.back.web;

import com.bankaccount.back.domain.event.RegistrationCompleteEvent;
import com.bankaccount.back.domain.service.AccountService;
import com.bankaccount.back.domain.service.EmailService;
import com.bankaccount.back.domain.service.TokenService;
import com.bankaccount.back.exception.NotAllowedException;
import com.bankaccount.back.exception.NotFoundException;
import com.bankaccount.back.helpers.Messages;
import com.bankaccount.back.persistence.entity.AccountEntity;
import com.bankaccount.back.persistence.entity.TokenEntity;
import com.bankaccount.back.web.config.EnvConfigProperties;
import com.bankaccount.back.web.config.JwtUtil;
import com.bankaccount.back.web.dto.AccountDto;
import com.bankaccount.back.web.dto.LoginDto;
import com.bankaccount.back.web.dto.PasswordDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EnvConfigProperties configProperties;

    @PostMapping(value = "/login", consumes = {"application/json"})
    public ResponseEntity<String> login(@RequestBody @Valid LoginDto loginDto) {
        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        Authentication authentication = authenticationManager.authenticate(login);
        String jwt = jwtUtil.create(loginDto.email());

        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = {"application/json"})
    public ResponseEntity<String> registerAccount(
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
            @RequestBody @Valid AccountDto accountDto) throws NotAllowedException {

        if (!accountDto.password().equals(accountDto.matchingPassword())) {
            return new ResponseEntity<>(
                    Messages.getMessageForLocale("controller.auth.register.error", locale),
                    HttpStatus.BAD_REQUEST);
        }

        AccountEntity account = accountService.saveAccount(accountDto, locale);

        publisher.publishEvent(new RegistrationCompleteEvent(account, locale));

        return new ResponseEntity<>(
                Messages.getMessageForLocale("controller.auth.register.success", locale),
                HttpStatus.CREATED);
    }

    @GetMapping("/verify-registration")
    public ResponseEntity<String> verifyRegistration(
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
            @RequestParam String token) {
        String result = tokenService.validateVerification(token);
        if (result.equalsIgnoreCase("valid")) {
            tokenService.deleteToken(token);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/resend-token")
    public String resendToken(
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
            @RequestParam String type,
            @RequestParam("token") String oldToken) {
        TokenEntity tokenEntity = tokenService.generateNewToken(oldToken);

        AccountEntity account = tokenEntity.getAccountEntity();

        if (type.equalsIgnoreCase("verification")) {
            resendVerificationTokenEmail(tokenEntity, account);
        } else if (type.equalsIgnoreCase("password")) {
            passwordResetTokenEmail(tokenEntity.getToken(), account);
        }

        return Messages.getMessageForLocale("controller.auth.resend", locale);
    }

    @GetMapping("/reset-password/{email}")
    public void resetPassword(@PathVariable String email) {
        Optional<AccountEntity> optionalAccount = accountService.getAccountByEmail(email);

        if (optionalAccount.isPresent()) {
            AccountEntity accountEntity = optionalAccount.get();
            String token = UUID.randomUUID().toString();
            accountService.saveToken(token, accountEntity);

            passwordResetTokenEmail(token, accountEntity);
        }
    }

    @PostMapping("/save-password")
    private ResponseEntity<String> savePassword(
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
            @RequestParam String token,
            @RequestBody @Valid PasswordDto passwordDto) throws NotFoundException {
        String result = tokenService.validateToken(token);

        if (result.equalsIgnoreCase("valid")) {
            Optional<AccountEntity> accountEntity = tokenService.getAccountByToken(token);
            if (accountEntity.isPresent()) {
                tokenService.deleteToken(token);
                accountService.updatePassword(passwordDto.newPassword(), passwordDto.idAccount());

                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/secure/change-name")
    public ResponseEntity<Map<String, String>> changeName(
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
            @RequestParam String name,
            @RequestBody @Valid PasswordDto passwordDto) throws NotFoundException {
        Map<String, String> response = new HashMap<>();
        if (name.isEmpty()) {
            response.put("name", Messages.getMessageForLocale("controller.auth.change-name.error", locale));
            return ResponseEntity.badRequest().body(response);
        }

        String result = accountService.changeName(name, passwordDto, locale);

        if (result.contains(Messages.getMessageForLocale("service.account.change-name.success", locale))) {
            response.put("result", result);
            return ResponseEntity.ok().body(response);
        }

        response.put("newPassword", result);
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/secure/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
            @RequestBody @Valid PasswordDto passwordDto) throws NotFoundException {
        Map<String, String> response = new HashMap<>();
        String result = accountService.changePassword(passwordDto, locale);

        if (result.contains(Messages.getMessageForLocale("service.account.change-password.success", locale))) {
            response.put("result", result);
            return ResponseEntity.ok().body(response);
        }

        response.put("oldPassword", result);
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/secure/change-email")
    public ResponseEntity<Map<String, String>> changeEmail(
            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
            @RequestBody @Valid PasswordDto passwordDto) throws NotFoundException, NotAllowedException {
        Map<String, String> response = new HashMap<>();
        String result = accountService.changeEmail(passwordDto, locale);

        if (result.contains(Messages.getMessageForLocale("service.account.change-email.success", locale))) {
            response.put("result", result);
            return ResponseEntity.ok().body(response);
        }

        response.put("newPassword", result);
        return ResponseEntity.badRequest().body(response);
    }

    private void passwordResetTokenEmail(String token, AccountEntity accountEntity) {
        String url = configProperties.client() + "/save-password?token=" + token
                + "&id=" + accountEntity.getIdAccount();

        emailService.sendEmail(
                accountEntity.getEmail(),
                "Reset password",
                "Click the link to reset your password: " + url);
    }

    private void resendVerificationTokenEmail(TokenEntity token, AccountEntity account) {
        String url = configProperties.client() + "/verify-registration?token=" + token.getToken() +
                "&traduction=TOKEN_REGISTER&email=" + account.getEmail();

        emailService.sendEmail(
                account.getEmail(),
                "Verification Token resend",
                "Click the link to verify your account: " + url);
    }
}
