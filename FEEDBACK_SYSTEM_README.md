# Feedback System Implementation

## Overview
This document describes the comprehensive feedback system implemented for the Medicare application, allowing users to provide ratings and reviews for completed appointments.

## Features Implemented

### 1. Feedback Model
- **Location**: `src/main/java/com/incapp/models/Feedback.java`
- **Fields**:
  - `id`: Unique identifier
  - `appointmentId`: Reference to the appointment
  - `doctorEmail`: Doctor's email address
  - `userEmail`: User's email address
  - `userName`: User's name
  - `doctorName`: Doctor's name
  - `rating`: 1-5 star rating
  - `comment`: User's feedback text
  - `feedbackDate`: Date when feedback was submitted
  - `status`: "submitted", "approved", or "rejected"

### 2. Database Schema
- **Table**: `feedback`
- **File**: `src/main/resources/feedback-table.sql`
- **Features**:
  - Foreign key constraint to appointments table
  - Rating validation (1-5 stars)
  - Status validation
  - Indexes for performance
  - Sample data included

### 3. RESTful Services (Doctors-RESTfulWebservices)

#### Repository
- **File**: `FeedbackRepository.java`
- **Features**:
  - Find feedback by doctor email
  - Find feedback by user email
  - Find feedback by appointment ID
  - Get average rating for doctors
  - Get feedback count for doctors
  - Get approved feedback only

#### Service
- **File**: `FeedbackService.java`
- **Features**:
  - Submit new feedback
  - Update feedback status
  - Delete feedback
  - Get various feedback statistics
  - Business logic for feedback management

#### Controller
- **File**: `FeedbackController.java`
- **Endpoints**:
  - `POST /feedback/submit` - Submit new feedback
  - `GET /feedback/all` - Get all feedback
  - `GET /feedback/{id}` - Get feedback by ID
  - `GET /feedback/doctor/{doctorEmail}` - Get feedback by doctor
  - `GET /feedback/user/{userEmail}` - Get feedback by user
  - `GET /feedback/appointment/{appointmentId}` - Get feedback by appointment
  - `GET /feedback/doctor/{doctorEmail}/approved` - Get approved feedback for doctor
  - `GET /feedback/doctor/{doctorEmail}/rating` - Get average rating for doctor
  - `GET /feedback/doctor/{doctorEmail}/count` - Get feedback count for doctor
  - `PUT /feedback/{id}/status` - Update feedback status
  - `DELETE /feedback/{id}` - Delete feedback
  - `GET /feedback/approved` - Get all approved feedback

### 4. User Interface Updates

#### UserAppointments.html
- **Feedback Button**: Added "Give Feedback" button for completed appointments
- **Feedback Modal**: Interactive modal with:
  - 5-star rating system
  - Comment textarea
  - Form validation
  - Responsive design
- **JavaScript**: Modal handling and form submission

#### UserHome.html
- **Real Testimonials**: Replaced hardcoded testimonials with dynamic data
- **Rating Display**: Shows star ratings for each testimonial
- **Dynamic Loading**: Fetches approved testimonials from API

#### FindDoctor.html
- **Dynamic Testimonials**: Loads testimonials via JavaScript
- **Real-time Updates**: Displays latest approved feedback
- **Error Handling**: Graceful fallback for API failures

### 5. Controller Updates

#### UserController.java
- **New Methods**:
  - `submitFeedback()`: Handles feedback submission
  - `getFeedbackForAppointment()`: Retrieves feedback for specific appointment
- **Updated Methods**:
  - `userHome()`: Now fetches real testimonials
  - `UserAppointments()`: Fixed deserialization issues

## Database Setup

### 1. Create Feedback Table
```sql
-- Run the feedback-table.sql script
source src/main/resources/feedback-table.sql;
```

### 2. Sample Data
The script includes sample feedback data for testing:
- 5 sample testimonials with ratings 4-5 stars
- Various doctor specialties represented
- Realistic patient names and comments

## Usage Flow

### 1. User Submits Feedback
1. User completes an appointment
2. Appointment status changes to "Completed"
3. "Give Feedback" button appears in UserAppointments
4. User clicks button and fills out feedback form
5. Feedback is submitted to REST API
6. Feedback status is set to "submitted"

### 2. Feedback Display
1. Approved feedback appears as testimonials
2. Home page shows latest approved testimonials
3. Find Doctor page loads testimonials dynamically
4. Star ratings are displayed for each testimonial

### 3. Admin Management (Future Enhancement)
- Admin can approve/reject submitted feedback
- Admin can manage feedback status
- Admin can view feedback statistics

## API Endpoints

### Submit Feedback
```
POST http://localhost:7071/feedback/submit
Content-Type: application/json

{
  "appointmentId": 1,
  "doctorEmail": "doctor@example.com",
  "userEmail": "user@example.com",
  "userName": "John Doe",
  "doctorName": "Dr. Smith",
  "rating": 5,
  "comment": "Excellent consultation!"
}
```

### Get Approved Testimonials
```
GET http://localhost:7071/feedback/approved
```

### Get Doctor Rating
```
GET http://localhost:7071/feedback/doctor/{doctorEmail}/rating
```

## Security Features

1. **User Authentication**: Only logged-in users can submit feedback
2. **Appointment Validation**: Feedback only allowed for completed appointments
3. **Input Validation**: Rating must be 1-5, comment required
4. **Status Management**: Feedback goes through approval process

## Future Enhancements

1. **Admin Panel**: Interface for managing feedback
2. **Email Notifications**: Notify doctors of new feedback
3. **Feedback Analytics**: Dashboard with feedback statistics
4. **Photo Uploads**: Allow users to upload photos with feedback
5. **Reply System**: Allow doctors to respond to feedback
6. **Moderation Tools**: Automatic content filtering

## Testing

### Manual Testing Steps
1. Complete an appointment (change status to "Completed")
2. Navigate to UserAppointments page
3. Click "Give Feedback" button
4. Fill out the feedback form
5. Submit and verify it appears in testimonials
6. Check that testimonials appear on home page

### API Testing
```bash
# Test feedback submission
curl -X POST http://localhost:7071/feedback/submit \
  -H "Content-Type: application/json" \
  -d '{"appointmentId":1,"doctorEmail":"test@example.com","userEmail":"user@example.com","userName":"Test User","doctorName":"Dr. Test","rating":5,"comment":"Great service!"}'

# Test getting approved feedback
curl http://localhost:7071/feedback/approved
```

## Troubleshooting

### Common Issues
1. **Feedback not appearing**: Check if status is "approved"
2. **Modal not opening**: Verify JavaScript is loaded
3. **API errors**: Check RESTful services are running on port 7071
4. **Database errors**: Verify feedback table exists and has correct schema

### Debug Steps
1. Check browser console for JavaScript errors
2. Verify REST API endpoints are accessible
3. Check database connection and table structure
4. Review application logs for errors

## Conclusion

The feedback system provides a comprehensive solution for collecting and displaying user feedback, enhancing the user experience and providing valuable insights for both users and doctors. The system is designed to be scalable, secure, and user-friendly. 