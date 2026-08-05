package com.mapedu.rfid;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RfidRegistrationRepository extends JpaRepository<RfidRegistration, String> {
}
