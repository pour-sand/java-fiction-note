package com.fictionNote.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fictionNote.model.Book;
import com.fictionNote.model.BookList;
import com.fictionNote.model.Result;
import com.fictionNote.repository.BookListRepository;
import com.fictionNote.service.BookService;
import com.fictionNote.utils.DateUtils;

@Controller
public class BookListController {
	@Autowired
	BookListRepository blRepository;
	@Autowired
	BookService bookService;
	
	@RequestMapping(value="/addToBookList", method={ RequestMethod.POST})
	@ResponseBody
	public String insertToList(HttpServletRequest request){
		String u = request.getParameter("userId");
		String b = request.getParameter("bookId");
		System.out.println("uid bid===="+u+" "+b);
		if(blRepository.findByBookIdAndUserId(b, u) != null){
			return "existed";
		}
		String time = DateUtils.dateToString(new Date(), DateUtils.patternB);
		BookList bookList = new BookList(time, u, b);
		blRepository.save(bookList);
		return "successful";
	}
	
	@RequestMapping(value="/updateList",method={ RequestMethod.PUT})
	@ResponseBody
	public void updateList(HttpServletRequest request){
		String u = request.getParameter("userId");
		String b = request.getParameter("bookId");
		BookList bookList = new BookList(new Date().toString(), b, u);
		blRepository.save(bookList);
	}
	
	@RequestMapping(value="/delInList",method={ RequestMethod.POST})
	@ResponseBody
	public String delInList(HttpServletRequest request){
		
		String u = request.getParameter("userId");
		String b = request.getParameter("bookId");
		BookList bookList = blRepository.findByBookIdAndUserId(b, u);
		if(bookList!=null) {
			blRepository.delete(bookList);
			return "success";
		}
		return null;
	}
	
	@RequestMapping(value="/getUserBookList", method={ RequestMethod.POST})
	@ResponseBody
	public Result<Object> getBookList(HttpServletRequest request){
		String u = request.getParameter("userId");
		List<Book> bookList = bookService.bookList(u);
		if(bookList.isEmpty()) return new Result(201, "Your List is empty!");
		return new Result(200, "", bookList);
	}
	
	@RequestMapping(value="/getAdminBookList", method={ RequestMethod.POST})
	@ResponseBody
	public Result<Object> getAdminList(HttpServletRequest request){
		String u = request.getParameter("userId");
		List<Book> bookList = bookService.bookList(u);
		if(bookList.isEmpty()) return new Result(201, "Your List is empty!");
		return new Result(200, "", bookList);
	}
	
}
