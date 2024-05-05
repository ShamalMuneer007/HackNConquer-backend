package org.hackncrypt.clanservice.config.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String WEB_FCM_QUEUE = "web_fmc_queue";
    public static final String WEB_FMC_EXCHANGE = "web_fmc_exchange";
    public static final String WEB_FMC_ROUTING_KEY = "web_fmc_routingKey";


    @Bean
    public Queue queue(){
        return new Queue(WEB_FCM_QUEUE);
    }
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(WEB_FMC_EXCHANGE);
    }
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(WEB_FMC_ROUTING_KEY);
    }
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}