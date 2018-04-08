package com.thumbsup;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import com.thumbsup.model.YsUser;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class YsUserResource extends ResourceSupport {
	private final YsUser user ;
	public YsUserResource(YsUser user) {
		Long userid = user.getId();
		this.user = user ;
		this.add(new Link(user.getUsername(), "username"));
//		this.add(linkTo(YsRestController.class, userid).withRel("users"));
//		this.add(linkTo(methodOn(YsRestController.class, userid)
//				.getUser(userid, null)).withSelfRel());
	}
	
	public YsUser getUser() {
		return user ;
	}

}