package com.oc.ssdlc.service;


import com.oc.ssdlc.model.Command;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmarthomeMessageSender {

    private static final String EXCHANGE_NAME = "Smarthome";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendCommand(String routingKey, Command command) {
        String jsonCommand = "";
        try {
            jsonCommand = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(command);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, jsonCommand);
            System.out.println(" [x] CentralClient sent command to " + routingKey + ": '" + jsonCommand + "'");
        } catch (Exception e) {
            System.err.println("Error sending command: " + e.getMessage());
        }
    }
}