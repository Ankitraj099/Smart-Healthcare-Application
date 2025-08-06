package com.incapp.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.incapp.models.Appointments;
import com.incapp.models.Doctor;
import com.incapp.models.DoctorAvail;
import com.incapp.models.DoctorDetails;
import com.incapp.models.DoctorNotAvail;
import com.incapp.models.DoctorOnline;
import com.incapp.models.User;
import com.incapp.models.PasswordResetToken;
import com.incapp.models.Feedback;
import com.incapp.models.VideoCallRequest;
import com.incapp.services.PasswordResetService;
import com.incapp.services.VideoCallService;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.transaction.annotation.Transactional;





@Controller
@RequestMapping("/user")
public class UserController {
	
	private RestTemplate restTemplate=new RestTemplate();
	private String URL="http://localhost:7071/user";
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private PasswordResetService passwordResetService;
	
	@Autowired
	private VideoCallService videoCallService;
	
	@GetMapping("/oauth2success")
	public String googleLoginSuccess(@AuthenticationPrincipal OAuth2User principal, HttpSession session) {
	    String email = principal.getAttribute("email");
	    String name = principal.getAttribute("name");
	    
	    String API="/getUser/"+email;
		ResponseEntity<User> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, User.class);
		User user=result.getBody();
		if(user!=null) {
			session.setAttribute("user", user);
		}else {
			user = new User();
			user.setName(name);
		    user.setEmail(email);
		    user.setPassword(passwordEncoder.encode("jggJHGH@jgjhgjU%465"));
		    API="/register";
			HttpEntity<User> requestEntity=new HttpEntity<User>(user);
			restTemplate.exchange(URL+API,HttpMethod.POST,requestEntity,Boolean.class);
			session.setAttribute("user", user);
		}
	    return "redirect:/user/UserHome";
	}
	
	
	@PostMapping("/login")
	public String login(@RequestParam String email,@RequestParam String password, HttpSession session,  ModelMap m) {
		String API="/getUser/"+email;
		ResponseEntity<User> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, User.class);
		User user=result.getBody();
		if(user!=null && passwordEncoder.matches(password, user.getPassword())) {
			session.setAttribute("user", user);
			return "redirect:/user/UserHome";
		}else {
			m.addAttribute("msg","Invalid Credentials!");
			session.setAttribute("message", "Invalid Credentials!");
			return "login-signup";
		}
	}
	
	@GetMapping("/FindDoctor")
	public String findDoctor() {
		return "FindDoctor";
	}
	@GetMapping("/UserAppointments")
	public String UserAppointments(HttpSession session,ModelMap model) {
		String URL="http://localhost:7071/appointment";
		String API="/getByUserEmail/"+((User)session.getAttribute("user")).getEmail();
		ResponseEntity<List<Appointments>> result = restTemplate.exchange(
			URL+API, HttpMethod.GET, null, 
			new ParameterizedTypeReference<List<Appointments>>() {}
		);
		List<Appointments> appointments = result.getBody();
		model.addAttribute("apts", appointments);
		return "UserAppointments";
	}
	
	@GetMapping("/UserProfile")
	public String userProfile() {
		return "UserProfile";
	}
	@GetMapping("/UserHome")
	public String userHome(ModelMap model) {
		try {
			// Fetch all doctors from the REST API
			RestTemplate doctorRestTemplate = new RestTemplate();
			String doctorURL = "http://localhost:7071/doctor";
			String doctorAPI = "/getDoctors";
			String fullURL = doctorURL + doctorAPI;
			
			ResponseEntity<List<Doctor>> doctorResult = doctorRestTemplate.exchange(
				fullURL, 
				HttpMethod.GET, 
				null, 
				new ParameterizedTypeReference<List<Doctor>>() {}
			);
			List<Doctor> allDoctors = doctorResult.getBody();
			
			// Fetch appointment counts for each doctor
			RestTemplate appointmentRestTemplate = new RestTemplate();
			String appointmentURL = "http://localhost:7071/appointment";
			
			List<Map<String, Object>> featuredDoctors = new ArrayList<>();
			if (allDoctors != null && !allDoctors.isEmpty()) {
				// Take up to 4 doctors for the featured section
				int count = Math.min(allDoctors.size(), 4);
				for (int i = 0; i < count; i++) {
					Doctor doctor = allDoctors.get(i);
					
					// Get consultation count for this doctor
					String appointmentAPI = "/getByDoctorEmail/" + doctor.getEmail();
					ResponseEntity<List<Appointments>> appointmentResult = appointmentRestTemplate.exchange(
						appointmentURL + appointmentAPI, 
						HttpMethod.GET, 
						null, 
						new ParameterizedTypeReference<List<Appointments>>() {}
					);
					List<Appointments> appointments = appointmentResult.getBody();
					int consultationCount = appointments != null ? appointments.size() : 0;
					
					// Create a map to hold doctor info with consultation count
					Map<String, Object> doctorInfo = new HashMap<>();
					doctorInfo.put("doctor", doctor);
					doctorInfo.put("consultationCount", consultationCount);
					featuredDoctors.add(doctorInfo);
				}
			}
			
			model.addAttribute("featuredDoctors", featuredDoctors);
			
			// Fetch approved testimonials from feedback
			try {
				RestTemplate feedbackRestTemplate = new RestTemplate();
				String feedbackURL = "http://localhost:7071/feedback/approved";
				
				ResponseEntity<List<Feedback>> feedbackResult = feedbackRestTemplate.exchange(
					feedbackURL, 
					HttpMethod.GET, 
					null, 
					new ParameterizedTypeReference<List<Feedback>>() {}
				);
				List<Feedback> testimonials = feedbackResult.getBody();
				
				// Take up to 3 testimonials for display
				if (testimonials != null && !testimonials.isEmpty()) {
					int testimonialCount = Math.min(testimonials.size(), 3);
					List<Feedback> featuredTestimonials = testimonials.subList(0, testimonialCount);
					model.addAttribute("testimonials", featuredTestimonials);
				} else {
					model.addAttribute("testimonials", new ArrayList<Feedback>());
				}
			} catch (Exception e) {
				model.addAttribute("testimonials", new ArrayList<Feedback>());
			}
			
		} catch (Exception e) {
			// If there's an error, create some default doctors
			List<Map<String, Object>> defaultDoctors = new ArrayList<>();
			model.addAttribute("featuredDoctors", defaultDoctors);
			model.addAttribute("testimonials", new ArrayList<Feedback>());
		}
		
		return "UserHome";
	}

	
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam String email,@RequestParam String oldpassword,@RequestParam(value = "newpassword") String newPassword, HttpSession session, ModelMap m) {
		String API="/getUser/"+email;
		ResponseEntity<User> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, User.class);
		User user=result.getBody();
		if(user!=null && passwordEncoder.matches(oldpassword, user.getPassword())) {
			newPassword=passwordEncoder.encode(newPassword);
			
			Map<String, String> data=new HashMap<>();
			data.put("email", email);
			data.put("newPassword", newPassword);
			
			HttpEntity<Map<String, String>> requestEntity=new HttpEntity<Map<String, String>>(data);
			API="/updatePassword";
			ResponseEntity<Boolean> r= restTemplate.exchange(URL+API,HttpMethod.PUT, requestEntity, Boolean.class);
			if(r.getBody()) {
				m.addAttribute("msg","Password Updation Success!");
			}else {
				session.invalidate();
				return "redirect:/login-signup";
			}
		}else {
			m.addAttribute("msg","Invalid OLD Password!");
		}
		return "UserProfile";
	}
	@PostMapping("/forgetPassword")
	public String processForgotPassword(@RequestParam String email, ModelMap m) {
		String API = "/getUser/" + email;
		ResponseEntity<User> result = restTemplate.exchange(URL + API, HttpMethod.GET, null, User.class);
		User user = result.getBody();
		if (user == null) {
			m.addAttribute("msg", "Email not found!");
			return "login-signup";
		}
		String token = passwordResetService.createPasswordResetToken(email);
		passwordResetService.sendResetEmail(email, token, "user");
		m.addAttribute("msg", "Password reset link sent to your email.");
		return "login-signup";
	}
	
	@GetMapping("/reset-password")
	public String showResetPasswordForm(@RequestParam String token, ModelMap m) {
		PasswordResetToken resetToken = passwordResetService.getByToken(token);
		if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
			m.addAttribute("msg", "Invalid or expired token.");
			return "login-signup";
		}
		m.addAttribute("token", token);
		return "reset-password";
	}
	
	@PostMapping("/reset-password")
	public String processResetPassword(@RequestParam String token, @RequestParam String newPassword, ModelMap m) {
		String realToken = token.split(",")[0];
		PasswordResetToken resetToken = passwordResetService.getByToken(realToken);
		if (resetToken == null || resetToken.getExpiryDate().before(new java.util.Date())) {
			m.addAttribute("msg", "Invalid or expired token.");
			return "login-signup";
		}
		String email = resetToken.getEmail();
		String API = "/getUser/" + email;
		ResponseEntity<User> result = restTemplate.exchange(URL + API, HttpMethod.GET, null, User.class);
		User user = result.getBody();
		if (user == null) {
			m.addAttribute("msg", "User not found.");
			return "login-signup";
		}
		String encodedPassword = passwordEncoder.encode(newPassword);
		Map<String, String> data = new HashMap<>();
		data.put("email", email);
		data.put("newPassword", encodedPassword);
		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(data);
		API = "/updatePassword";
		restTemplate.exchange(URL + API, HttpMethod.PUT, requestEntity, Boolean.class);
		passwordResetService.deleteByToken(realToken);
		m.addAttribute("msg", "Password reset successful. Please login.");
		return "login-signup";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute User user,HttpSession session, ModelMap m) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		String API="/register";
		HttpEntity<User> requestEntity=new HttpEntity<User>(user);
		ResponseEntity<Boolean> result= restTemplate.exchange(URL+API,HttpMethod.POST,requestEntity,Boolean.class);
		if(result.getBody()) {
			session.setAttribute("user", user);
			return "redirect:/user/UserHome";
		}else {
			m.addAttribute("msg","Email ID Already Exist!");
			return "login-signup";
		}
	}
	@GetMapping("/getPhoto")
	public void getPhoto(HttpSession session,ServletResponse response) throws IOException {
		User user=(User)session.getAttribute("user");
		if(user == null) {
			// If user is not logged in, return default image
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/images/person.png");
			byte[] image = is.readAllBytes();
			response.getOutputStream().write(image);
			return;
		}
		String API="/getPhoto/"+user.getEmail();
		ResponseEntity<byte[]> result=restTemplate.exchange(URL+API, HttpMethod.GET, null, byte[].class);
		byte image[]=result.getBody();
		if(image==null || image.length==0 ) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/images/person.png");
			image=is.readAllBytes();
		}
		response.getOutputStream().write(image);
	}
	@PostMapping("/updatePhoto")
	public String updatePhoto(HttpSession session,@RequestPart("photo") MultipartFile photo,ModelMap m) throws IOException {
		User user=(User)session.getAttribute("user");
		String API="/updatePhoto/"+user.getEmail();
		HttpEntity<byte[]> requestEntity=new HttpEntity<>(photo.getBytes());
		restTemplate.put(URL+API, requestEntity);
		m.addAttribute("msg","Photo updated successfully!");
		API="/getUser/"+user.getEmail();
		ResponseEntity<User> result=restTemplate.exchange(URL+API,HttpMethod.GET,null,User.class);
		user=result.getBody();
		session.setAttribute("user", user);
		return "redirect:/user/UserHome";
	}
	
	@PostMapping("/updateUser")
	public String updateUser(HttpSession session,@ModelAttribute User user, ModelMap m) {
		String API="/updateUser";
		HttpEntity<User> requestEntity=new HttpEntity<User>(user);
		ResponseEntity<User> result=restTemplate.exchange(URL+API,HttpMethod.PUT,requestEntity,User.class);
		if(result.getBody()!=null) {
			session.setAttribute("user", result.getBody());
			m.addAttribute("msg","Updation Success!");
		}else {
			m.addAttribute("msg","Updation Failed!");
		}
		return "UserHome";
	}
	
	// New video call request endpoint
	@PostMapping("/requestVideoCall")
	@ResponseBody
	public Map<String, Object> requestVideoCall(@RequestParam String doctorEmail, 
	                                           @RequestParam String doctorName,
	                                           HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			// Validate inputs
			if (doctorEmail == null || doctorEmail.trim().isEmpty()) {
				response.put("success", false);
				response.put("message", "Doctor email is required");
				return response;
			}
			
			if (doctorName == null || doctorName.trim().isEmpty()) {
				response.put("success", false);
				response.put("message", "Doctor name is required");
				return response;
			}
			
			User user = (User) session.getAttribute("user");
			if (user == null) {
				response.put("success", false);
				response.put("message", "Please login to request video call");
				return response;
			}
			
			// Check if doctor is online
			boolean isOnline = videoCallService.isDoctorOnline(doctorEmail);
			
			if (!isOnline) {
				response.put("success", false);
				response.put("message", "Doctor is not online at the moment");
				return response;
			}
			
			// Generate room ID
			String roomId = "room_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
			
			// Create video call request
			VideoCallRequest request = videoCallService.createVideoCallRequest(
				user.getEmail(), user.getName(), doctorEmail, doctorName, roomId
			);
			
			response.put("success", true);
			response.put("requestId", request.getId());
			response.put("roomId", roomId);
			response.put("message", "Video call request sent to doctor");
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Error: " + e.getMessage());
		}
		return response;
	}
	
	// Check video call request status
	@GetMapping("/checkVideoCallStatus")
	@ResponseBody
	public Map<String, Object> checkVideoCallStatus(HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			User user = (User) session.getAttribute("user");
			if (user == null) {
				response.put("success", false);
				response.put("message", "User not logged in");
				return response;
			}
			
			VideoCallRequest request = videoCallService.getRequestStatusForUser(user.getEmail());
			if (request == null) {
				response.put("success", false);
				response.put("message", "No active video call request found");
				return response;
			}
			
			response.put("success", true);
			response.put("requestId", request.getId());
			response.put("status", request.getStatus());
			response.put("roomId", request.getRoomId());
			response.put("doctorName", request.getDoctorName());
			
			if (request.getStatus().equals("accepted")) {
				response.put("message", "Video call request accepted by doctor");
			} else if (request.getStatus().equals("rejected")) {
				response.put("message", "Video call request rejected by doctor");
			} else {
				response.put("message", "Video call request is pending");
			}
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Error: " + e.getMessage());
		}
		return response;
	}
	
	// Start video call (when doctor accepts)
	@GetMapping("/startVideoCall")
	public String startVideoCall(@RequestParam String roomId, 
	                           @RequestParam String doctorName,
	                           HttpSession session, ModelMap model) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login-signup";
		}
		
		model.addAttribute("roomID", roomId);
		model.addAttribute("userName", user.getName());
		model.addAttribute("doctorName", doctorName);
		
		return "videocall";
	}
	
	// Feedback submission method
	@PostMapping("/submitFeedback")
	public String submitFeedback(@RequestParam int appointmentId, 
	                            @RequestParam String doctorEmail,
	                            @RequestParam String doctorName,
	                            @RequestParam int rating,
	                            @RequestParam String comment,
	                            HttpSession session, ModelMap model) {
		try {
			User user = (User) session.getAttribute("user");
			if (user == null) {
				model.addAttribute("msg", "Please login to submit feedback.");
				return "redirect:/login-signup";
			}
			
			// Create feedback object
			Feedback feedback = new Feedback();
			feedback.setAppointmentId(appointmentId);
			feedback.setDoctorEmail(doctorEmail);
			feedback.setUserEmail(user.getEmail());
			feedback.setUserName(user.getName());
			feedback.setDoctorName(doctorName);
			feedback.setRating(rating);
			feedback.setComment(comment);
			feedback.setFeedbackDate(new java.sql.Date(System.currentTimeMillis()));
			feedback.setStatus("submitted"); // Default status
			
			// Submit feedback via REST API
			String feedbackURL = "http://localhost:7071/feedback/submit";
			HttpEntity<Feedback> requestEntity = new HttpEntity<>(feedback);
			ResponseEntity<Feedback> result = restTemplate.exchange(
				feedbackURL, HttpMethod.POST, requestEntity, Feedback.class
			);
			
			if (result.getBody() != null) {
				model.addAttribute("msg", "Thank you for your feedback! It has been submitted successfully.");
			} else {
				model.addAttribute("msg", "Failed to submit feedback. Please try again.");
			}
			
		} catch (Exception e) {
			model.addAttribute("msg", "Error submitting feedback. Please try again.");
		}
		
		return "redirect:/user/UserAppointments";
	}
	
	// Get feedback for a specific appointment
	@GetMapping("/getFeedback/{appointmentId}")
	@ResponseBody
	public Feedback getFeedbackForAppointment(@PathVariable int appointmentId) {
		try {
			String feedbackURL = "http://localhost:7071/feedback/appointment/" + appointmentId;
			ResponseEntity<Feedback> result = restTemplate.exchange(
				feedbackURL, HttpMethod.GET, null, Feedback.class
			);
			return result.getBody();
		} catch (Exception e) {
			return null;
		}
	}
	

	
}
