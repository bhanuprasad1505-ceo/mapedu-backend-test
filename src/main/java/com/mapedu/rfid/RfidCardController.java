package com.mapedu.rfid;

import com.mapedu.employee.EmployeeRepository;
import com.mapedu.student.StudentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rfid/cards")
public class RfidCardController {
    private final RfidCardRepository cardRepository;
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;

    public RfidCardController(RfidCardRepository cardRepository, StudentRepository studentRepository, EmployeeRepository employeeRepository) {
        this.cardRepository = cardRepository;
        this.studentRepository = studentRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<RfidCard> list(@RequestParam(required = false) String schoolCode) {
        return schoolCode == null || schoolCode.isBlank()
            ? cardRepository.findAll()
            : cardRepository.findBySchoolCodeOrderByRegisteredAtDesc(schoolCode);
    }

    @GetMapping("/{cardUid}")
    public RfidCard get(@PathVariable String cardUid) {
        return cardRepository.findById(cardUid.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("RFID card not found: " + cardUid));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RfidCard register(@Valid @RequestBody CardRequest request) {
        String uid = request.cardUid().trim().toUpperCase();
        String type = request.personType().trim().toUpperCase();
        String personId = request.personId().trim();

        if (cardRepository.existsById(uid)) {
            throw new IllegalArgumentException("RFID card already registered: " + uid);
        }
        if (!type.equals("STUDENT") && !type.equals("EMPLOYEE")) {
            throw new IllegalArgumentException("personType must be STUDENT or EMPLOYEE");
        }
        if (cardRepository.existsByPersonTypeAndPersonIdAndActiveTrue(type, personId)) {
            throw new IllegalArgumentException("Person already has an active RFID card");
        }
        if (type.equals("STUDENT") && studentRepository.findById(personId).isEmpty()) {
            throw new IllegalArgumentException("Student not found: " + personId);
        }
        if (type.equals("EMPLOYEE") && employeeRepository.findById(personId).isEmpty()) {
            throw new IllegalArgumentException("Employee not found: " + personId);
        }

        return cardRepository.save(new RfidCard(uid, type, personId, request.schoolCode().trim()));
    }

    @DeleteMapping("/{cardUid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable String cardUid) {
        RfidCard card = get(cardUid);
        card.setActive(false);
        cardRepository.save(card);
    }

    public record CardRequest(
        @NotBlank String cardUid,
        @NotBlank String personType,
        @NotBlank String personId,
        @NotBlank String schoolCode
    ) {}
}
