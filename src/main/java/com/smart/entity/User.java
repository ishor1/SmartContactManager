package com.smart.entity;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
public class User {
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

@NotBlank(message ="user Name can not be empty !!")
@Size(min=3, max=20, message="user Name between 3 to 20 character")
private String name;

@Column(unique=true)

private String email;
private String password;
private boolean enabled;
private String imageUrl;
@Column(length=500)
private String about;
private String role;

@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
private List<Contact> contact=new ArrayList<Contact>();

public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public boolean isEnabled() {
	return enabled;
}
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}
public String getImageUrl() {
	return imageUrl;
}
public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}
public String getAbout() {
	return about;
}
public void setAbout(String about) {
	this.about = about;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}

public List<Contact> getContact() {
	return contact;
}
public void setContact(List<Contact> contact) {
	this.contact = contact;
}
public User(int id, String name, String email, String password, boolean enabled, String imageUrl, String about,
		String role) {
	super();
	this.id = id;
	this.name = name;
	this.email = email;
	this.password = password;
	this.enabled = enabled;
	this.imageUrl = imageUrl;
	this.about = about;
	this.role = role;
}
public User(String name, String email, String password, boolean enabled, String imageUrl, String about, String role) {
	super();
	this.name = name;
	this.email = email;
	this.password = password;
	this.enabled = enabled;
	this.imageUrl = imageUrl;
	this.about = about;
	this.role = role;
}
public User() {
	super();
}
@Override
public String toString() {
	return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", enabled=" + enabled
			+ ", imageUrl=" + imageUrl + ", about=" + about + ", role=" + role + ", contact=" + contact + "]";
}




}