package com.bankaccount.back.web;

import com.bankaccount.back.domain.service.TransactionService;
import com.bankaccount.back.domain.service.TransactionTypeService;
import com.bankaccount.back.exception.NotFoundException;
import com.bankaccount.back.persistence.entity.TransactionEntity;
import com.bankaccount.back.web.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionTypeService transactionTypeService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionEntity> getTransactionById(@PathVariable long id) {
        return transactionService.getTransactionById(id)
                .map(transaction -> new ResponseEntity<>(transaction, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/account")
    public ResponseEntity<Page<TransactionEntity>> getByIdAccount(@RequestParam(name = "id") int idAccount, @RequestParam int page) throws NotFoundException {
        Page<TransactionEntity> pageable = transactionService.getByIdAccount(idAccount, page).get();

        if (!pageable.isEmpty()) {
            return new ResponseEntity<>(pageable, HttpStatus.OK);
        }

        throw new NotFoundException("Transactions not found");
    }

    @GetMapping("/year")
    public ResponseEntity<List<TransactionEntity>> getByYearAndIdAccount(@RequestParam(name = "id") int idAccount, @RequestParam int year) {
        List<TransactionEntity> transactionDomainList = transactionService.getByIdAccountAndYear(idAccount, year);

        if (!transactionDomainList.isEmpty()) {
            return new ResponseEntity<>(transactionDomainList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/save", consumes = {"application/json"})
    public ResponseEntity<?> saveDepositTransaction(@RequestBody @Valid TransactionDto transactionDto) throws Exception {
        return new ResponseEntity<>(transactionTypeService.saveTransaction(transactionDto, false), HttpStatus.CREATED);
    }
}
