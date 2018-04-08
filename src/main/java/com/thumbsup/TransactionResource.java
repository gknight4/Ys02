package com.thumbsup;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import com.thumbsup.model.Transaction;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Optional;

// tag::code[]
public class TransactionResource extends ResourceSupport {
	
	private final Transaction transaction ;
	
	public TransactionResource(Transaction transaction) {
//    	System.out.println("TransactionResource");
		
//		String username = bookmark.getAccount().getUsername();
    	long transactionid = transaction.getId() ;
		this.transaction = transaction;
		this.add(new Link(transaction.getName(), "transaction-name"));
		this.add(linkTo(YsRestController.class, transactionid).withRel("transactions"));
//		this.add(linkTo(methodOn(YsRestController.class, transactionid)
//				.getTransaction(transactionid, null)).withSelfRel());

//		this.add(new Link(bookmark.getUri(), "bookmark-uri"));
//		this.add(linkTo(BookmarkRestController.class, username).withRel("bookmarks"));
//		this.add(linkTo(methodOn(BookmarkRestController.class, username)
//				.readBookmark(username, bookmark.getId())).withSelfRel());
	}

	public Transaction getTransaction() {
		return transaction;
	}


}
