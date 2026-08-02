package com.mapedu.student;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Student> list(@RequestParam(required = false) String schoolCode) {
        return schoolCode == null || schoolCode.isBlank()
            ? repository.findAll()
            : repository.findBySchoolCodeOrderByNameAsc(schoolCode);
    }

    @GetMapping("/{studentId}")
    public Student get(@PathVariable String studentId) {
        return repository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@Valid @RequestBody StudentRequest request) {
        if (repository.existsById(request.studentId())) {
            throw new IllegalArgumentException("Student already exists: " + request.studentId());
        }
        return repository.save(new Student(request.studentId(), request.schoolCode(), request.name(), request.className(), request.section()));
    }

    public record StudentRequest(
        @NotBlank String studentId,
        @NotBlank String schoolCode,
        @NotBlank String name,
        String className,
        String section
    ) {}
}
