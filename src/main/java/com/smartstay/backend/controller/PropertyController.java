
package com.smartstay.backend.controller;

import com.smartstay.backend.entity.Property;
import com.smartstay.backend.entity.User;
import com.smartstay.backend.repository.PropertyRepository;
import com.smartstay.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/properties")
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyController(
            PropertyRepository propertyRepository,
            UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getProperties(Authentication authentication) {
        User owner = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        if (owner.getRole() != User.Role.OWNER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Owner access required"));
        }

        List<Property> properties =
                propertyRepository.findByOwnerId(owner.getId());

        List<Map<String, Object>> result = properties.stream()
                .map(property -> Map.<String, Object>of(
                        "id", property.getId(),
                        "name", property.getName(),
                        "address", property.getAddress(),
                        "city", property.getCity() == null
                                ? "" : property.getCity(),
                        "totalRooms", property.getTotalRooms(),
                        "monthlyRent", property.getMonthlyRent() == null
                                ? 0 : property.getMonthlyRent(),
                        "facilities", property.getFacilities() == null
                                ? "" : property.getFacilities()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> addProperty(
            @RequestBody Property request,
            Authentication authentication) {

        User owner = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        if (owner.getRole() != User.Role.OWNER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Owner access required"));
        }

        if (request.getName() == null || request.getName().isBlank()
                || request.getAddress() == null
                || request.getAddress().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Property name and address are required"
                    ));
        }

        if (request.getTotalRooms() == null
                || request.getTotalRooms() < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Total rooms must be zero or greater"
                    ));
        }

        Property property = new Property();
        property.setName(request.getName().trim());
        property.setAddress(request.getAddress().trim());
        property.setCity(request.getCity());
        property.setTotalRooms(request.getTotalRooms());
        property.setMonthlyRent(request.getMonthlyRent());
        property.setFacilities(request.getFacilities());

        // Always use the authenticated owner, never a client-supplied owner ID.
        property.setOwner(owner);

        Property saved = propertyRepository.save(property);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Property added successfully",
                        "id", saved.getId(),
                        "name", saved.getName(),
                        "address", saved.getAddress()
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProperty(
            @PathVariable Long id,
            @RequestBody Property request,
            Authentication authentication) {

        User owner = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        if (owner.getRole() != User.Role.OWNER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Owner access required"));
        }

        Property property = propertyRepository.findById(id).orElse(null);

        if (property == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Property not found"));
        }

        if (property.getOwner() == null
                || !property.getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You cannot modify this property"));
        }

        if (request.getName() == null || request.getName().isBlank()
                || request.getAddress() == null
                || request.getAddress().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Property name and address are required"
                    ));
        }

        if (request.getTotalRooms() == null || request.getTotalRooms() < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Total rooms must be zero or greater"
                    ));
        }

        property.setName(request.getName().trim());
        property.setAddress(request.getAddress().trim());
        property.setCity(request.getCity());
        property.setTotalRooms(request.getTotalRooms());
        property.setMonthlyRent(request.getMonthlyRent());
        property.setFacilities(request.getFacilities());

        Property saved = propertyRepository.save(property);

        return ResponseEntity.ok(Map.of(
                "message", "Property updated successfully",
                "id", saved.getId(),
                "name", saved.getName(),
                "address", saved.getAddress()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(
            @PathVariable Long id,
            Authentication authentication) {

        User owner = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        if (owner.getRole() != User.Role.OWNER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Owner access required"));
        }

        Property property = propertyRepository.findById(id).orElse(null);

        if (property == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Property not found"));
        }

        if (property.getOwner() == null
                || !property.getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You cannot delete this property"));
        }

        propertyRepository.delete(property);

        return ResponseEntity.ok(Map.of(
                "message", "Property deleted successfully",
                "id", id
        ));
    }

}