package com.oc.ssdlc;


import com.oc.ssdlc.model.Command;
import com.oc.ssdlc.service.SmarthomeMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.StreamSupport;


@SpringBootApplication
public class CentralApplication {

    private static final Logger logger = LoggerFactory.getLogger(CentralApplication.class);

    @Value("${smarthome.admin.username}")
    private String userName;

    @Value("${smarthome.admin.password}")
    private String password;

    // Inject the entire Spring Environment
    private final ConfigurableEnvironment environment;

    public CentralApplication(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(CentralApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(SmarthomeMessageSender sender) {
        return args -> {
            logger.info("Application starting up...");
            logger.debug("portal user: {}", userName);
            logger.debug("portal Password: {}", password);

            Set<String> loggedPropertyNames = new HashSet<>(); // To avoid logging duplicate properties if they exist in multiple sources we target

            StreamSupport.stream(environment.getPropertySources().spliterator(), false)
                    .filter(propertySource -> propertySource instanceof EnumerablePropertySource)
                    .map(propertySource -> (EnumerablePropertySource<?>) propertySource)
                    // Filter specifically for property sources that likely originate from application.properties
                    // These typically have names like "applicationConfig: [classpath:/application.properties]"
                    // or "Config resource 'class path resource [application.properties]'" etc.
                    .filter(propertySource -> propertySource.getName().contains("applicationConfig") ||
                            propertySource.getName().contains("application.properties"))
                    .forEach(propertySource -> {
                        logger.debug("Found relevant property source: {}", propertySource.getName());
                        Arrays.stream(propertySource.getPropertyNames())
                                // Only log properties we haven't logged from a higher-priority source yet
                                .filter(loggedPropertyNames::add) // Adds to set and returns true if new
                                .sorted(Comparator.naturalOrder()) // Optional: sort for readability
                                .forEach(name -> {
                                    try {
                                        String value = environment.getProperty(name);
                                        logger.debug("{}: {}", name, value);
                                    } catch (Exception e) {
                                        logger.debug("{}: <Error retrieving value - {}>", name, e.getMessage());
                                    }
                                });
                    });
            logger.debug("------------------------------------------------------------------");
            logger.debug("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");


            logger.debug("\n--- Smart Home Central Application ---\n\n\n");
            logger.debug("Enter commands to publish (e.g., 'thermostat.101.settemp:22.5' or 'exit'):");
            logger.debug("Enter commands to publish (e.g., 'doorlock.10.open' or 'exit'):");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("exit")) {
                    break;
                }
                try {
                    String[] parts = line.split(":", 2);
                    String routingKey = parts[0].trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";

                    Command command = new Command();
                    command.setSender("SpringCentral");
                    command.setCommand(routingKey);
                    command.setValue(value);

                    sender.sendCommand(routingKey, command);
                } catch (Exception e) {
                    System.err.println("Error parsing command or sending: " + e.getMessage());
                    System.out.println("Please use format: <routing.key>:<value>");
                    logger.error("Error processing console command: {}", e.getMessage());
                }
            }
            System.out.println("CentralClient shutting down.");
            logger.info("Application shutting down.");
            System.exit(0); // Applikation beenden
        };
    }
}