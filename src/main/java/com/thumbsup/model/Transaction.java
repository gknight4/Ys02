package com.thumbsup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Transaction {
	// date, name, amount, userid, transactionId

	@Id
    @GeneratedValue
    private Long id;

    private Date date;
    private String name;
    private int amount;
//    private int userId;

//    @JsonIgnore
    @ManyToOne
    private YsUser user;

    private Transaction() { } // JPA only

    public Transaction(
    		final Date date, 
    		final String name,
    		final int amount,
    		final YsUser user
    		) {
        this.date = date;
        this.name = name;
        this.amount = amount;
        this.user = user ;
//        this.userid = userid;
    }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public YsUser getUser() {
		return this.user;
	}

	public void setUser(YsUser user) {
		this.user = user ;
	}

}

/*package com.thumbsup;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

//    @OneToMany(mappedBy = "user")
//    private Set<Bookmark> bookmarks = new HashSet<>();

    private User() { } // JPA only

    public User(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

//    public Set<Bookmark> getBookmarks() {
//        return bookmarks;
//    }

}
*/