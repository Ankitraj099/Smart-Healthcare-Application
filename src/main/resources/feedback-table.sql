-- Drop existing table if exists (for testing)
DROP TABLE IF EXISTS feedback;

-- Create feedback table
CREATE TABLE IF NOT EXISTS feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    doctor_email VARCHAR(255) NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    doctor_name VARCHAR(255) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    feedback_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'submitted' CHECK (status IN ('submitted', 'approved', 'rejected')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    INDEX idx_doctor_email (doctor_email),
    INDEX idx_user_email (user_email),
    INDEX idx_appointment_id (appointment_id),
    INDEX idx_status (status),
    INDEX idx_feedback_date (feedback_date)
);

-- Sample feedback data
INSERT INTO feedback (appointment_id, doctor_email, user_email, user_name, doctor_name, rating, comment, feedback_date, status) VALUES
(1, 'dr.johnson@medicare.com', 'jennifer.martinez@email.com', 'Jennifer Martinez', 'Dr. Sarah Johnson', 5, 'Excellent consultation! Dr. Johnson was very professional and thorough. She took the time to explain everything clearly and made me feel comfortable throughout the session.', '2024-01-15', 'approved'),
(2, 'dr.wilson@medicare.com', 'robert.wilson@email.com', 'Robert Wilson', 'Dr. Michael Wilson', 4, 'Great experience! The doctor was knowledgeable and the platform is very user-friendly. Saved me hours of travel time.', '2024-01-16', 'approved'),
(3, 'dr.thompson@medicare.com', 'lisa.thompson@email.com', 'Lisa Thompson', 'Dr. Emily Thompson', 5, 'Professional, caring, and efficient. The symptom checker helped me understand my condition better before the consultation.', '2024-01-17', 'approved'),
(4, 'dr.davis@medicare.com', 'michael.davis@email.com', 'Michael Davis', 'Dr. James Davis', 4, 'Very satisfied with the consultation. The doctor was patient and answered all my questions.', '2024-01-18', 'approved'),
(5, 'dr.garcia@medicare.com', 'sarah.garcia@email.com', 'Sarah Garcia', 'Dr. Maria Garcia', 5, 'Outstanding service! The doctor was very professional and the consultation was very helpful.', '2024-01-19', 'approved'),
(6, 'dr.johnson@medicare.com', 'test.user@email.com', 'Test User', 'Dr. Sarah Johnson', 5, 'This is a test feedback to verify the system is working correctly.', CURDATE(), 'approved'); 