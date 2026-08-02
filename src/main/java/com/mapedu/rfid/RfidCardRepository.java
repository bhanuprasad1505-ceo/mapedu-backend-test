package com.mapedu.rfid;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RfidCardRepository extends JpaRepository<RfidCard, String> {
    List<RfidCard> findBySchoolCodeOrderByRegisteredAtDesc(String schoolCode);
    Optional<RfidCard> findByCardUidAndActiveTrue(String cardUid);
    boolean existsByPersonTypeAndPersonIdAndActiveTrue(String personType, String personId);
}
