package com.mapedu.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    private String employeeId;
    private String schoolCode;
    private String name;
    private String department;
    private boolean active = true;

    protected Employee() {}

    public Employee(String employeeId, String schoolCode, String name, String department) {
        this.employeeId = employeeId;
        this.schoolCode = schoolCode;
        this.name = name;
        this.department = department;
        this.active = true;
    }

    public String getEmployeeId() { return employeeId; }
    public String getSchoolCode() { return schoolCode; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
