package org.hackncrypt.userservice.config.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String QUEUE = "message_queue";
    public static final String EXCHANGE = "message_exchange";
    public static final String ROUTING_KEY = "message_routingKey";

    public static final String OTP_RPC_QUEUE = "otpRpc_queue";
    public static final String OTP_RPC_EXCHANGE = "otpRpc_exchange";
    public static final String OTP_RPC_ROUTINGKEY = "otpRpc_routingKey";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }
    @Bean
    public Queue otpRpc(){
        return new Queue(OTP_RPC_QUEUE);
    }
    @Bean
    public TopicExchange otpRpcExchange(){
        return new TopicExchange(OTP_RPC_EXCHANGE);
    }
    @Bean
    public Binding otpRpcBinding(Queue otpRpc, TopicExchange otpRpcExchange){
        return BindingBuilder
                .bind(otpRpc)
                .to(otpRpcExchange)
                .with(OTP_RPC_ROUTINGKEY);
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