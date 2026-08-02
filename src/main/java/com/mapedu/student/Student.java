package com.mapedu.student;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
    @Id
    private String studentId;
    private String schoolCode;
    private String name;
    private String className;
    private String section;
    private boolean active = true;

    protected Student() {}

    public Student(String studentId, String schoolCode, String name, String className, String section) {
        this.studentId = studentId;
        this.schoolCode = schoolCode;
        this.name = name;
        this.className = className;
        this.section = section;
        this.active = true;
    }

    public String getStudentId() { return studentId; }
    public String getSchoolCode() { return schoolCode; }
    public String getName() { return name; }
    public String getClassName() { return className; }
    public String getSection() { return section; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
