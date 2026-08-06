package com.mapedu.rfid;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rfid/registration")
public class RfidRegistrationController {
    private final RfidRegistrationService service;

    public RfidRegistrationController(RfidRegistrationService service) {
        this.service = service;
    }

    @PostMapping("/{deviceId}/start")
    @ResponseStatus(HttpStatus.CREATED)
    public RfidRegistration start(@PathVariable String deviceId, @Valid @RequestBody StartRequest request) {
        return service.start(deviceId.trim(), request.studentId().trim(), request.schoolCode().trim());
    }

    @GetMapping("/{deviceId}/pending")
    public Map<String, Object> pending(@PathVariable String deviceId) {
        return service.pending(deviceId.trim());
    }

    @PostMapping("/{deviceId}/scan")
    public Map<String, Object> scan(@PathVariable String deviceId, @Valid @RequestBody ScanRequest request) {
        return service.scan(deviceId.trim(), request.cardUid());
    }

    @DeleteMapping("/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String deviceId) {
        service.cancel(deviceId.trim());
    }

    public record StartRequest(@NotBlank String studentId, @NotBlank String schoolCode) {}
    public record ScanRequest(@NotBlank String cardUid) {}
}
