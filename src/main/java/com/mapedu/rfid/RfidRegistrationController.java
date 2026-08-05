package com.mapedu.rfid;

import com.mapedu.device.Device;
import com.mapedu.device.DeviceRepository;
import com.mapedu.student.StudentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rfid/registration")
public class RfidRegistrationController {
    private final RfidRegistrationRepository registrationRepository;
    private final RfidCardRepository cardRepository;
    private final DeviceRepository deviceRepository;
    private final StudentRepository studentRepository;

    public RfidRegistrationController(
            RfidRegistrationRepository registrationRepository,
            RfidCardRepository cardRepository,
            DeviceRepository deviceRepository,
            StudentRepository studentRepository) {
        this.registrationRepository = registrationRepository;
        this.cardRepository = cardRepository;
        this.deviceRepository = deviceRepository;
        this.studentRepository = studentRepository;
    }

    // React calls this when the user selects a student and presses "Scan Card".
    @PostMapping("/{deviceId}/start")
    @ResponseStatus(HttpStatus.CREATED)
    public RfidRegistration start(
            @PathVariable String deviceId,
            @Valid @RequestBody StartRequest request) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not registered: " + deviceId));
        if (!device.isActive()) throw new IllegalArgumentException("Device is inactive");
        if (!device.getSchoolCode().equals(request.schoolCode().trim()))
            throw new IllegalArgumentException("Device school mismatch");
        if (studentRepository.findById(request.studentId().trim()).isEmpty())
            throw new IllegalArgumentException("Student not found: " + request.studentId());

        RfidRegistration session = new RfidRegistration(
                deviceId,
                request.studentId().trim(),
                request.schoolCode().trim());
        return registrationRepository.save(session);
    }

    // ESP32 polls this endpoint to know whether React is waiting for a card.
    @GetMapping("/{deviceId}/pending")
    public Map<String, Object> pending(@PathVariable String deviceId) {
        return registrationRepository.findById(deviceId)
                .map(s -> Map.of(
                        "waiting", s.isWaiting(),
                        "studentId", s.getStudentId(),
                        "schoolCode", s.getSchoolCode(),
                        "status", s.getStatus()))
                .orElse(Map.of("waiting", false));
    }

    // ESP32 calls this after RC522 reads a UID.
    @PostMapping("/{deviceId}/scan")
    public Map<String, Object> scan(
            @PathVariable String deviceId,
            @Valid @RequestBody ScanRequest request) {
        RfidRegistration session = registrationRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("No RFID registration waiting on device"));
        if (!session.isWaiting()) throw new IllegalArgumentException("RFID registration is not active");

        String uid = request.cardUid().trim().toUpperCase();
        if (cardRepository.existsById(uid))
            throw new IllegalArgumentException("RFID card already registered: " + uid);
        if (cardRepository.existsByPersonTypeAndPersonIdAndActiveTrue("STUDENT", session.getStudentId()))
            throw new IllegalArgumentException("Student already has an active RFID card");

        RfidCard card = cardRepository.save(new RfidCard(
                uid, "STUDENT", session.getStudentId(), session.getSchoolCode()));
        session.complete(uid);
        registrationRepository.save(session);

        return Map.of(
                "success", true,
                "studentId", session.getStudentId(),
                "cardUid", card.getCardUid(),
                "status", "COMPLETED");
    }

    @DeleteMapping("/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String deviceId) {
        registrationRepository.findById(deviceId).ifPresent(s -> {
            s.cancel();
            registrationRepository.save(s);
        });
    }

    public record StartRequest(
            @NotBlank String studentId,
            @NotBlank String schoolCode) {}

    public record ScanRequest(@NotBlank String cardUid) {}
}
