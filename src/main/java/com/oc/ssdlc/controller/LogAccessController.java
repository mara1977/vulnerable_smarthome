package com.oc.ssdlc.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/debug")
@CrossOrigin(origins = "*")
public class LogAccessController {

    private static final String LOG_FILE_NAME = "smarthome-central.log"; // Matches logging.file.name in properties
    // Determine the log directory. Spring Boot logs relative to the app's working directory.
    // For this demo, assume logs/ is in the same directory as the JAR.
    private static final String LOG_DIRECTORY = "logs";

    @GetMapping("/logs")
    public ResponseEntity<Resource> getLogs() {
        try {
            Path logFilePath = Paths.get(LOG_DIRECTORY, LOG_FILE_NAME);
            File logFile = logFilePath.toFile();

            if (!logFile.exists() || !logFile.canRead()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }

            Resource resource = new FileSystemResource(logFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + logFile.getName() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(Files.size(logFilePath)));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // Optional: Ein einfacher HTML-Endpunkt, um auf die Log-Datei zu verlinken
    @GetMapping("/logviewer")
    public ResponseEntity<String> viewLogsHtml() {
        String htmlContent = "<html><body>" +
                "<h1>SmartHome Debug Log Viewer</h1>" +
                "<p>Click <a href=\"/debug/logs\" target=\"_blank\">here</a> to view the log file directly.</p>" +
                "</body></html>";
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }
}
