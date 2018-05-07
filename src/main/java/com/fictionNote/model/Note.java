package com.fictionNote.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter@Setter
@ToString
public class Note {
	@Id
	private String id;
	private String bookId;
	private String userId;
	private String title;
	private int fromPage, toPage;
	private String content;
	//private String timeb;
	//private String timee;
	private String time;
	private boolean visible;
	private String type; //blurb extraction
	private List<Review> reviews;
	private List<String> likes;
	
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
		Note other = (Note) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
