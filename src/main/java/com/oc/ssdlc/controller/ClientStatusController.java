package com.oc.ssdlc.controller;


import com.oc.ssdlc.model.Command;
import com.oc.ssdlc.model.TemperatureUpdate;
import com.oc.ssdlc.service.ClientStatusService;
import com.oc.ssdlc.service.SmarthomeMessageSender;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

    @RestController
    @RequestMapping("/api/status")
    @CrossOrigin(origins = "*") // Allows your Angular app (or any origin) to access this API
    public class ClientStatusController {

        @Autowired
        private ClientStatusService clientStatusService;

        @Autowired
        SmarthomeMessageSender smarthomeMessageSender;

        @GetMapping("/all")
        public ResponseEntity<Map<String, JSONObject>> getAllClientStatuses() {
            return ResponseEntity.ok(clientStatusService.getAllClientStatuses());
        }

        @GetMapping("/thermostats")
        public ResponseEntity<Map<String, TemperatureUpdate>> getThermostatTemperatures() {
            return ResponseEntity.ok(clientStatusService.getThermostatTemperatures());
        }

        @GetMapping("/doorlocks")
        public ResponseEntity<Map<String, String>> getDoorlockStatuses() {
            return ResponseEntity.ok(clientStatusService.getDoorlockStatuses());
        }

        @PostMapping("/command")
        public ResponseEntity<String> sendCommand(@RequestBody Command command){
            if(command == null || command.getCommand() == null || command.getValue() == null){
                return new ResponseEntity<>("Invalid Command format. Requires 'command' and 'value'", HttpStatus.BAD_REQUEST);
            }

            String routingKey = command.getCommand();

            smarthomeMessageSender.sendCommand(routingKey, command);

            return new ResponseEntity<>("Command '" + routingKey + "' sent successfully with value '" + command.getValue() + "'", HttpStatus.OK);
        }
    }


