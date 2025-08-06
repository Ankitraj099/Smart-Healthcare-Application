# Doctors Application - Real Doctors Integration

This application has been updated to display real doctors from the database on the user home page instead of hardcoded placeholder data.

## Changes Made

### 1. Updated UserController
- Modified `userHome()` method in `UserController.java` to fetch real doctors from the REST API
- Added consultation count calculation for each doctor
- Integrated with existing REST endpoints

### 2. Updated UserHome Template
- Replaced hardcoded doctor cards with dynamic Thymeleaf templates
- Added proper error handling for when no doctors are available
- Displays real doctor information including name, speciality, experience, and consultation count

### 3. Database Setup
- Created `sample-doctors.sql` with sample doctor data
- Includes 6 sample doctors with realistic information
- Added sample appointments to demonstrate consultation counts

## Setup Instructions

### Prerequisites
- MySQL database running on localhost:3306
- Java 8 or higher
- Maven

### Database Setup

1. **Create Database**
   ```sql
   CREATE DATABASE doctors;
   USE doctors;
   ```

2. **Run the REST API Service**
   ```bash
   cd ../Doctors-RESTfulWebservices
   mvn spring-boot:run
   ```
   This will start the REST API on port 7071 and create the database tables automatically.

3. **Load Sample Data**
   ```bash
   mysql -u root -p doctors < src/main/resources/sample-doctors.sql
   ```
   Or run the SQL script directly in your MySQL client.

### Running the Application

1. **Start the Main Application**
   ```bash
   mvn spring-boot:run
   ```
   This will start the web application on port 7070.

2. **Access the Application**
   - Open your browser and go to `http://localhost:7070`
   - Navigate to the user home page to see the real doctors

## Sample Doctors Added

The following doctors are included in the sample data:

1. **Dr. Sarah Johnson** - Dermatology (7 years, 5 consults)
2. **Dr. Michael Chen** - Cardiology (12 years, 12 consults)
3. **Dr. Emily Rodriguez** - Pediatrics (9 years, 7 consults)
4. **Dr. David Kim** - Orthopedics (15 years, 14 consults)
5. **Dr. Lisa Patel** - Neurology (11 years, 0 consults)
6. **Dr. James Wilson** - Oncology (18 years, 0 consults)

## Features

- **Real Doctor Data**: All doctors displayed are from the actual database
- **Dynamic Consultation Counts**: Shows actual number of appointments for each doctor
- **Experience Display**: Shows real years of experience from doctor details
- **Error Handling**: Gracefully handles cases where no doctors are available
- **Responsive Design**: Maintains the existing beautiful UI design

## API Endpoints Used

- `GET /doctor/getDoctors` - Fetches all doctors
- `GET /appointment/getByDoctorEmail/{email}` - Fetches appointments for a specific doctor

## Troubleshooting

1. **No doctors displayed**: Make sure the REST API is running on port 7071
2. **Database connection issues**: Verify MySQL is running and credentials are correct
3. **Empty consultation counts**: Ensure appointments exist in the database for the doctors

## Security Note

The sample doctors use a default password hash for 'password123'. In production, you should:
- Change all doctor passwords
- Use proper password hashing
- Implement proper authentication mechanisms 