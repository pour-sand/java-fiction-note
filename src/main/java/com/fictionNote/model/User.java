package com.fictionNote.model;

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Setter
@Getter
@ToString
public class User {
	@Id
	private String id;
	private String userName;
	private String password;
	private String sex;
	private String time;
	private String role;
	private String photo;
	private String[] loginTime;
	
}
