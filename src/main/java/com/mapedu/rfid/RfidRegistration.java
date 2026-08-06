package com.mapedu.rfid;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rfid_registration_sessions")
public class RfidRegistration {
    @Id
    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Column(name = "student_id", nullable = false, length = 100)
    private String studentId;

    @Column(name = "school_code", nullable = false, length = 100)
    private String schoolCode;

    @Column(name = "waiting", nullable = false)
    private boolean waiting;

    @Column(name = "card_uid", length = 100)
    private String cardUid;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected RfidRegistration() {}

    public RfidRegistration(String deviceId, String studentId, String schoolCode) {
        this.deviceId = deviceId;
        this.studentId = studentId;
        this.schoolCode = schoolCode;
        this.waiting = true;
        this.cardUid = null;
        this.status = "WAITING";
        this.updatedAt = Instant.now();
    }

    public String getDeviceId() { return deviceId; }
    public String getStudentId() { return studentId; }
    public String getSchoolCode() { return schoolCode; }
    public boolean isWaiting() { return waiting; }
    public String getCardUid() { return cardUid; }
    public String getStatus() { return status; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void start(String studentId, String schoolCode) {
        this.studentId = studentId;
        this.schoolCode = schoolCode;
        this.waiting = true;
        this.cardUid = null;
        this.status = "WAITING";
        this.updatedAt = Instant.now();
    }

    public void complete(String cardUid) {
        this.cardUid = cardUid;
        this.waiting = false;
        this.status = "COMPLETED";
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.waiting = false;
        this.status = "CANCELLED";
        this.updatedAt = Instant.now();
    }
}
