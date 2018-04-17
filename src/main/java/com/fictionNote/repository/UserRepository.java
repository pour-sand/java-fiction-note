package com.fictionNote.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fictionNote.model.User;

public interface UserRepository extends MongoRepository<User, String>{
	public User findByUserName(String name);
	public User findByUserNameAndPassword(String name, String password);
	public User findById(String id);
}
