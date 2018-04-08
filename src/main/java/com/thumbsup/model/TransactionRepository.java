package com.thumbsup.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	Collection<Transaction> findByUserId(Long userid);
	Collection<Transaction> findByUserUsername(String username);
	Collection<Transaction> findByUserParentname(String parentname);

//	void updateTransaction(Long transactionid, Transaction modTransaction); 
}

/*
package com.thumbsup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); 
}

*/