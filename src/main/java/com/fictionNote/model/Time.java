package com.fictionNote.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
@ToString
public class Time {
	@Id
	String id;
	String userId;
	String theme;
	String type;
	String[] books;
	String msg;
	String begin;
	String end;
	String fromPage;
	String toPage;
	String during;
}
