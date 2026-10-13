
package com.smartstay.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private static final Logger log =
            LoggerFactory.getLogger(HealthController.class);

    @GetMapping("/api/health")
    public Map<String, String> health() {
        log.info("SMARTSTAY_HEALTH_CONTROLLER_REACHED");

        return Map.of(
                "status", "UP",
                "application", "SmartStay Backend"
        );
    }
}
