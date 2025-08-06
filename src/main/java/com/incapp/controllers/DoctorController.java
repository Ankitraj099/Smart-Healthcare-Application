package com.incapp.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
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
import com.incapp.models.PasswordResetToken;
import com.incapp.models.VideoCallRequest;
import com.incapp.services.PasswordResetService;
import com.incapp.services.VideoCallService;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/doctor")
public class DoctorController {
	
	private RestTemplate restTemplate=new RestTemplate();
	private String URL="http://localhost:7071/doctor";
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private PasswordResetService passwordResetService;
	
	@Autowired
	private VideoCallService videoCallService;
	
	@PostMapping("/login")
	public String login(@RequestParam String email,@RequestParam String password, HttpSession session,  ModelMap m) {
		String API="/getDoctor/"+email;
		ResponseEntity<Doctor> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, Doctor.class);
		Doctor doctor=result.getBody();
		if(doctor!=null && passwordEncoder.matches(password, doctor.getPassword())) {
			session.setAttribute("doctor", doctor);
			
			API="/getDocNotAvail/"+email;
			List<DoctorNotAvail> dna=restTemplate.getForObject(URL+API, List.class);
			session.setAttribute("dna",dna);
	        session.setAttribute("onlineStatus", "offline");
			return "redirect:/doctor/DoctorHome";
		}else {
			m.addAttribute("msg","Invalid Credentials!");
			return "login-signup";
		}
	}
	@GetMapping("/DoctorAppointments")
	public String DoctorAppointments(HttpSession session,ModelMap model) {
		Doctor doctor = (Doctor) session.getAttribute("doctor");
		if (doctor == null) {
			return "redirect:/login-signup";
		}
		String URL="http://localhost:7071/appointment";
		String API="/getByDoctorEmail/"+doctor.getEmail();
		List<Appointments> appointments=restTemplate.getForObject(URL+API,List.class);
		model.addAttribute("apts",appointments);
		model.addAttribute("doctor", doctor);
		return "DoctorAppointments";
	}
	@RequestMapping("/DoctorHome")
	public String DoctorHome(HttpSession session) {
		Doctor doctor = (Doctor) session.getAttribute("doctor");
		if (doctor == null) {
			return "redirect:/login-signup";
		}
		return "DoctorHome";
	}
	
	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam String email,@RequestParam String oldpassword,@RequestParam(value = "newpassword") String newPassword, HttpSession session, ModelMap m) {
		String API="/getDoctor/"+email;
		ResponseEntity<Doctor> result= restTemplate.exchange(URL+API,HttpMethod.GET, null, Doctor.class);
		Doctor doctor=result.getBody();
		if(doctor!=null && passwordEncoder.matches(oldpassword, doctor.getPassword())) {
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
		return "DoctorHome";
	}
	
	@PostMapping("/forgetPassword")
	public String processForgotPassword(@RequestParam String email, ModelMap m) {
		// Check if doctor exists
		String API = "/getDoctor/" + email;
		ResponseEntity<Doctor> result = restTemplate.exchange(URL + API, HttpMethod.GET, null, Doctor.class);
		Doctor doctor = result.getBody();
		if (doctor == null) {
			m.addAttribute("msg", "Email not found!");
			return "login-signup";
		}
		String token = passwordResetService.createPasswordResetToken(email);
		passwordResetService.sendResetEmail(email, token, "doctor");
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
		return "reset-password"; // Create this Thymeleaf template
	}
	
	@PostMapping("/reset-password")
	public String processResetPassword(@RequestParam String token, @RequestParam String newPassword, ModelMap m) {
		PasswordResetToken resetToken = passwordResetService.getByToken(token);
		if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
			m.addAttribute("msg", "Invalid or expired token.");
			return "login-signup";
		}
		// Update password
		String email = resetToken.getEmail();
		String API = "/getDoctor/" + email;
		ResponseEntity<Doctor> result = restTemplate.exchange(URL + API, HttpMethod.GET, null, Doctor.class);
		Doctor doctor = result.getBody();
		if (doctor == null) {
			m.addAttribute("msg", "Doctor not found.");
			return "login-signup";
		}
		String encodedPassword = passwordEncoder.encode(newPassword);
		Map<String, String> data = new HashMap<>();
		data.put("email", email);
		data.put("newPassword", encodedPassword);
		HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(data);
		API = "/updatePassword";
		restTemplate.exchange(URL + API, HttpMethod.PUT, requestEntity, Boolean.class);
		passwordResetService.deleteByToken(token);
		m.addAttribute("msg", "Password reset successful. Please login.");
		return "login-signup";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute Doctor doctor,HttpSession session, ModelMap m) {
		doctor.setDoctorDetails(new DoctorDetails());
		doctor.setDoctorAvail(new DoctorAvail());
		doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
		String API="/register";
		HttpEntity<Doctor> requestEntity=new HttpEntity<Doctor>(doctor);
		ResponseEntity<Boolean> result= restTemplate.exchange(URL+API,HttpMethod.POST,requestEntity,Boolean.class);
		if(result.getBody()) {
			session.setAttribute("doctor", doctor);
	        session.setAttribute("onlineStatus", "offline");
			return "redirect:/doctor/DoctorHome";
		}else {
			m.addAttribute("msg","Email ID Already Exist!");
			return "login-signup";
		}
	}
	@GetMapping("/getPhoto")
	public void getPhoto(@RequestParam String email,ServletResponse response) throws IOException {
		String API="/getDoctorPhoto/"+email;
		ResponseEntity<byte[]> result=restTemplate.exchange(URL+API, HttpMethod.GET, null, byte[].class);
		byte image[]=result.getBody();
		if(image==null || image.length==0 ) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/images/doctor-dp.png");
			image=is.readAllBytes();
		}
		response.getOutputStream().write(image);
	}
	@PostMapping("/updatePhoto")
	public String updatePhoto(HttpSession session,@RequestPart("photo") MultipartFile photo,ModelMap m) throws IOException {
		Doctor doctor=(Doctor)session.getAttribute("doctor");
		if (doctor == null) {
			return "redirect:/login-signup";
		}
		String API="/updateDoctorPhoto/"+doctor.getEmail();
		HttpEntity<byte[]> requestEntity=new HttpEntity<>(photo.getBytes());
		restTemplate.put(URL+API, requestEntity);
		m.addAttribute("msg","Photo updated successfully!");
		API="/getDoctor/"+doctor.getEmail();
		ResponseEntity<Doctor> result=restTemplate.exchange(URL+API,HttpMethod.GET,null,Doctor.class);
		doctor=result.getBody();
		session.setAttribute("doctor", doctor);
		return "redirect:/doctor/DoctorHome";
	}
	
	@PostMapping("/updateDoctor")
	public String updateDoctor(HttpSession session,@ModelAttribute Doctor doctor,@ModelAttribute DoctorDetails doctorDetails, ModelMap m) {
		Doctor sessionDoctor = (Doctor) session.getAttribute("doctor");
		if (sessionDoctor == null) {
			return "redirect:/login-signup";
		}
		doctor.setDoctorDetails(doctorDetails);
		String API="/updateDoctor";
		HttpEntity<Doctor> requestEntity=new HttpEntity<Doctor>(doctor);
		ResponseEntity<Doctor> result=restTemplate.exchange(URL+API,HttpMethod.PUT,requestEntity,Doctor.class);
		if(result.getBody()!=null) {
			session.setAttribute("doctor", result.getBody());
			m.addAttribute("msg","Updation Success!");
		}else {
			m.addAttribute("msg","Updation Failed!");
		}
		return "DoctorHome";
	}
	
	@PostMapping("/updateDocAvail") 
	public String updateDocAvail(@ModelAttribute DoctorAvail doctorAvail,HttpSession session,ModelMap m) {
		Doctor doctor = (Doctor) session.getAttribute("doctor");
		if (doctor == null) {
			return "redirect:/login-signup";
		}
		String email = doctor.getEmail();
		String API="/updateDocAvail/"+email;
		HttpEntity<DoctorAvail> requestEntity=new HttpEntity<DoctorAvail>(doctorAvail);
		ResponseEntity<Doctor> result=restTemplate.exchange(URL+API,HttpMethod.PUT,requestEntity,Doctor.class);
		if(result.getBody()!=null) {
			session.setAttribute("doctor", result.getBody());
			m.addAttribute("msg","Updation Success!");
		}else {
			m.addAttribute("msg","Updation Failed!");
		}
		return "DoctorHome";
	}
	
	@PostMapping("/addDocNotAvail")
	public String addDocNotAvail(HttpSession session,@ModelAttribute DoctorNotAvail doctorNotAvail ,ModelMap m) {
		Doctor doctor = (Doctor) session.getAttribute("doctor");
		if (doctor == null) {
			return "redirect:/login-signup";
		}
		String API="/addDocNotAvail";
		boolean result=restTemplate.postForObject(URL+API, doctorNotAvail, Boolean.class);
		if(result) {
			m.addAttribute("msg","Success!");
			API="/getDocNotAvail/"+doctorNotAvail.getEmail();
			List<DoctorNotAvail> dna=restTemplate.getForObject(URL+API, List.class);
			session.setAttribute("dna",dna);
		}else {
			m.addAttribute("msg","Already Exist!");
		}
		return "DoctorHome";
	}
	
	@GetMapping("/cancelDocNotAvail")
	public String cancelDocNotAvail(@RequestParam int id,HttpSession session,ModelMap m) {
		Doctor doctor = (Doctor) session.getAttribute("doctor");
		if (doctor == null) {
			return "redirect:/login-signup";
		}
		String API="/cancelDocNotAvail/"+id;
		ResponseEntity<Boolean> result= restTemplate.exchange(URL+API,HttpMethod.DELETE,null,Boolean.class);
		if(result.getBody()) {
			m.addAttribute("msg","Success!");
			String email = doctor.getEmail();
			API="/getDocNotAvail/"+email;
			List<DoctorNotAvail> dna=restTemplate.getForObject(URL+API, List.class);
			session.setAttribute("dna",dna);
		}else {
			m.addAttribute("msg","Leave Does Not Exist!");
		}
		return "redirect:/doctor/DoctorHome";
	}
	
	@GetMapping("/DoctorOnline")
	public String doctorOnline(@RequestParam String status,HttpSession session,ModelMap model) {
		Doctor d=(Doctor)session.getAttribute("doctor");
		if (d == null) {
			return "redirect:/login-signup";
		}
		String email=d.getEmail();
		
		if(status.equalsIgnoreCase("online")) {
			// Set doctor as online in video call service
		    videoCallService.setDoctorOnline(email, true);
		    session.setAttribute("onlineStatus", "online");
		    model.addAttribute("doctor", d);
		    
			return "doctorWaitingRoom"; // New waiting interface instead of videocallDoctor
		}else {
			String API="/doctorOffline/"+email;
			restTemplate.delete(URL+API);
		    session.setAttribute("onlineStatus", "offline");
		    
		    // Set doctor as offline in video call service
		    videoCallService.setDoctorOnline(email, false);
		    
			return "redirect:/doctor/DoctorHome";
		}
	}
	
	// Get pending video call requests for doctor
	@GetMapping("/getPendingVideoCallRequests")
	@ResponseBody
	public List<VideoCallRequest> getPendingVideoCallRequests(HttpSession session) {
		Doctor doctor = (Doctor) session.getAttribute("doctor");
		if (doctor == null) {
			return new ArrayList<>();
		}
		
		List<VideoCallRequest> requests = videoCallService.getPendingRequestsForDoctor(doctor.getEmail());
		return requests;
	}
	
	// Accept video call request
	@PostMapping("/acceptVideoCallRequest")
	@ResponseBody
	public Map<String, Object> acceptVideoCallRequest(@RequestParam String requestId, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			Doctor doctor = (Doctor) session.getAttribute("doctor");
			if (doctor == null) {
				response.put("success", false);
				response.put("message", "Doctor not logged in");
				return response;
			}
			
			VideoCallRequest request = videoCallService.acceptVideoCallRequest(requestId);
			response.put("success", true);
			response.put("message", "Video call request accepted");
			response.put("roomId", request.getRoomId());
			response.put("userName", request.getUserName());
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}
	
	// Reject video call request
	@PostMapping("/rejectVideoCallRequest")
	@ResponseBody
	public Map<String, Object> rejectVideoCallRequest(@RequestParam String requestId, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		try {
			Doctor doctor = (Doctor) session.getAttribute("doctor");
			if (doctor == null) {
				response.put("success", false);
				response.put("message", "Doctor not logged in");
				return response;
			}
			
			videoCallService.rejectVideoCallRequest(requestId);
			response.put("success", true);
			response.put("message", "Video call request rejected");
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}
	
	// Join video call (when doctor accepts)
	@GetMapping("/joinVideoCall")
	public String joinVideoCall(@RequestParam String roomId, 
	                          @RequestParam String userName,
	                          HttpSession session, ModelMap model) {
		Doctor doctor = (Doctor) session.getAttribute("doctor");
		if (doctor == null) {
			return "redirect:/login-signup";
		}
		
		model.addAttribute("roomID", roomId);
		model.addAttribute("userName", doctor.getName()); // This is the doctor's name for ZegoCloud
		model.addAttribute("patientName", userName); // This is the patient's name
		model.addAttribute("doctor", doctor); // Add doctor object to model
		
		return "videocallDoctor";
	}
	
	@GetMapping("/doctorProfile")
	public String doctorProfile(HttpSession session, ModelMap model) {
		Doctor doctor = (Doctor) session.getAttribute("doctor");
		if (doctor == null) {
			return "redirect:/login-signup";
		}
		model.addAttribute("doctor", doctor);
		return "doctorProfile";
	}
	
	@GetMapping("/oauth2success")
	public String googleLoginSuccess(@AuthenticationPrincipal OAuth2User principal, HttpSession session) {
	    String email = principal.getAttribute("email");
	    String name = principal.getAttribute("name");
	    String API = "/getDoctor/" + email;
	    ResponseEntity<Doctor> result = restTemplate.exchange(URL + API, HttpMethod.GET, null, Doctor.class);
	    Doctor doctor = result.getBody();
	    if (doctor != null) {
	        session.setAttribute("doctor", doctor);
	        session.setAttribute("onlineStatus", "offline");
	    } else {
	        doctor = new Doctor();
	        doctor.setName(name);
	        doctor.setEmail(email);
	        doctor.setPassword(passwordEncoder.encode("jggJHGH@jgjhgjU%465"));
	        doctor.setDoctorDetails(new DoctorDetails());
	        doctor.setDoctorAvail(new DoctorAvail());
	        API = "/register";
	        HttpEntity<Doctor> requestEntity = new HttpEntity<Doctor>(doctor);
	        restTemplate.exchange(URL + API, HttpMethod.POST, requestEntity, Boolean.class);
	        session.setAttribute("doctor", doctor);
	        session.setAttribute("onlineStatus", "offline");
	    }
	    return "redirect:/doctor/DoctorHome";
	}
	

	
}
