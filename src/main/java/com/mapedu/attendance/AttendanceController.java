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
        Device device = deviceRepository.findById(request.deviceId())
            .orElseThrow(() -> new IllegalArgumentException("Device not registered: " + request.deviceId()));
        if (!device.isActive()) throw new IllegalArgumentException("Device is inactive");
        if (!device.getSchoolCode().equals(request.schoolCode())) throw new IllegalArgumentException("Device school mismatch");

        RfidCard card = cardRepository.findByCardUidAndActiveTrue(request.cardUID().trim().toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("RFID card is not registered"));
        if (!card.getSchoolCode().equals(request.schoolCode())) throw new IllegalArgumentException("Card school mismatch");
        if (!card.getPersonId().equals(request.studentId())) throw new IllegalArgumentException("Card and person mismatch");

        String type = card.getPersonType();
        if (type.equals("STUDENT")) {
            if (studentRepository.findById(request.studentId()).filter(s -> s.isActive() && s.getSchoolCode().equals(request.schoolCode())).isEmpty())
                throw new IllegalArgumentException("Student is not active or not found");
        } else if (type.equals("EMPLOYEE")) {
            if (employeeRepository.findById(request.studentId()).filter(e -> e.isActive() && e.getSchoolCode().equals(request.schoolCode())).isEmpty())
                throw new IllegalArgumentException("Employee is not active or not found");
        }

        String attendanceType = request.attendanceType().trim().toUpperCase();
        if (!attendanceType.equals("IN") && !attendanceType.equals("OUT"))
            throw new IllegalArgumentException("attendanceType must be IN or OUT");

        Instant duplicateAfter = Instant.now().minus(30, ChronoUnit.SECONDS);
        if (attendanceRepository.existsByPersonIdAndCardUidAndAttendanceTypeAndAttendanceTimeAfter(request.studentId(), card.getCardUid(), attendanceType, duplicateAfter)) {
            return Map.of("success", true, "duplicate", true, "message", "Duplicate attendance ignored");
        }

        Attendance saved = attendanceRepository.save(new Attendance(
            request.deviceId(), request.schoolCode(), type, request.studentId(), card.getCardUid(), attendanceType));
        device.setLastSeen(Instant.now());
        deviceRepository.save(device);

        return Map.of("success", true, "duplicate", false, "attendanceId", saved.getId(), "personType", type,
            "personId", saved.getPersonId(), "cardUID", saved.getCardUid(), "attendanceType", saved.getAttendanceType(),
            "attendanceTime", saved.getAttendanceTime());
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
        @NotBlank String studentId,
        @NotBlank String cardUID,
        @NotBlank String attendanceType
    ) {}
}
