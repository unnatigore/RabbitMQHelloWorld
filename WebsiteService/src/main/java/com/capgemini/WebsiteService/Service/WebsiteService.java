package com.capgemini.WebsiteService.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.capgemini.WebsiteService.Entity.CurrentDataSet;
import com.capgemini.WebsiteService.Entity.Transaction;
import com.capgemini.WebsiteService.controller.WebsiteController;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@Service
public class WebsiteService {

	@Autowired
	private RestTemplate restTemplate;
	
	private static CurrentDataSet dataSet; 

	@HystrixCommand(fallbackMethod = "depositFallBack")
	public String deposit(Transaction transaction, Model model) {
		restTemplate.postForEntity("http://ZuulService/TransactionService/transactions", transaction, null);
		model.addAttribute("message","Success!");
		return "DepositForm";
	}
	
	public String depositFallBack(Transaction transaction, Model model) {
		model.addAttribute("message", "Deposite Service is currently busy");
		return "DepositForm";
	}

	@HystrixCommand(fallbackMethod = "withdrawFallBack")
	public String withdraw(Transaction transaction, Model model) {
		restTemplate.postForEntity("http://ZuulService/TransactionService/transactions/withdraw", transaction, null);
		model.addAttribute("message","Success!");
		return "WithdrawForm";
	}

	public String withdrawFallBack(Transaction transaction, Model model) {
		model.addAttribute("message",  "Withdraw Service is currently busy" );
		return "WithdrawForm";
	}
	
	@HystrixCommand(fallbackMethod = "fundtransferFallBack")
	public String fundTransfer(int sendersAccountNumber, int receiversAccountNumber,
			double amount, Model model) {
		Transaction sendersTransaction = new Transaction(sendersAccountNumber, amount);
		restTemplate.postForEntity("http://ZuulService/TransactionService/transactions/withdraw", sendersTransaction, null);
		Transaction receiversTransaction = new Transaction(receiversAccountNumber, amount);
		restTemplate.postForEntity("http://ZuulService/TransactionService/transactions", receiversTransaction, null);
		model.addAttribute("message","Success!");
		return "FundTransferForm";
	}
	
	public String fundtransferFallBack(int sendersAccountNumber, int receiversAccountNumber,
			double amount, Model model) {
		model.addAttribute("message", "FundTransfer service is currently busy");
		return "FundTransferForm";
	}
	
	@HystrixCommand(fallbackMethod = "getStatementFallBack")
	public ModelAndView getStatement(int offset, int size) {
		int currentSize = size==0?5:size;
		int currentOffset = offset==0?1:offset;
		Link previous = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(WebsiteController.class).getStatement(currentOffset-currentSize, currentSize)).withRel("previous");
		Link next = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(WebsiteController.class).getStatement(currentOffset+currentSize, currentSize)).withRel("next");
		CurrentDataSet dataSet = restTemplate.getForObject("http://ZuulService/TransactionService/transactions", CurrentDataSet.class);
		List<Transaction> transactionList = dataSet.getTransactions();
		List<Transaction> transactions = new ArrayList<Transaction>();
		for(int value=currentOffset-1; value<currentOffset+currentSize-1; value++) {
			if((transactionList.size() <= value && value > 0) || currentOffset < 1)
				break;
			Transaction transaction = transactionList.get(value);
			transactions.add(transaction);		
		}
		dataSet.setPreviousLink(previous);
		dataSet.setNextLink(next);
		dataSet.setTransactions(transactions);
		return new ModelAndView("DepositForm", "currentDataSet", dataSet);
	}
	
	public ModelAndView getStatementFallBack(int offset, int size) {
		return new ModelAndView("DepositForm", "currentDataSet", dataSet);
		
	}
}