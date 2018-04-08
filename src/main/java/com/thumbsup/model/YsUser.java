package com.thumbsup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class YsUser {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String parentname;
    private String parentemail;
    private Long confirmcode;
    private Long flags ;

//    @JsonIgnore
    private String password;

//    @OneToMany(mappedBy = "user")
//    private Set<Transaction> transactions = new HashSet<>();
    
//    private Set<Bookmark> bookmarks = new HashSet<>();

    YsUser() { } // JPA only

//    public User(final String username, final String password) {
//        this.username = username;
//        this.password = password;
//    }

    public YsUser(
    		final String username, 
    		final String parentname, 
    		final String parentemail,
    		final String password,
    		final Long confirmcode) {
        this.username = username;
        this.parentname = parentname ; // (parentname != null) ? parentname : "none";
        this.password = password;
        this.parentemail = parentemail;
        this.confirmcode = confirmcode;
        this.flags = 0L ;
    }

    public Long getId() {
        return id;
    }
    
    public Long getConfirmCode() {
    	return confirmcode ;
    }
    
    public void setConfirmCode(Long confirmcode) {
    	this.confirmcode = confirmcode ;
    }
    
    public void setFlags(Long flags) {
    	this.flags = flags;
    }
    
    public Long getFlags() {
    	return flags ;
    }

    public String getUsername() {
        return username;
    }

    public void setParentname(String parentname) {
    	this.parentname = parentname ;
    }

    public String getParentname() {
        return parentname;
    }

   	@JsonIgnore
    public String getPassword() {
        return password;
    }
    
   	@JsonProperty
    public void setPassword(String password) {
        this.password = password ;
    }
/*
{
	"username": "randol",
	"parentname": "gknight4",
	"password": "mypass
}
    
 */

//    public Set<Bookmark> getBookmarks() {
//        return bookmarks;
//    }

}
