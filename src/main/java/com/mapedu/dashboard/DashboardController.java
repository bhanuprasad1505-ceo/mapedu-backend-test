package com.mapedu.dashboard;

import com.mapedu.attendance.Attendance;
import com.mapedu.attendance.AttendanceRepository;
import com.mapedu.device.Device;
import com.mapedu.device.DeviceRepository;
import com.mapedu.employee.EmployeeRepository;
import com.mapedu.student.StudentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;
    private final DeviceRepository deviceRepository;
    private final AttendanceRepository attendanceRepository;

    public DashboardController(StudentRepository studentRepository, EmployeeRepository employeeRepository,
                               DeviceRepository deviceRepository, AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.employeeRepository = employeeRepository;
        this.deviceRepository = deviceRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping
    public Map<String, Object> dashboard(@RequestParam String schoolCode) {
        List<Device> devices = deviceRepository.findBySchoolCodeOrderByDeviceIdAsc(schoolCode);
        Instant dayStart = Instant.now().truncatedTo(ChronoUnit.DAYS);
        long attendanceToday = attendanceRepository.countBySchoolCodeAndAttendanceTimeAfter(schoolCode, dayStart);
        List<Attendance> recent = attendanceRepository.findTop100BySchoolCodeOrderByAttendanceTimeDesc(schoolCode);

        long onlineDevices = devices.stream()
            .filter(d -> d.getLastSeen() != null && d.getLastSeen().isAfter(Instant.now().minus(2, ChronoUnit.MINUTES)))
            .count();

        return Map.of(
            "schoolCode", schoolCode,
            "students", studentRepository.findBySchoolCodeOrderByNameAsc(schoolCode).size(),
            "employees", employeeRepository.findBySchoolCodeOrderByNameAsc(schoolCode).size(),
            "devices", devices.size(),
            "onlineDevices", onlineDevices,
            "attendanceToday", attendanceToday,
            "recentAttendance", recent
        );
    }
}
