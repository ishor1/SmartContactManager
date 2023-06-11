package com.smart.Controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;
@Autowired
private BCryptPasswordEncoder bCryptPasswordEncoder;
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "smartContact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "smartContact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "smartContact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// handler for registering user
//	@PostMapping("/do_register")
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String register(@Valid @ModelAttribute("user") User user,
			@RequestParam(value = "check", defaultValue = "false") boolean check, Model model, HttpSession session,BindingResult result) {
		try { 
			if (!check) {
				throw new Exception("you are not accetpt our term and condition");
			}
			if(result.hasErrors()) {
				System.out.println("error"+ result.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setImageUrl("default.png");
			user.setRole("ROLE_USER");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setEnabled(true);
			
		User result1=userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Your are successfully Register !!", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("something Went wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	
	@GetMapping("/signin")
	public String CustomLogin(Model m) {
		m.addAttribute("title", "Login Page");
		return "signin";
	}

}
