package com.fictionNote.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fictionNote.model.User;
import com.fictionNote.repository.UserRepository;
import com.fictionNote.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	UserRepository userRepository;
	
	public boolean addUsr(User user){
		boolean isSuccess = false;
		//if(userRepository.findByUserName(user.getUserName()).equals(null)){
			userRepository.save(user);
			isSuccess = true;
		//}
		return isSuccess;
	}
}
