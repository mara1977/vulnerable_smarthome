package com.oc.ssdlc.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oc.ssdlc.service.ClientStatusService;
import com.oc.ssdlc.model.TemperatureUpdate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmarthomeMessageListener {

    @Autowired
    ClientStatusService clientStatusService;

    // Use ObjectMapper for safer JSON parsing/conversion if needed
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(SmarthomeMessageListener.class);

    // Abonniert alle Nachrichten im "Smarthome"-Topic
    // Die Wildcard '#' fängt alle Routing Keys ab.
    // autoDelete=true: Die Queue wird gelöscht, wenn keine Consumer mehr verbunden sind.
    // exclusive=false: Andere Consumer können sich auch mit dieser Queue verbinden.
    // durable=false: Die Queue überlebt keinen RabbitMQ-Neustart (für Demo ok).
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "central-client-queue", autoDelete = "true", exclusive = "false", durable = "false"),
            exchange = @Exchange(value = "Smarthome", type = "topic", durable = "false"),
            key = "#")) // Wildcard für alle Routing Keys im Topic
    public void receiveMessage(String message) {

        logger.debug(" [x] CentralClient received: '" + message + "'");
        // Hier kann die empfangene JSON-Nachricht geparst und verarbeitet werden.
        // Z.B. if (message.contains("temperature_update")) ...
        try {
            JSONObject jsonMessage = new JSONObject(message);

            // Extract device_id (assuming it's always present in client messages)
            String deviceId = jsonMessage.optString("device_id", "unknown");

            // Store the latest full message for the device
            clientStatusService.updateClientStatus(deviceId, jsonMessage);

            // You can keep specific logging or processing here
            if (jsonMessage.has("type") && "temperature_update".equals(jsonMessage.getString("type"))) {
                // Parsing to DTO for strong typing (optional, service can do this)
                TemperatureUpdate update = objectMapper.readValue(message, TemperatureUpdate.class);

                logger.debug(" -> Parsed Temperature Update: Device \" + update.getDevice_id() + \", Temp: \" + update.getTemperature() + \"°C\");");
            } else if (jsonMessage.has("type") && "status_update".equals(jsonMessage.getString("type"))) {

                logger.debug("     -> Parsed Status Update: Device " + jsonMessage.getString("device_id") + ", Status: " + jsonMessage.getString("status"));
            } else {

                logger.debug("     -> Unrecognized or command message.");
            }
        } catch (Exception e) {
            System.err.println("Error parsing received message: " + e.getMessage());
        }
    }
}