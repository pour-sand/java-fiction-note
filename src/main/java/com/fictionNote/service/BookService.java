package com.fictionNote.service;

import java.util.List;

import com.fictionNote.model.Book;

public interface BookService {
	public List<Book> checkList(List<Book> books, String uid);
	public List<Book> bookList(String uid);
}
