package com.mapedu.attendance;

import com.mapedu.device.Device;
import com.mapedu.device.DeviceRepository;
import com.mapedu.employee.EmployeeRepository;
import com.mapedu.rfid.RfidCard;
import com.mapedu.rfid.RfidCardRepository;
import com.mapedu.student.StudentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceRepository attendanceRepository;
    private final DeviceRepository deviceRepository;
    private final RfidCardRepository cardRepository;
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceController(AttendanceRepository attendanceRepository, DeviceRepository deviceRepository,
                                 RfidCardRepository cardRepository, StudentRepository studentRepository,
                                 EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.deviceRepository = deviceRepository;
        this.cardRepository = cardRepository;
        this.studentRepository = studentRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> mark(@Valid @RequestBody AttendanceRequest request) {
        String deviceId = request.deviceId().trim();
        String schoolCode = request.schoolCode().trim();
        String cardUid = request.cardUID().trim().toUpperCase();

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not registered: " + deviceId));
        if (!device.isActive()) {
            throw new IllegalArgumentException("Device is inactive");
        }
        if (!device.getSchoolCode().equals(schoolCode)) {
            throw new IllegalArgumentException("Device school mismatch");
        }

        // The RFID card is the source of truth for the person.
        RfidCard card = cardRepository.findByCardUidAndActiveTrue(cardUid)
                .orElseThrow(() -> new IllegalArgumentException("RFID card is not registered: " + cardUid));

        if (!card.getSchoolCode().equals(schoolCode)) {
            throw new IllegalArgumentException("Card school mismatch");
        }

        String personType = card.getPersonType();
        String personId = card.getPersonId();

        if ("STUDENT".equalsIgnoreCase(personType)) {
            if (studentRepository.findById(personId)
                    .filter(s -> s.isActive() && s.getSchoolCode().equals(schoolCode))
                    .isEmpty()) {
                throw new IllegalArgumentException("Student is not active or not found: " + personId);
            }
        } else if ("EMPLOYEE".equalsIgnoreCase(personType)) {
            if (employeeRepository.findById(personId)
                    .filter(e -> e.isActive() && e.getSchoolCode().equals(schoolCode))
                    .isEmpty()) {
                throw new IllegalArgumentException("Employee is not active or not found: " + personId);
            }
        } else {
            throw new IllegalArgumentException("Unsupported RFID person type: " + personType);
        }

        String attendanceType = request.attendanceType().trim().toUpperCase();
        if (!attendanceType.equals("IN") && !attendanceType.equals("OUT")) {
            throw new IllegalArgumentException("attendanceType must be IN or OUT");
        }

        Instant duplicateAfter = Instant.now().minus(30, ChronoUnit.SECONDS);
        if (attendanceRepository.existsByPersonIdAndCardUidAndAttendanceTypeAndAttendanceTimeAfter(
                personId, card.getCardUid(), attendanceType, duplicateAfter)) {
            return Map.of(
                    "success", true,
                    "duplicate", true,
                    "message", "Duplicate attendance ignored",
                    "personType", personType,
                    "personId", personId,
                    "cardUID", card.getCardUid(),
                    "attendanceType", attendanceType
            );
        }

        Attendance saved = attendanceRepository.save(new Attendance(
                deviceId, schoolCode, personType, personId, card.getCardUid(), attendanceType));

        device.setLastSeen(Instant.now());
        deviceRepository.save(device);

        return Map.of(
                "success", true,
                "duplicate", false,
                "attendanceId", saved.getId(),
                "personType", personType,
                "personId", saved.getPersonId(),
                "cardUID", saved.getCardUid(),
                "attendanceType", saved.getAttendanceType(),
                "attendanceTime", saved.getAttendanceTime()
        );
    }

    @GetMapping
    public List<Attendance> list(@RequestParam String schoolCode) {
        return attendanceRepository.findTop100BySchoolCodeOrderByAttendanceTimeDesc(schoolCode);
    }

    @GetMapping("/device/{deviceId}")
    public List<Attendance> byDevice(@PathVariable String deviceId) {
        return attendanceRepository.findTop100ByDeviceIdOrderByAttendanceTimeDesc(deviceId);
    }

    public record AttendanceRequest(
            @NotBlank String deviceId,
            @NotBlank String schoolCode,
            @NotBlank String cardUID,
            @NotBlank String attendanceType
    ) {}
}
