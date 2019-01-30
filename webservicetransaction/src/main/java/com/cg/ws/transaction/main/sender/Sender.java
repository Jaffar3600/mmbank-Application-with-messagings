package com.cg.ws.transaction.main.sender;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.cg.ws.transaction.main.account.Account;
@Component
public class Sender {
	
	@Autowired
	RabbitMessagingTemplate rmTemplate;
	
	
	@Bean
	public Queue queue() {
		return new Queue("sender1",false);
	}
	
	public void send(Account account) {
		rmTemplate.convertAndSend("sender1",account);
	}
	
}
