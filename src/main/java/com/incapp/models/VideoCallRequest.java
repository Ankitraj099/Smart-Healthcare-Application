package com.incapp.models;

import java.time.LocalDateTime;

public class VideoCallRequest {
    private String id;
    private String userEmail;
    private String userName;
    private String doctorEmail;
    private String doctorName;
    private String roomId;
    private String status; // "pending", "accepted", "rejected", "completed"
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    
    public VideoCallRequest() {
    }
    
    public VideoCallRequest(String userEmail, String userName, String doctorEmail, String doctorName, String roomId) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.doctorEmail = doctorEmail;
        this.doctorName = doctorName;
        this.roomId = roomId;
        this.status = "pending";
        this.requestTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }
    
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    
    public LocalDateTime getResponseTime() { return responseTime; }
    public void setResponseTime(LocalDateTime responseTime) { this.responseTime = responseTime; }
    
    @Override
    public String toString() {
        return "VideoCallRequest{" +
                "id='" + id + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", doctorEmail='" + doctorEmail + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", roomId='" + roomId + '\'' +
                ", status='" + status + '\'' +
                ", requestTime=" + requestTime +
                ", responseTime=" + responseTime +
                '}';
    }
}