package com.fictionNote.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fictionNote.model.Book;
import com.fictionNote.model.BookList;
import com.fictionNote.repository.BookListRepository;
import com.fictionNote.repository.BookRepository;
import com.fictionNote.service.BookService;
import com.fictionNote.utils.DateUtils;

@Service
public class BookServiceImpl implements BookService{
	@Autowired
	BookListRepository blRepository;
	@Autowired
	BookRepository bookRepository;
	
	public List<Book> checkList(List<Book> books, String uid){
		List<BookList> bList = blRepository.findByUserId(uid);
		Iterator<BookList> it = bList.iterator();
		while(it.hasNext()){
			BookList bl = it.next();
			Book book = bookRepository.findById(bl.getBookId());
			if(books.contains(book)){
				books.remove(book);
				book.setInList(true);
				books.add(book);
			}
		}
		return books;
	}
	
	public List<Book> bookList(String uid){
		List<Book> books = new ArrayList<Book>();
		List<BookList> bl = blRepository.findByUserId(uid);
		if(bl.isEmpty()) return null;
		for(int i=0; i<bl.size(); i++){
			Book book = bookRepository.findById(bl.get(i).getBookId());
			String time = bl.get(i).getTime();
			//Date date = bl.get(i).getDtime();//DateUtils.stringToDate(time, "EEE MMM dd HH:mm:ss Z yyyy");
			//time = DateUtils.dateToString(date, "yyyy-MM-dd HH:mm");
			book.setTime(time);
			books.add(book);
		}
		return books;
	}
}
