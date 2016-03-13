package com.beeva.springamqp.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

    @RabbitListener(queues = "myqueue")
    public void processMessage(String content) {
        System.out.println("MESSAGE CONSUMED: " + content);
    }
}
