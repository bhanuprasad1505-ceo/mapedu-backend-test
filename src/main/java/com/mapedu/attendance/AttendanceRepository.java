package com.mapedu.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findTop100BySchoolCodeOrderByAttendanceTimeDesc(String schoolCode);
    List<Attendance> findTop100ByDeviceIdOrderByAttendanceTimeDesc(String deviceId);
    boolean existsByPersonIdAndCardUidAndAttendanceTypeAndAttendanceTimeAfter(String personId, String cardUid, String attendanceType, Instant after);
    long countBySchoolCodeAndAttendanceTimeAfter(String schoolCode, Instant after);
}
