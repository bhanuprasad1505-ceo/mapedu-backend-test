# MAPEDU Backend

Spring Boot backend APIs for MAPEDU ERP and RFID Attendance Device.

## Stack

- Java 21
- Spring Boot 3.4
- Spring Web
- Spring Data JPA
- MySQL
- Spring Security foundation
- Jakarta Validation

## Current APIs

### Health
- `GET /api/health`

### Students
- `GET /api/students?schoolCode=SCH001`
- `GET /api/students/{studentId}`
- `POST /api/students`

### Employees
- `GET /api/employees?schoolCode=SCH001`
- `GET /api/employees/{employeeId}`
- `POST /api/employees`

### RFID Cards
- `GET /api/rfid/cards?schoolCode=SCH001`
- `GET /api/rfid/cards/{cardUid}`
- `POST /api/rfid/cards`
- `DELETE /api/rfid/cards/{cardUid}`

### Devices
- `GET /api/devices?schoolCode=SCH001`
- `GET /api/devices/{deviceId}`
- `POST /api/devices`
- `POST /api/devices/{deviceId}/heartbeat`

### Attendance
- `POST /api/attendance`
- `GET /api/attendance?schoolCode=SCH001`
- `GET /api/attendance/device/{deviceId}`

### Dashboard
- `GET /api/dashboard?schoolCode=SCH001`

## RFID attendance payload

The ESP32 can send the existing payload without firmware changes:

```json
{
  "deviceId": "DEVICE001",
  "schoolCode": "SCH001",
  "studentId": "STU000001",
  "cardUID": "3585CBCA",
  "attendanceType": "IN"
}
```

The backend validates the device, school, RFID card and person, identifies the person as `STUDENT` or `EMPLOYEE`, records attendance, updates device `lastSeen`, and ignores the same attendance event repeated within 30 seconds.

## Configuration

Database and JWT settings are supplied through environment variables. Never commit real passwords, production database URLs, or JWT secrets.

Required database variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
