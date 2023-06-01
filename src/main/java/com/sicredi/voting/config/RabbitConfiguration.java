package com.sicredi.voting.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Bean
    public Queue myQueue() {
        return new Queue("VotingResult", false);
    }

}

