package com.mapedu.attendance;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceId;
    private String schoolCode;
    private String personType;
    private String personId;
    private String cardUid;
    private String attendanceType;
    private Instant attendanceTime;

    protected Attendance() {}

    public Attendance(String deviceId, String schoolCode, String personType, String personId, String cardUid, String attendanceType) {
        this.deviceId = deviceId;
        this.schoolCode = schoolCode;
        this.personType = personType;
        this.personId = personId;
        this.cardUid = cardUid;
        this.attendanceType = attendanceType;
        this.attendanceTime = Instant.now();
    }

    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getSchoolCode() { return schoolCode; }
    public String getPersonType() { return personType; }
    public String getPersonId() { return personId; }
    public String getCardUid() { return cardUid; }
    public String getAttendanceType() { return attendanceType; }
    public Instant getAttendanceTime() { return attendanceTime; }
}
