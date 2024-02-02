package com.service.notification.listener;

import com.service.notification.events.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;


@Slf4j
@Configuration
public class NotificationListener {
    @KafkaListener(topics = "notification", groupId = "notificationGroup")
    public void handleNotification(NotificationEvent event){
        log.info("Email has been sent to {}.\nEmail : {}", event.getEmail(), event.getNotification());
    }

}
