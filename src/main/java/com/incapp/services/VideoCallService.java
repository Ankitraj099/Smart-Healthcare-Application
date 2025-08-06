package com.incapp.services;

import com.incapp.models.VideoCallRequest;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

@Service
public class VideoCallService {
    
    // In-memory storage for video call requests (in production, use database)
    private final Map<String, VideoCallRequest> videoCallRequests = new ConcurrentHashMap<>();
    
    // In-memory storage for online doctors (in production, use database)
    private final Set<String> onlineDoctors = ConcurrentHashMap.newKeySet();
    
    // Generate unique request ID
    private String generateRequestId() {
        return "req_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }
    
    // Check if doctor is online
    public boolean isDoctorOnline(String doctorEmail) {
        // Convert to lowercase for case-insensitive comparison
        String normalizedEmail = doctorEmail.toLowerCase();
        return onlineDoctors.contains(normalizedEmail);
    }
    
    // Set doctor online status
    public void setDoctorOnline(String doctorEmail, boolean online) {
        // Convert to lowercase for consistent storage
        String normalizedEmail = doctorEmail.toLowerCase();
        if (online) {
            onlineDoctors.add(normalizedEmail);
        } else {
            onlineDoctors.remove(normalizedEmail);
        }
    }
    
    // Create a video call request
    public VideoCallRequest createVideoCallRequest(String userEmail, String userName, 
                                                 String doctorEmail, String doctorName, String roomId) {
        // Validate inputs
        if (doctorEmail == null || doctorEmail.trim().isEmpty()) {
            throw new RuntimeException("Doctor email is required");
        }
        
        if (doctorName == null || doctorName.trim().isEmpty()) {
            throw new RuntimeException("Doctor name is required");
        }
        
        // Check if doctor is online (case-insensitive)
        if (!isDoctorOnline(doctorEmail)) {
            throw new RuntimeException("Doctor is not online");
        }
        
        // Check if there's already a pending request
        for (VideoCallRequest request : videoCallRequests.values()) {
            if (request.getDoctorEmail().equalsIgnoreCase(doctorEmail) && 
                request.getStatus().equals("pending")) {
                throw new RuntimeException("Doctor already has a pending video call request");
            }
        }
        
        VideoCallRequest request = new VideoCallRequest(userEmail, userName, doctorEmail, doctorName, roomId);
        request.setId(generateRequestId());
        videoCallRequests.put(request.getId(), request);
        
        return request;
    }
    
    // Get pending requests for a doctor
    public List<VideoCallRequest> getPendingRequestsForDoctor(String doctorEmail) {
        List<VideoCallRequest> pendingRequests = new ArrayList<>();
        String normalizedDoctorEmail = doctorEmail.toLowerCase();
        for (VideoCallRequest request : videoCallRequests.values()) {
            if (request.getDoctorEmail().equalsIgnoreCase(normalizedDoctorEmail) && 
                request.getStatus().equals("pending")) {
                pendingRequests.add(request);
            }
        }
        return pendingRequests;
    }
    
    // Get a specific request by ID
    public VideoCallRequest getRequestById(String requestId) {
        return videoCallRequests.get(requestId);
    }
    
    // Accept a video call request
    public VideoCallRequest acceptVideoCallRequest(String requestId) {
        VideoCallRequest request = videoCallRequests.get(requestId);
        if (request == null) {
            throw new RuntimeException("Video call request not found");
        }
        if (!request.getStatus().equals("pending")) {
            throw new RuntimeException("Video call request is not pending");
        }
        request.setStatus("accepted");
        return request;
    }
    
    // Reject a video call request
    public VideoCallRequest rejectVideoCallRequest(String requestId) {
        VideoCallRequest request = videoCallRequests.get(requestId);
        if (request == null) {
            throw new RuntimeException("Video call request not found");
        }
        if (!request.getStatus().equals("pending")) {
            throw new RuntimeException("Video call request is not pending");
        }
        request.setStatus("rejected");
        return request;
    }
    
    // Complete a video call request
    public VideoCallRequest completeVideoCallRequest(String requestId) {
        VideoCallRequest request = videoCallRequests.get(requestId);
        if (request == null) {
            throw new RuntimeException("Video call request not found");
        }
        request.setStatus("completed");
        return request;
    }
    
    // Get request status for a user
    public VideoCallRequest getRequestStatusForUser(String userEmail) {
        for (VideoCallRequest request : videoCallRequests.values()) {
            if (request.getUserEmail().equalsIgnoreCase(userEmail) && 
                (request.getStatus().equals("pending") || request.getStatus().equals("accepted"))) {
                return request;
            }
        }
        return null;
    }
    
    // Get all online doctors
    public Set<String> getAllOnlineDoctors() {
        return new HashSet<>(onlineDoctors);
    }
    
    // Cleanup old requests (call this periodically)
    public void cleanupOldRequests() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        videoCallRequests.entrySet().removeIf(entry -> 
            entry.getValue().getRequestTime().isBefore(cutoff));
    }
}