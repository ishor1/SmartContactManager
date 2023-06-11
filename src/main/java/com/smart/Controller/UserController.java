package com.smart.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private Optional<Contact> Optional;

	private java.util.Optional<Contact> findById;
	
	//adding common data form all response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}
	
	//dashboard home
	@RequestMapping("/index")
public String UserController(Model model,Principal principal) {
		String userName=principal.getName();
//		System.out.println("username= "+userName);
		model.addAttribute("title","User Dashboard");
		//get User By UserName;
		User user=userRepository.getUserByUserName(userName);
	model.addAttribute("user",user);
		return "/normal/user_dashboard";
}
	
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model, HttpSession session) {
		model.addAttribute("title","Add Contact");
		return "normal/add_contact_form";
	}
	
	//processing contact form
	
	@PostMapping("/process-contact")
public String procesContact(@ModelAttribute Contact contact,
		@RequestParam("profileImage") MultipartFile file ,
		Principal principal, 
		HttpSession session){

		try {
			//image operations
			if(file.isEmpty()) {
				contact.setImage("default.png");
				//file is empty message print
				System.out.println("file not uploaded");
//				throw new Exception("image neccessary upload");
			}
			else {
			contact.setImage(file.getOriginalFilename());
			File savefile=new ClassPathResource("/static/img").getFile();
		 Path path=Paths.get(savefile.getAbsolutePath()+ File.separator+ file.getOriginalFilename());
		 Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
			System.out.println("file uploaded....");
			}
			//end image operation
			
			User user=userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
	user.getContact().add(contact);
	session.setAttribute("message", new Message("Successfully Contact Added...", "alert-success"));
	User result=this.userRepository.save(user);
			System.out.println("My DATA  "+contact);
			System.out.println("Data Added"+result);
		//success massage
			
			return "normal/add_contact_form";
		
		}
		catch(Exception e) {
System.out.println("Error"+e.getMessage());
e.printStackTrace();
//error massage send
session.setAttribute("message", new Message("something Went wrong !!" + e.getMessage(), "alert-danger"));
return "normal/add_contact_form";
		}
		

}
	
	
	//show contact handler
	@GetMapping("/show-contact/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m, Principal p) {
	String userName=p.getName();
	User user=this.userRepository.getUserByUserName(userName);
	
	//current page
	//contact per page - 3
	Pageable pageable=PageRequest.of(page, 3);
	Page<Contact> contact=this.contactRepository.findContactByUser(user.getId(),pageable);
	m.addAttribute("title","show contact");
	m.addAttribute("contact",contact);
	m.addAttribute("currentPage", page);
	m.addAttribute("totalPages", contact.getTotalPages());

	System.out.println(contact.getTotalPages());

		return "normal/show-contact";
	}
	
	
	
	//show post by particular userid
//	use get or Request mapping -RequestMapping is getmapping
	@RequestMapping("/{cid}/contact")
	public String showContactDetails(Model m, @PathVariable("cid") Integer cid, Principal p) {
		
	Optional<Contact> contactOptional = contactRepository.findById(cid);
		Contact contact=contactOptional.get();
		User user=this.userRepository.getUserByUserName(p.getName());
		if(user.getId()==contact.getUser().getId()) {
			m.addAttribute("title", "show details");
			m.addAttribute(contact);
		}

		return "normal/contact_details";
	}
	
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model m,HttpSession s,Principal p) {
	Optional<Contact> op = contactRepository.findById(cid);
	Contact contact=op.get();
User user=userRepository.getUserByUserName(p.getName());
	if(contact.getUser().getId()==user.getId()) {
		contact.setUser(null);
		//Also img delete from folder
		
		
		contactRepository.delete(contact);
		s.setAttribute("message", new Message("Contact delete Successfully.....","alert-success"));
	}
	else {
		s.setAttribute("message", new Message("you can't delete this contact","alert-danger"));
	}
		 
		return "redirect:/user/show-contact/0";
	}
	
	
	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m) {
		m.addAttribute("title", "update contact");
		Contact contact=this.contactRepository.findById(cid).get();
		m.addAttribute("contact", contact);
		
	return "/normal/update_form";
	}
	
	
	//update form details
	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal p, Model m,HttpSession s) {
	try {
		Contact oldContactDetails=contactRepository.findById(contact.getCid()).get();
		if(!file.isEmpty()) {
			//delete old pic
		if(!file.getOriginalFilename().equals("default.png")) {
			File deleteFile=new ClassPathResource("/static/img").getFile();
			File file1=new File(deleteFile,oldContactDetails.getImage());
			file1.delete();
		}
			
			//upload new photo
			contact.setImage(file.getOriginalFilename());
			File savefile=new ClassPathResource("/static/img").getFile();
		 Path path=Paths.get(savefile.getAbsolutePath()+ File.separator+ file.getOriginalFilename());
		 Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
			System.out.println("file uploaded....");	
			contact.setImage(file.getOriginalFilename());
		}
		else{
contact.setImage(oldContactDetails.getImage());
		} 
		User u=userRepository.getUserByUserName(p.getName());
		
      contact.setUser(u);
    contactRepository.save(contact);
	
		s.setAttribute("message", new Message("Contact update Successfully.....","alert-success"));
	}catch(Exception e) {
		s.setAttribute("message", new Message("someThings wents wrong !!.....","alert-danger"));
	
		e.printStackTrace();
	}
		
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title","profile Page");
		
		return "/normal/profile";
		
	}
	
	
	
	
	@GetMapping("/setting")
	public String processSetting( Model m) {
		m.addAttribute("title","setting");
		
		return "/normal/setting";
		
	}
	
	
	@PostMapping("/changePass")
	public String UserSetting(@RequestParam("oldPassword") String oldP, 
			@RequestParam("newPassword") String newPass,
			Principal p,HttpSession s, 
			Model m) {
		User u=userRepository.getUserByUserName(p.getName());
		System.out.println("password chnge"+u.getPassword()+"and"+newPass);
		if(bCryptPasswordEncoder.matches(oldP, u.getPassword())) {
			u.setPassword(bCryptPasswordEncoder.encode(newPass));
			userRepository.save(u);
			s.setAttribute("message",new Message("Successfully updated password..", "alert-success"));
		}
		else {
			s.setAttribute("message",new Message("Old password not match password..", "alert-danger"));
		}
		
		m.addAttribute("title","setting");
		
		return "/normal/setting";
		
	}
                    
}
