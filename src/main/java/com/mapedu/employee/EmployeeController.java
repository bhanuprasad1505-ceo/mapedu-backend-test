package com.mapedu.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Employee> list(@RequestParam(required = false) String schoolCode) {
        return schoolCode == null || schoolCode.isBlank()
            ? repository.findAll()
            : repository.findBySchoolCodeOrderByNameAsc(schoolCode);
    }

    @GetMapping("/{employeeId}")
    public Employee get(@PathVariable String employeeId) {
        return repository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@Valid @RequestBody EmployeeRequest request) {
        if (repository.existsById(request.employeeId())) {
            throw new IllegalArgumentException("Employee already exists: " + request.employeeId());
        }
        return repository.save(new Employee(request.employeeId(), request.schoolCode(), request.name(), request.department()));
    }

    public record EmployeeRequest(
        @NotBlank String employeeId,
        @NotBlank String schoolCode,
        @NotBlank String name,
        String department
    ) {}
}
