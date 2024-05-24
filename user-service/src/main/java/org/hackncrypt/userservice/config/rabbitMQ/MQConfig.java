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
    public static final String INACTIVE_EMAIL_QUEUE = "inactive_email_queue";
    public static final String INACTIVE_EMAIL_EXCHANGE = "inactive_email_exchange";
    public static final String INACTIVE_EMAIL_ROUTINGKEY = "inactive_email_routingkey";
    public static final String CLAN_LEVEL_QUEUE = "clan_level_queue";
    public static final String CLAN_LEVEL_EXCHANGE = "clan_level_exchange";
    public static final String CLAN_LEVEL_ROUTING_KEY = "clan_level_routing_key";

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
    public Queue clanLevelQueue(){
        return new Queue(CLAN_LEVEL_QUEUE);
    }
    @Bean
    public TopicExchange clanLevelExchange(){
        return new TopicExchange(CLAN_LEVEL_EXCHANGE);
    }
    @Bean
    public Binding clanLevelBinding(Queue clanLevelQueue, TopicExchange clanLevelExchange){
        return BindingBuilder
                .bind(clanLevelQueue)
                .to(clanLevelExchange)
                .with(CLAN_LEVEL_ROUTING_KEY);
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
    public Queue emailQueue(){
        return new Queue(INACTIVE_EMAIL_QUEUE);
    }
    @Bean
    public TopicExchange emailExchange(){
        return new TopicExchange(INACTIVE_EMAIL_EXCHANGE);
    }
    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange emailExchange){
        return BindingBuilder
                .bind(emailQueue)
                .to(emailExchange)
                .with(INACTIVE_EMAIL_ROUTINGKEY);
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