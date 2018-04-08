package com.thumbsup;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Registration {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String parentName ;
	private String parentEmail ;
	private String userName ;
	private String password ;
	private String recaptcha ;
	
	public Registration() {}
	
	public Registration(
			final String parentName,
			final String parentEmail,
			final String userName,
			final String password,
			final String recaptcha
			) {
		this.parentName = parentName ;
		this.parentEmail = parentEmail ;
		this.userName = userName ;
		this.password = password ;
		this.recaptcha = recaptcha ;
		
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentEmail() {
		return parentEmail;
	}

	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRecaptcha() {
		return recaptcha;
	}

	public void setRecaptcha(String recaptcha) {
		this.recaptcha = recaptcha;
	}

}
