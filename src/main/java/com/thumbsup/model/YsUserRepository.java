package com.thumbsup.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface YsUserRepository extends JpaRepository<YsUser, Long> {
    Optional<YsUser> findByUsername(String username); 
    Collection<YsUser> findByParentname(String parentname); 
    Optional<YsUser> findById(long id); 
}

