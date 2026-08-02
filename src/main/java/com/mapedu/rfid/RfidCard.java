package com.mapedu.rfid;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rfid_cards")
public class RfidCard {
    @Id
    private String cardUid;
    private String personType;
    private String personId;
    private String schoolCode;
    private boolean active = true;
    private Instant registeredAt;

    protected RfidCard() {}

    public RfidCard(String cardUid, String personType, String personId, String schoolCode) {
        this.cardUid = cardUid;
        this.personType = personType;
        this.personId = personId;
        this.schoolCode = schoolCode;
        this.active = true;
        this.registeredAt = Instant.now();
    }

    public String getCardUid() { return cardUid; }
    public String getPersonType() { return personType; }
    public String getPersonId() { return personId; }
    public String getSchoolCode() { return schoolCode; }
    public boolean isActive() { return active; }
    public Instant getRegisteredAt() { return registeredAt; }
    public void setActive(boolean active) { this.active = active; }
}
