# Hospital Management System

A RESTful Hospital Management System built with Spring Boot 3, Spring Security (JWT), Hibernate/JPA, and PostgreSQL. It simulates real hospital operations with role-based access control (RBAC) for Administrators, Doctors, and Patients.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.3 |
| Security | Spring Security + JWT (jjwt 0.12.5) |
| ORM | Hibernate / Spring Data JPA |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation |
| Mapping | MapStruct |
| Boilerplate | Lombok |
| Build Tool | Maven |
| Java Version | Java 21 |

---

## Project Structure

```
src/main/java/org/example/com/hospitalmanagementsystem/
├── config/           # Security and Audit configuration
├── controller/       # REST controllers
├── dto/
│   ├── request/      # Request DTOs with validation
│   └── response/     # Response DTOs
├── entity/           # JPA entities
├── enums/            # Enumerations (Role, Gender, AppointmentStatus)
├── exception/        # Custom exceptions and global handler
├── repository/       # Spring Data JPA repositories
├── security/         # JWT filter, service, and UserDetails
└── service/          # Business logic
```

---

## Setup and Configuration

### Prerequisites
- Java 21
- Maven 3.8+
- PostgreSQL 15+

### Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE hospital_db;
```

### application.properties

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hospital_db
spring.datasource.username=postgres
spring.datasource.password=your_password

app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
app.jwt.expiration=86400000
```

### Run the Application

```bash
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080`

---

## Authentication

All protected endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer <token>
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@hospital.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJ...",
  "tokenType": "Bearer",
  "email": "admin@hospital.com",
  "role": "ADMIN"
}
```

---

## API Endpoints

### Hospital Registration (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/hospitals/register` | Register a new hospital with admin |
| GET | `/api/hospitals/{id}` | Get hospital details (ADMIN) |
| PUT | `/api/hospitals/{id}` | Update hospital info (ADMIN) |

#### Register Hospital
```http
POST /api/hospitals/register
Content-Type: application/json

{
  "hospitalName": "City General Hospital",
  "telephone": "+250788000000",
  "physicalAddress": "Kigali, Rwanda",
  "admin": {
    "fullName": "Dr. Admin",
    "email": "admin@hospital.com",
    "password": "password123",
    "phoneNumber": "+250788000001"
  }
}
```

---

### Patient Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/patients/register` | Public | Register as patient |
| GET | `/api/patients/my/profile` | PATIENT | View own profile |
| PUT | `/api/patients/my/profile` | PATIENT | Full profile update |
| PATCH | `/api/patients/my/profile` | PATIENT | Partial profile update |
| DELETE | `/api/patients/my/account` | PATIENT | Delete account |
| GET | `/api/patients` | ADMIN | Get all patients |
| GET | `/api/patients/{id}` | ADMIN | Get patient by ID |

#### Register Patient
```http
POST /api/patients/register
Content-Type: application/json

{
  "nationalId": "1199012345678",
  "fullName": "Jane Doe",
  "dateOfBirth": "1995-06-15",
  "gender": "FEMALE",
  "email": "jane@example.com",
  "phoneNumber": "+250788123456",
  "address": "Kigali",
  "emergencyContactName": "John Doe",
  "emergencyContactPhone": "+250788654321",
  "password": "password123"
}
```

---

### Hospital Services

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/services` | ADMIN | Create service |
| PUT | `/api/services/{id}` | ADMIN | Update service |
| DELETE | `/api/services/{id}` | ADMIN | Delete service |
| GET | `/api/services` | ALL | List services |
| GET | `/api/services/{id}` | ALL | Get service by ID |

#### Create Service
```http
POST /api/services
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "serviceName": "Pediatrics",
  "description": "Children's health services"
}
```

---

### Doctor Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/doctors` | ADMIN | Create doctor |
| PUT | `/api/doctors/{id}` | ADMIN | Update doctor |
| PATCH | `/api/doctors/{id}/toggle-status` | ADMIN | Activate/deactivate |
| DELETE | `/api/doctors/{id}` | ADMIN | Delete doctor |
| GET | `/api/doctors` | ADMIN | List all doctors |
| GET | `/api/doctors/{id}` | ADMIN | Get doctor by ID |
| GET | `/api/doctors/my/profile` | DOCTOR | Doctor views own profile |

