package customer.library_management.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/mt/v1.0/subscriptions")
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @GetMapping("/dependencies")
    public ResponseEntity<Map<String, Object>> getDependencies(@RequestParam(name = "tenantId", required = false) String tenantId) {
        logger.info("=== DEPENDENCIES ENDPOINT CALLED ===");
        logger.info("Tenant ID: {}", tenantId);

        // Return empty dependencies - subscription can proceed
        Map<String, Object> response = Collections.singletonMap("dependencies", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tenants/{tenantId}")
    public ResponseEntity<String> subscribeTenant(@PathVariable(name = "tenantId") String tenantId) {
        logger.info("=== SUBSCRIBE TENANT CALLED ===");
        logger.info("Tenant ID: {}", tenantId);

        try {
            String tenantUrl = "https://" + tenantId + "-library-management-approuter.cfapps.us10-001.hana.ondemand.com";
            logger.info("Tenant subscribed, URL={}", tenantUrl);
            return ResponseEntity.ok(tenantUrl);
        } catch (Exception e) {
            logger.error("Error during tenant subscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Subscription failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/tenants/{tenantId}")
    public ResponseEntity<Void> unsubscribeTenant(@PathVariable(name = "tenantId") String tenantId) {
        logger.info("=== UNSUBSCRIBE TENANT CALLED ===");
        logger.info("Tenant ID: {}", tenantId);

        try {
            logger.info("Unsubscription successful for tenant: {}", tenantId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error during tenant unsubscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

