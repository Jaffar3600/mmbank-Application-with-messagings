package com.cg.ws.transaction.main.controller;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cg.ws.transaction.main.account.Account;
import com.cg.ws.transaction.main.sender.Sender;
import com.cg.ws.transaction.main.service.TransactionService;
import com.cg.ws.transaction.main.transaction.Transaction;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	@Autowired
	private TransactionService service;

	@Autowired
	Sender sender;
	
	@Autowired
	RestTemplate temp;

	@PostMapping("/deposit")
	public ResponseEntity<Transaction> deposit(@RequestBody Transaction transaction) {
		Account account = new Account();
		ResponseEntity<Double> entity = temp.getForEntity(
				"http://localhost:9090/accounts/" + transaction.getAccountNumber() + "/balance", Double.class);
		Double currentBalance = entity.getBody();
		Double updateBalance = service.deposit(transaction.getAccountNumber(), transaction.getAmount(), currentBalance,
				transaction.getTransactionDetails());
		account.setAccountNumber(transaction.getAccountNumber());
		account.setCurrentBalance(updateBalance);
		sender.send(account);
		
		/*
		 * temp.put( "http://localhost:9090/accounts/" + transaction.getAccountNumber()
		 * + "?currentBalance=" + updateBalance, null);
		 */
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/withdraw")
	public ResponseEntity<Transaction> withdraw(@RequestBody Transaction transaction) {
		ResponseEntity<Double> entity = temp.getForEntity(
				"http://localhost:9090/accounts/" + transaction.getAccountNumber() + "/balance", Double.class);
		Double currentBalance = entity.getBody();
		Double updateBalance = service.withdraw(transaction.getAccountNumber(), transaction.getAmount(), currentBalance,
				transaction.getTransactionDetails());
		temp.put(
				"http://localhost:9090/accounts/" + transaction.getAccountNumber() + "?currentBalance=" + updateBalance, null);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	
	@GetMapping("/getstatements")
	public CurrentDataSet getStatement() {
		List<Transaction> transactions = service.getStatement();
		CurrentDataSet dataset = new CurrentDataSet();
		dataset.setTransactions(transactions);
		return dataset;
	}
}
