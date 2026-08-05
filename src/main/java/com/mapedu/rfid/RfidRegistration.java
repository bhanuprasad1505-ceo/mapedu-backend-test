package com.mapedu.rfid;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rfid_registration_sessions")
public class RfidRegistration {
    @Id
    private String deviceId;
    private String studentId;
    private String schoolCode;
    private boolean waiting;
    private String cardUid;
    private String status;
    private Instant updatedAt;

    protected RfidRegistration() {}

    public RfidRegistration(String deviceId, String studentId, String schoolCode) {
        this.deviceId = deviceId;
        this.studentId = studentId;
        this.schoolCode = schoolCode;
        this.waiting = true;
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
