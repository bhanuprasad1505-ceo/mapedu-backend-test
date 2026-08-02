package com.mapedu.student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findBySchoolCodeOrderByNameAsc(String schoolCode);
}
