-- Sample Doctors Data for the Doctors Application
-- This script adds real doctors to the database for the user home page

-- Insert sample doctors
INSERT INTO doctor (email, password, name, state, city, area, speciality) VALUES
('dr.sarah.johnson@doctors.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Sarah Johnson', 'California', 'Los Angeles', 'Downtown', 'Dermatology'),
('dr.michael.chen@doctors.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Michael Chen', 'New York', 'New York City', 'Manhattan', 'Cardiology'),
('dr.emily.rodriguez@doctors.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Emily Rodriguez', 'Texas', 'Houston', 'Medical Center', 'Pediatrics'),
('dr.david.kim@doctors.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'David Kim', 'Florida', 'Miami', 'Brickell', 'Orthopedics'),
('dr.lisa.patel@doctors.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Lisa Patel', 'Illinois', 'Chicago', 'Loop', 'Neurology'),
('dr.james.wilson@doctors.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'James Wilson', 'Washington', 'Seattle', 'Downtown', 'Oncology');

-- Insert doctor details
INSERT INTO doctor_details (id, phone, dob, qualification, experience, gender) VALUES
(1, '+1-555-0101', '1985-03-15', 'MBBS, MD Dermatology', 7, 'Female'),
(2, '+1-555-0102', '1978-07-22', 'MBBS, MD Cardiology', 12, 'Male'),
(3, '+1-555-0103', '1982-11-08', 'MBBS, MD Pediatrics', 9, 'Female'),
(4, '+1-555-0104', '1975-05-30', 'MBBS, MS Orthopedics', 15, 'Male'),
(5, '+1-555-0105', '1980-09-12', 'MBBS, MD Neurology', 11, 'Female'),
(6, '+1-555-0106', '1973-12-25', 'MBBS, MD Oncology', 18, 'Male');

-- Insert doctor availability
INSERT INTO doctor_avail (mon_mor, mon_eve, tue_mor, tue_eve, wed_mor, wed_eve, thu_mor, thu_eve, fri_mor, fri_eve, sat_mor, sat_eve, sun_mor, sun_eve) VALUES
('9:00-12:00', '5:00-8:00', '9:00-12:00', '5:00-8:00', '9:00-12:00', '5:00-8:00', '9:00-12:00', '5:00-8:00', '9:00-12:00', '5:00-8:00', '9:00-12:00', '5:00-8:00', '9:00-12:00', '5:00-8:00'),
('8:00-11:00', '4:00-7:00', '8:00-11:00', '4:00-7:00', '8:00-11:00', '4:00-7:00', '8:00-11:00', '4:00-7:00', '8:00-11:00', '4:00-7:00', '8:00-11:00', '4:00-7:00', '8:00-11:00', '4:00-7:00'),
('10:00-1:00', '6:00-9:00', '10:00-1:00', '6:00-9:00', '10:00-1:00', '6:00-9:00', '10:00-1:00', '6:00-9:00', '10:00-1:00', '6:00-9:00', '10:00-1:00', '6:00-9:00', '10:00-1:00', '6:00-9:00'),
('7:00-10:00', '3:00-6:00', '7:00-10:00', '3:00-6:00', '7:00-10:00', '3:00-6:00', '7:00-10:00', '3:00-6:00', '7:00-10:00', '3:00-6:00', '7:00-10:00', '3:00-6:00', '7:00-10:00', '3:00-6:00'),
('9:30-12:30', '5:30-8:30', '9:30-12:30', '5:30-8:30', '9:30-12:30', '5:30-8:30', '9:30-12:30', '5:30-8:30', '9:30-12:30', '5:30-8:30', '9:30-12:30', '5:30-8:30', '9:30-12:30', '5:30-8:30'),
('8:30-11:30', '4:30-7:30', '8:30-11:30', '4:30-7:30', '8:30-11:30', '4:30-7:30', '8:30-11:30', '4:30-7:30', '8:30-11:30', '4:30-7:30', '8:30-11:30', '4:30-7:30', '8:30-11:30', '4:30-7:30');

-- Insert sample appointments to show consultation counts
INSERT INTO appointments (doctor_email, user_email, name, status, doc_booking_date, doc_booking_time, booking_date_time) VALUES
('dr.sarah.johnson@doctors.com', 'patient1@email.com', 'John Smith', 'Completed', '2024-01-15', '10:00', '2024-01-10 14:30:00'),
('dr.sarah.johnson@doctors.com', 'patient2@email.com', 'Mary Johnson', 'Completed', '2024-01-16', '11:00', '2024-01-11 09:15:00'),
('dr.sarah.johnson@doctors.com', 'patient3@email.com', 'Robert Brown', 'Completed', '2024-01-17', '14:00', '2024-01-12 16:45:00'),
('dr.sarah.johnson@doctors.com', 'patient4@email.com', 'Lisa Davis', 'Completed', '2024-01-18', '15:30', '2024-01-13 11:20:00'),
('dr.sarah.johnson@doctors.com', 'patient5@email.com', 'Michael Wilson', 'Completed', '2024-01-19', '16:00', '2024-01-14 13:10:00'),

('dr.michael.chen@doctors.com', 'patient6@email.com', 'Sarah Miller', 'Completed', '2024-01-15', '9:00', '2024-01-10 08:30:00'),
('dr.michael.chen@doctors.com', 'patient7@email.com', 'David Garcia', 'Completed', '2024-01-16', '10:30', '2024-01-11 10:45:00'),
('dr.michael.chen@doctors.com', 'patient8@email.com', 'Jennifer Lee', 'Completed', '2024-01-17', '11:30', '2024-01-12 14:20:00'),
('dr.michael.chen@doctors.com', 'patient9@email.com', 'Thomas Anderson', 'Completed', '2024-01-18', '13:00', '2024-01-13 15:30:00'),
('dr.michael.chen@doctors.com', 'patient10@email.com', 'Emily Taylor', 'Completed', '2024-01-19', '14:30', '2024-01-14 12:15:00'),
('dr.michael.chen@doctors.com', 'patient11@email.com', 'Christopher Martinez', 'Completed', '2024-01-20', '15:00', '2024-01-15 09:45:00'),
('dr.michael.chen@doctors.com', 'patient12@email.com', 'Amanda White', 'Completed', '2024-01-21', '16:30', '2024-01-16 11:30:00'),

('dr.emily.rodriguez@doctors.com', 'patient13@email.com', 'Daniel Clark', 'Completed', '2024-01-15', '10:00', '2024-01-10 13:20:00'),
('dr.emily.rodriguez@doctors.com', 'patient14@email.com', 'Jessica Hall', 'Completed', '2024-01-16', '11:00', '2024-01-11 16:10:00'),
('dr.emily.rodriguez@doctors.com', 'patient15@email.com', 'Kevin Lewis', 'Completed', '2024-01-17', '12:30', '2024-01-12 10:25:00'),
('dr.emily.rodriguez@doctors.com', 'patient16@email.com', 'Nicole Young', 'Completed', '2024-01-18', '13:30', '2024-01-13 14:40:00'),
('dr.emily.rodriguez@doctors.com', 'patient17@email.com', 'Ryan King', 'Completed', '2024-01-19', '14:00', '2024-01-14 08:55:00'),
('dr.emily.rodriguez@doctors.com', 'patient18@email.com', 'Stephanie Scott', 'Completed', '2024-01-20', '15:30', '2024-01-15 12:30:00'),
('dr.emily.rodriguez@doctors.com', 'patient19@email.com', 'Brandon Green', 'Completed', '2024-01-21', '16:00', '2024-01-16 15:20:00'),

('dr.david.kim@doctors.com', 'patient20@email.com', 'Rachel Adams', 'Completed', '2024-01-15', '8:00', '2024-01-10 07:30:00'),
('dr.david.kim@doctors.com', 'patient21@email.com', 'Matthew Baker', 'Completed', '2024-01-16', '9:30', '2024-01-11 09:45:00'),
('dr.david.kim@doctors.com', 'patient22@email.com', 'Lauren Carter', 'Completed', '2024-01-17', '10:30', '2024-01-12 11:15:00'),
('dr.david.kim@doctors.com', 'patient23@email.com', 'Andrew Evans', 'Completed', '2024-01-18', '11:30', '2024-01-13 13:40:00'),
('dr.david.kim@doctors.com', 'patient24@email.com', 'Megan Foster', 'Completed', '2024-01-19', '12:00', '2024-01-14 10:25:00'),
('dr.david.kim@doctors.com', 'patient25@email.com', 'Joshua Gordon', 'Completed', '2024-01-20', '13:30', '2024-01-15 14:50:00'),
('dr.david.kim@doctors.com', 'patient26@email.com', 'Ashley Harris', 'Completed', '2024-01-21', '14:00', '2024-01-16 16:30:00'),
('dr.david.kim@doctors.com', 'patient27@email.com', 'Tyler Jackson', 'Completed', '2024-01-22', '15:30', '2024-01-17 12:45:00'),
('dr.david.kim@doctors.com', 'patient28@email.com', 'Brittany Johnson', 'Completed', '2024-01-23', '16:00', '2024-01-18 09:20:00'),
('dr.david.kim@doctors.com', 'patient29@email.com', 'Zachary Kelly', 'Completed', '2024-01-24', '17:30', '2024-01-19 15:10:00'),
('dr.david.kim@doctors.com', 'patient30@email.com', 'Samantha Lopez', 'Completed', '2024-01-25', '18:00', '2024-01-20 11:35:00'),
('dr.david.kim@doctors.com', 'patient31@email.com', 'Nathan Moore', 'Completed', '2024-01-26', '19:30', '2024-01-21 13:25:00'),
('dr.david.kim@doctors.com', 'patient32@email.com', 'Victoria Nelson', 'Completed', '2024-01-27', '20:00', '2024-01-22 10:40:00'),
('dr.david.kim@doctors.com', 'patient33@email.com', 'Justin Parker', 'Completed', '2024-01-28', '21:30', '2024-01-23 14:15:00');

-- Note: The password hash used above is for 'password123' - you may want to change this for production 