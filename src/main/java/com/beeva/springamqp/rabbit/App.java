package com.beeva.springamqp.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfiguration.class);

        RabbitTemplate template = context.getBean(RabbitTemplate.class);

        template.setExchange(context.getBean("exchange", String.class));

        template.convertAndSend(context.getBean("routingKey", String.class), "Message 1 queued with key");
        template.convertAndSend(context.getBean("routingKey", String.class), "Message 2 queued with key");
        template.convertAndSend(context.getBean("routingKey", String.class), "Message 3 queued with key");

        Thread.sleep(60000);

        System.exit(0);

    }
}
