package com.incapp.models;

import java.sql.Date;

public class Feedback {
    private int id;
    private int appointmentId;
    private String doctorEmail;
    private String userEmail;
    private String userName;
    private String doctorName;
    private int rating; // 1-5 stars
    private String comment;
    private Date feedbackDate;
    private String status; // "submitted", "approved", "rejected"
    
    // Default constructor
    public Feedback() {
    }
    
    // Constructor with all fields
    public Feedback(int id, int appointmentId, String doctorEmail, String userEmail, String userName, String doctorName, int rating, String comment, Date feedbackDate, String status) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.doctorEmail = doctorEmail;
        this.userEmail = userEmail;
        this.userName = userName;
        this.doctorName = doctorName;
        this.rating = rating;
        this.comment = comment;
        this.feedbackDate = feedbackDate;
        this.status = status;
    }
    
    // Constructor without id (for creating new feedback)
    public Feedback(int appointmentId, String doctorEmail, String userEmail, String userName, String doctorName, int rating, String comment, Date feedbackDate, String status) {
        this.appointmentId = appointmentId;
        this.doctorEmail = doctorEmail;
        this.userEmail = userEmail;
        this.userName = userName;
        this.doctorName = doctorName;
        this.rating = rating;
        this.comment = comment;
        this.feedbackDate = feedbackDate;
        this.status = status;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    
    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public Date getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(Date feedbackDate) { this.feedbackDate = feedbackDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", appointmentId=" + appointmentId +
                ", doctorEmail='" + doctorEmail + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", feedbackDate=" + feedbackDate +
                ", status='" + status + '\'' +
                '}';
    }
} 