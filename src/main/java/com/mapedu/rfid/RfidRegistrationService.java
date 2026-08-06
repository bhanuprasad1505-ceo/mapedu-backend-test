package com.mapedu.rfid;

import com.mapedu.device.Device;
import com.mapedu.device.DeviceRepository;
import com.mapedu.student.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RfidRegistrationService {
    private final RfidRegistrationRepository registrationRepository;
    private final RfidCardRepository cardRepository;
    private final DeviceRepository deviceRepository;
    private final StudentRepository studentRepository;

    public RfidRegistrationService(RfidRegistrationRepository registrationRepository,
                                   RfidCardRepository cardRepository,
                                   DeviceRepository deviceRepository,
                                   StudentRepository studentRepository) {
        this.registrationRepository = registrationRepository;
        this.cardRepository = cardRepository;
        this.deviceRepository = deviceRepository;
        this.studentRepository = studentRepository;
    }

    public RfidRegistration start(String deviceId, String studentId, String schoolCode) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not registered: " + deviceId));
        if (!device.isActive()) throw new IllegalArgumentException("Device is inactive");
        if (device.getSchoolCode() == null || !device.getSchoolCode().equals(schoolCode)) {
            throw new IllegalArgumentException("Device school mismatch");
        }
        if (studentRepository.findById(studentId).isEmpty()) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        if (cardRepository.existsByPersonTypeAndPersonIdAndActiveTrue("STUDENT", studentId)) {
            throw new IllegalArgumentException("Student already has an active RFID card");
        }
        return registrationRepository.save(new RfidRegistration(deviceId, studentId, schoolCode));
    }

    public Map<String, Object> pending(String deviceId) {
        RfidRegistration session = registrationRepository.findById(deviceId).orElse(null);
        Map<String, Object> response = new LinkedHashMap<>();
        if (session == null) {
            response.put("waiting", false);
            response.put("status", "NONE");
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

    public Map<String, Object> scan(String deviceId, String cardUid) {
        RfidRegistration session = registrationRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("No RFID registration waiting on device"));
        if (!session.isWaiting()) throw new IllegalArgumentException("RFID registration is not active");

        String uid = cardUid.trim().toUpperCase();
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

    public void cancel(String deviceId) {
        registrationRepository.findById(deviceId).ifPresent(session -> {
            session.cancel();
            registrationRepository.save(session);
        });
    }
}
