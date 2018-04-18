package com.fictionNote.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fictionNote.model.Book;
import com.fictionNote.model.BookList;

public interface BookListRepository extends MongoRepository<BookList, String> {
	public List<BookList> findByUserId(String userId);
	public BookList findByBookIdAndUserId(String bid, String uid);
	public BookList findById(String id);
}
