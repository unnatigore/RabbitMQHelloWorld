package com.capgemini.TransactionService.Resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.capgemini.TransactionService.Service.TransactionService;
import com.capgemini.TransactionService.entity.CurrentDataSet;
import com.capgemini.TransactionService.entity.Transaction;
import com.capgemini.TransactionService.entity.TransactionType;
import com.capgemini.TransactionService.sender.sender;

@RestController
@RequestMapping("/transactions")
public class TransactionResource {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	TransactionService service;
	
	@Autowired
	private sender sender;

	@PostMapping
	public ResponseEntity<Transaction> deposit(@RequestBody Transaction transaction) {
		ResponseEntity<Double> balance = restTemplate.getForEntity(
				"http://AccountService/accounts/" + transaction.getAccountNumber() + "/balance", Double.class);
		double currentBalance = balance.getBody();
		double updatedBalance = service.deposit(transaction.getAccountNumber(), transaction.getAmount(), currentBalance,
				TransactionType.DEPOSIT);
		restTemplate.put("http://AccountService/accounts/" + transaction.getAccountNumber() + "?currentBalance="
				+ updatedBalance, null);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/withdraw")
	public ResponseEntity<Transaction> withDraw(@RequestBody Transaction transaction) {
		ResponseEntity<Double> balance = restTemplate.getForEntity(
				"http://AccountService/accounts/" + transaction.getAccountNumber() + "/balance", Double.class);
		double currentBalance = balance.getBody();
		double updatedBalance = service.withdraw(transaction.getAccountNumber(), transaction.getAmount(),
				currentBalance, TransactionType.WITHDRAW);
		restTemplate.put("http://AccountService/accounts/" + transaction.getAccountNumber() + "?currentBalance="
				+ updatedBalance, null);
	//	transaction.setCurrentBalance(updatedBalance);
	//	  sender.send(transaction);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<CurrentDataSet> listOfTransactions() {
		CurrentDataSet currentDataSet = new CurrentDataSet();
		List<Transaction> transactions = service.listOfTransactions();
		currentDataSet.setTransactions(transactions);
		return new ResponseEntity<>(currentDataSet, HttpStatus.OK);
	}
}