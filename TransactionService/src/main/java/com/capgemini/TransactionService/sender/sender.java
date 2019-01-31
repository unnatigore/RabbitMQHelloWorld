package com.capgemini.TransactionService.sender;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.capgemini.TransactionService.entity.Transaction;

@Component
public class sender {
	@Autowired
	private RabbitMessagingTemplate rabbitTemplate;
	
	@Bean
	public Queue queue() {
		return new Queue("updateBalance", false);
	}
	
	
	public void send(Transaction transaction) {
		rabbitTemplate.convertAndSend("updateBalance", transaction);
	}
}
