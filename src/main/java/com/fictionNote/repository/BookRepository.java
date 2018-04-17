package com.fictionNote.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fictionNote.model.Book;


public interface BookRepository extends MongoRepository<Book, String>{
	public Book findById(String id);
	public List<Book> findByIscheck(String check);
}
