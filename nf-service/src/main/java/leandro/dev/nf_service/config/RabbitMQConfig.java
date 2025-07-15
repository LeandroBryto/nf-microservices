package leandro.dev.nf_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String VENDAS_NOVAS_QUEUE = "vendas.novas";
    public static final String NOTAS_EMAIL_QUEUE = "notas.email";

    @Bean
    public Queue vendasNovasQueue(){
        return QueueBuilder.durable(VENDAS_NOVAS_QUEUE).build();
    }

    @Bean
    public Queue notaEmailQueue(){
        return QueueBuilder.durable(NOTAS_EMAIL_QUEUE).build();
    }
    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
