package com.mapedu.device;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceRepository repository;

    public DeviceController(DeviceRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Device> list(@RequestParam(required = false) String schoolCode) {
        return schoolCode == null || schoolCode.isBlank()
            ? repository.findAll()
            : repository.findBySchoolCodeOrderByDeviceIdAsc(schoolCode);
    }

    @GetMapping("/{deviceId}")
    public Device get(@PathVariable String deviceId) {
        return repository.findById(deviceId)
            .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Device register(@Valid @RequestBody DeviceRequest request) {
        if (repository.existsById(request.deviceId())) {
            throw new IllegalArgumentException("Device already exists: " + request.deviceId());
        }
        return repository.save(new Device(request.deviceId(), request.deviceName(), request.deviceType(), request.schoolCode(), request.location(), request.firmwareVersion()));
    }

    @PostMapping("/{deviceId}/heartbeat")
    public Device heartbeat(@PathVariable String deviceId, @RequestBody(required = false) HeartbeatRequest request) {
        Device device = get(deviceId);
        device.setLastSeen(Instant.now());
        if (request != null && request.firmwareVersion() != null && !request.firmwareVersion().isBlank()) {
            device.setFirmwareVersion(request.firmwareVersion());
        }
        return repository.save(device);
    }

    public record DeviceRequest(
        @NotBlank String deviceId,
        @NotBlank String deviceName,
        @NotBlank String deviceType,
        @NotBlank String schoolCode,
        String location,
        String firmwareVersion
    ) {}

    public record HeartbeatRequest(String firmwareVersion) {}
}