#### Create Doctor
```http
POST /api/doctors
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "medicalLicenseNumber": "ML-2024-001",
  "fullName": "Dr. John Smith",
  "email": "john.smith@hospital.com",
  "password": "password123",
  "phoneNumber": "+250788000002",
  "gender": "MALE",
  "specialisation": "Pediatrician",
  "serviceIds": [1, 2]
}
```

---

### Appointment Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/appointments` | PATIENT | Book appointment |
| GET | `/api/appointments/my` | PATIENT | View own appointments |
| PATCH | `/api/appointments/{id}/cancel` | PATIENT | Cancel appointment |
| GET | `/api/appointments/doctor/my` | DOCTOR | Doctor's appointments |
| PATCH | `/api/appointments/{id}/complete` | DOCTOR | Complete appointment |
| GET | `/api/appointments` | ADMIN | All appointments |
| GET | `/api/appointments/{id}` | ALL | Get appointment |
| PATCH | `/api/appointments/{id}/approve` | ADMIN | Approve appointment |
| PATCH | `/api/appointments/{id}/reject` | ADMIN | Reject appointment |
| PATCH | `/api/appointments/{id}/reassign` | ADMIN | Reassign doctor |

#### Book Appointment
```http
POST /api/appointments
Authorization: Bearer <patient_token>
Content-Type: application/json

{
  "appointmentDate": "2025-08-15",
  "appointmentTime": "09:00:00",
  "serviceId": 1,
  "symptoms": "Fever and headache",
  "doctorId": 1
}
```

---

### Diagnosis Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/appointments/{id}/diagnosis` | DOCTOR | Create diagnosis |
| PUT | `/api/appointments/{id}/diagnosis` | DOCTOR | Update diagnosis |
| POST | `/api/appointments/{id}/diagnosis/prescription` | DOCTOR | Upload prescription (PDF) |
| GET | `/api/appointments/{id}/diagnosis` | ALL | Get diagnosis |

#### Upload Prescription
```http
POST /api/appointments/1/diagnosis/prescription
Authorization: Bearer <doctor_token>
Content-Type: multipart/form-data

file: <pdf_file>
```

---

### Reports

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/reports` | ADMIN | Generate full report |

Report includes:
- Total patients, doctors, appointments
- Daily / weekly / monthly appointments
- Appointments per service
- Doctor workload
- Most requested service
- Cancelled/rejected appointment counts

---

## Business Rules

- Max 4 appointments per doctor per day
- Appointment date cannot be in the past
- No overlapping appointments for same doctor or patient
- PENDING -> APPROVED -> COMPLETED workflow
- Completed appointments are read-only
- Patients cannot delete accounts with PENDING or APPROVED appointments
- Doctors with future appointments cannot be deleted (deactivate instead)
- Prescriptions must be PDF format and under 5MB
- Only the assigned doctor can create a diagnosis

---

## Appointment Status Flow

```
PENDING -> APPROVED -> COMPLETED
       -> REJECTED
PENDING/APPROVED -> CANCELLED (by patient, before date)
```

---

## Roles and Permissions

| Action | ADMIN | DOCTOR | PATIENT |
|--------|-------|--------|---------|
| Register Hospital | auto | no | no |
| Manage Doctors | yes | no | no |
| Manage Services | yes | no | no |
| View All Patients | yes | no | no |
| View All Appointments | yes | no | no |
| Approve/Reject Appointments | yes | no | no |
| Book Appointments | no | no | yes |
| Diagnose Patients | no | yes | no |
| View Own Appointments | no | yes | yes |
| Generate Reports | yes | no | no |

---

## Audit Fields

All major entities track:
- createdAt - creation timestamp
- updatedAt - last modification timestamp
- createdBy - email of creator
- updatedBy - email of last modifier

---

## License

This project is developed for educational purposes as part of a Java Backend Development assignment.
