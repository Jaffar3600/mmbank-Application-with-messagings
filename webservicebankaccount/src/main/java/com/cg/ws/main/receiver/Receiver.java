package com.cg.ws.main.receiver;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	
	@Bean
	public Queue queue() {
		
		return new Queue("sender1",false);
	}
	
}
