package com.mapedu.device;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "devices")
public class Device {
    @Id
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String schoolCode;
    private String location;
    private String firmwareVersion;
    private boolean active = true;
    private Instant lastSeen;

    protected Device() {}

    public Device(String deviceId, String deviceName, String deviceType, String schoolCode, String location, String firmwareVersion) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.schoolCode = schoolCode;
        this.location = location;
        this.firmwareVersion = firmwareVersion;
        this.active = true;
        this.lastSeen = Instant.now();
    }

    public String getDeviceId() { return deviceId; }
    public String getDeviceName() { return deviceName; }
    public String getDeviceType() { return deviceType; }
    public String getSchoolCode() { return schoolCode; }
    public String getLocation() { return location; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public boolean isActive() { return active; }
    public Instant getLastSeen() { return lastSeen; }
    public void setActive(boolean active) { this.active = active; }
    public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
}
