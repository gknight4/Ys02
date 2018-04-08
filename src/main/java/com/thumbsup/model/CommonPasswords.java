package com.thumbsup.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CommonPasswords {
	
	@Id
	String password;
	
	public CommonPasswords() {}
	public CommonPasswords(String password) {
		this.password = password ;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
