package com.fictionNote.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Book {
	@Id
	private String id;
	private int totalPage;
	private String title;
	private String subtitle;
	private String originalName;
	private String author;
	private String translator;
	private String picture;
	private String press;
	private String pubTime;
	private String time;
	private Date dtime;
	private String intro;
	private String ischeck;
	private String uid;
	private boolean inList;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
