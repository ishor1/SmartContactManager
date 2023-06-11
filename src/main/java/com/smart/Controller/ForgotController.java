package com.smart.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Services.EmailServices;
import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	Random randam=new Random(1000);
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private EmailServices emailServices;
	
	@Autowired
	private BCryptPasswordEncoder bcencode;

	@RequestMapping(value="/forgot-password", method=RequestMethod.GET)
public String openEmailForm(Model m) {
		
		m.addAttribute("title","forgot password");
	return"/forgot_email_form";
}
	
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session, Model m,HttpSession s)
	{	
	int otp=randam.nextInt(99999);
	String subject="OTP From Smart Contact management";
	
	String message=""+"<div style='border:1px solid #e2e2e2; padding:20px; text-align:center;'>"
			+ "<h1>"
			+ "OTP is "
			+ "<b>"+otp
			+"</b>"
			+ "</h1>"
			+ "</div>";
			
	
	
	
	String to=email;
	boolean flag=emailServices.sendEmail(subject, message, to);
	if(flag) {
		m.addAttribute("title","verify otp");
		s.setAttribute("email",email);
		System.out.println("this is otp="+otp);
		s.setAttribute("myotp", otp);
		return "/verify_otp";
	}
	else {
		session.setAttribute("message", new Message("Email is invalid","alert-danger"));
		return "/forgot-password";
		
	}
	}
	
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, Model m,HttpSession session) {
		//featching user from session
		int myOtp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		if(myOtp==otp) {
			m.addAttribute("title","change password");
			User user=userRepository.getUserByUserName(email);
			if(user==null) {
				session.setAttribute("message", new Message("User does not exist with this email","alert-danger"));
				
				return "/forgot_email_form";
			}
			session.setAttribute("email", email);
			m.addAttribute("title","change password");
			return "/change_password";
		}
		else {
			session.setAttribute("message", new Message("OTP not match","alert-danger"));
			return "/verify_otp";
		}
	}

	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("password") String password,Model m,HttpSession session) {
		String email=(String)session.getAttribute("email");
		User user=userRepository.getUserByUserName(email);
		
		user.setPassword(bcencode.encode(password));
		userRepository.save(user);

//		Direct message send in param variable like as form in post method
		return "redirect:/signin?change=password change successfully....";
	}
}
