package com.capgemini.AccountService.Receiver;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.capgemini.AccountService.Resource.AccountResource;
import com.capgemini.TransactionService.entity.Transaction;
@Component
public class Receiver {

	@Autowired
	private AccountResource accountResource;
	
	@Bean
	public Queue queue() {
		return new Queue("updateBalance", false);
	}
	
	@RabbitListener(queues="updateBalance")
	public void updateCurrentBalance(Transaction transaction) throws ListenerExecutionFailedException{
		//System.out.println(transaction.toString());
		accountResource.updateBalance(transaction.getAccountNumber(), transaction.getCurrentBalance());
	}
	
	
}
