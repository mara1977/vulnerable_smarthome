// src/main/java/com/smarthome/central/service/ClientStatusService.java
package com.oc.ssdlc.service;

import com.oc.ssdlc.model.TemperatureUpdate; // Add this import if you want to store TemperatureUpdate objects
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientStatusService {

    // Map to store the latest status of each client (DeviceId -> Latest JSON Message)
    // You could define more specific DTOs for each device type if needed
    private final Map<String, JSONObject> clientStatuses = new ConcurrentHashMap<>();

    // Map to store latest temperature updates for thermostats specifically
    private final Map<String, TemperatureUpdate> thermostatTemperatures = new ConcurrentHashMap<>();

    // Map to store latest doorlock statuses specifically
    private final Map<String, String> doorlockStatuses = new ConcurrentHashMap<>(); // DeviceId -> "LOCKED" or "UNLOCKED"


    public void updateClientStatus(String deviceId, JSONObject message) {
        clientStatuses.put(deviceId, message);

        // Specific handling based on message type
        if (message.has("type")) {
            String type = message.getString("type");
            if ("temperature_update".equals(type)) {
                TemperatureUpdate update = new TemperatureUpdate();
                update.setDevice_id(deviceId);
                update.setTemperature(message.getDouble("temperature"));
                update.setTarget_temperature(message.getDouble("target_temperature"));
                update.setTimestamp(message.getLong("timestamp"));
                thermostatTemperatures.put(deviceId, update);
            } else if ("status_update".equals(type) && message.has("status")) {
                doorlockStatuses.put(deviceId, message.getString("status"));
            }
        }
    }

    public Map<String, JSONObject> getAllClientStatuses() {
        return Collections.unmodifiableMap(clientStatuses);
    }

    public Map<String, TemperatureUpdate> getThermostatTemperatures() {
        return Collections.unmodifiableMap(thermostatTemperatures);
    }

    public Map<String, String> getDoorlockStatuses() {
        return Collections.unmodifiableMap(doorlockStatuses);
    }
}