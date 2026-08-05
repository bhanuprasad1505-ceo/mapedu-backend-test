package com.mapedu.rfid;

import com.mapedu.device.Device;
import com.mapedu.device.DeviceRepository;
import com.mapedu.student.StudentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rfid/registration")
public class RfidRegistrationController {
    private final RfidRegistrationRepository registrationRepository;
    private final RfidCardRepository cardRepository;
    private final DeviceRepository deviceRepository;
    private final StudentRepository studentRepository;

    public RfidRegistrationController(RfidRegistrationRepository registrationRepository, RfidCardRepository cardRepository, DeviceRepository deviceRepository, StudentRepository studentRepository) {
        this.registrationRepository = registrationRepository;
        this.cardRepository = cardRepository;
        this.deviceRepository = deviceRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/{deviceId}/start")
    @ResponseStatus(HttpStatus.CREATED)
    public RfidRegistration start(@PathVariable String deviceId, @Valid @RequestBody StartRequest request) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not registered: " + deviceId));
        if (!device.isActive()) throw new IllegalArgumentException("Device is inactive");

        String schoolCode = request.schoolCode().trim();
        String studentId = request.studentId().trim();
        if (!device.getSchoolCode().equals(schoolCode)) throw new IllegalArgumentException("Device school mismatch");
        if (studentRepository.findById(studentId).isEmpty()) throw new IllegalArgumentException("Student not found: " + studentId);

        return registrationRepository.save(new RfidRegistration(deviceId, studentId, schoolCode));
    }

    @GetMapping("/{deviceId}/pending")
    public Map<String, Object> pending(@PathVariable String deviceId) {
        RfidRegistration session = registrationRepository.findById(deviceId).orElse(null);
        Map<String, Object> response = new LinkedHashMap<>();
        if (session == null) {
            response.put("waiting", false);
            return response;
        }
        response.put("waiting", session.isWaiting());
        response.put("studentId", session.getStudentId());
        response.put("schoolCode", session.getSchoolCode());
        response.put("status", session.getStatus());
        response.put("cardUid", session.getCardUid());
        response.put("updatedAt", session.getUpdatedAt());
        return response;
    }

    @PostMapping("/{deviceId}/scan")
    public Map<String, Object> scan(@PathVariable String deviceId, @Valid @RequestBody ScanRequest request) {
        RfidRegistration session = registrationRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("No RFID registration waiting on device"));
        if (!session.isWaiting()) throw new IllegalArgumentException("RFID registration is not active");

        String uid = request.cardUid().trim().toUpperCase();
        if (uid.isBlank()) throw new IllegalArgumentException("RFID card UID is required");
        if (cardRepository.existsById(uid)) throw new IllegalArgumentException("RFID card already registered: " + uid);
        if (cardRepository.existsByPersonTypeAndPersonIdAndActiveTrue("STUDENT", session.getStudentId())) {
            throw new IllegalArgumentException("Student already has an active RFID card");
        }

        RfidCard card = cardRepository.save(new RfidCard(uid, "STUDENT", session.getStudentId(), session.getSchoolCode()));
        session.complete(uid);
        registrationRepository.save(session);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("studentId", session.getStudentId());
        response.put("cardUid", card.getCardUid());
        response.put("status", "COMPLETED");
        return response;
    }

    @DeleteMapping("/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String deviceId) {
        registrationRepository.findById(deviceId).ifPresent(session -> {
            session.cancel();
            registrationRepository.save(session);
        });
    }

    public record StartRequest(@NotBlank String studentId, @NotBlank String schoolCode) {}
    public record ScanRequest(@NotBlank String cardUid) {}
}
