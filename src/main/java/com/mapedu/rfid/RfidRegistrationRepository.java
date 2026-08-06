package com.mapedu.rfid;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RfidRegistrationRepository extends JpaRepository<RfidRegistration, String> {
    Optional<RfidRegistration> findByDeviceId(String deviceId);
}
