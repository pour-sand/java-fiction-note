package com.fictionNote.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class BookList {
	@Id
	private String id;
	private String time;
	private Date dtime;
	private String userId;
	private String bookId;
	
	public BookList() {
	}
	public BookList(String time, String userId, String bookId) {
		super();
		this.time = time;
		this.userId = userId;
		this.bookId = bookId;
	}
}
