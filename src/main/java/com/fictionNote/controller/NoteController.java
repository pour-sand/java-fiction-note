package com.fictionNote.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.fictionNote.model.Review;
import com.fictionNote.repository.BookRepository;
import com.fictionNote.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.fictionNote.model.Note;
import com.fictionNote.repository.NoteRepository;
import com.fictionNote.service.NoteService;
import com.fictionNote.utils.DateUtils;

@Controller
public class NoteController {
	@Autowired
	NoteService noteService;
	@Autowired
	NoteRepository noteRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	BookRepository bookRepository;
	
	@RequestMapping(value = "/addNote", method = { RequestMethod.POST})
	@ResponseBody
	public String addNote(@RequestBody Note note) {
		note.setTime(DateUtils.formateNoteTime(new Date()));
		/*if(!DateUtils.compareNoteTime(note.getTimeb(), note.getTimee())
				|| !DateUtils.compareNoteTime(note.getTimee(), note.getTime())){
			return "Wrong time error";
		}*/
		if(note.getId()!=null && !note.getId().equals(""))
			noteRepository.delete(noteRepository.findById(note.getId()));
		//note.setTimeb(DateUtils.formateNoteTime(note.getTimeb()));
		//note.setTimee(DateUtils.formateNoteTime(note.getTimee()));
		noteRepository.save(note);
		//noteService.addNote(note);
		return "success";
	}
	
	@RequestMapping(value = "/userAllNotes", method = { RequestMethod.GET})
	@ResponseBody
	public List<Note> getNotes(HttpServletRequest request) {
		return noteRepository.findByUserId(request.getParameter("uid"));
	}
	
	@RequestMapping(value = "/oneBookNotes", method = { RequestMethod.GET})
	@ResponseBody
	public List<Note> getOneBookNotes(HttpServletRequest request) {
		return noteService.oneBookNotes(request.getParameter("bid"));
	}
	
	@RequestMapping(value = "/deleteNote", method = { RequestMethod.POST})
	@ResponseBody
	public String delNote(HttpServletRequest request) {
		Note note = noteRepository.findById(request.getParameter("id"));
		if(note != null) {
			noteRepository.delete(note);
			return "success";
		}
		return null;
	}

	@RequestMapping(value = "/addReview", method = { RequestMethod.POST})
	@ResponseBody
	public String addReview(@RequestBody Note note, HttpServletRequest request) {
		System.out.println("note "+note.getReviews());

		Note n = noteRepository.findById(note.getId());
		List<Review> rs=n.getReviews();
		rs.add(note.getReviews().get(0));
		n.setReviews(rs);
		noteRepository.delete(n);
		noteRepository.save(n);
		return "success";
	}

	@RequestMapping(value = "/addLike", method = { RequestMethod.POST})
	@ResponseBody
	public String addLike(HttpServletRequest request) {
		String name = "";
		if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
		//String uid = userRepository.findByUserName(name).getId();
		Note note = noteRepository.findById(request.getParameter("id"));
		List<String> likes = note.getLikes();
		if(likes.contains(name)){
			likes.remove(name);
			note.setLikes(likes);
			noteRepository.delete(note);
			noteRepository.save(note);
			return "removed";
		}else{
			likes.add(name);note.setLikes(likes);
			noteRepository.delete(note);
			noteRepository.save(note);
			return "added";
		}
	}

	@RequestMapping(value = "/getLikeNum", method = { RequestMethod.GET})
	@ResponseBody
	public String likeN(HttpServletRequest request) {
		String name = "";
		if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
		String uid = userRepository.findByUserName(name).getId();
		List<Note> notes = noteRepository.findByUserId(uid);
		Map<String, String> likes = new HashMap<String, String>();
		int n = 0;
		for(int i=0; i<notes.size(); i++){
			List<String> like =  notes.get(i).getLikes();
			String book = bookRepository.findById(notes.get(i).getBookId()).getTitle();
			for(int j=0; j<like.size(); j++){
				likes.put(book, like.get(j));
			}
		}
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		return gson.toJson(likes);
	}

	@RequestMapping(value = "/getReviewNum", method = { RequestMethod.GET})
	@ResponseBody
	public List<Review> reviewN(HttpServletRequest request) {
		String name = "";
		if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
		String uid = userRepository.findByUserName(name).getId();
		List<Note> notes = noteRepository.findByUserId(uid);
		List<Review> res = new ArrayList<Review>();
		for(int i=0; i<notes.size(); i++){
			String book = bookRepository.findById(notes.get(i).getBookId()).getTitle();
			List<Review> reviews = notes.get(i).getReviews();
			for(int j=0; j<reviews.size(); j++){
				String user = reviews.get(j).getUser();
				String photo = userRepository.findByUserName(user).getPhoto();
				reviews.get(j).setInfo(photo);
				reviews.get(j).setType(book);
			}
			res.addAll(reviews);
		}
		return res;
	}

	@RequestMapping(value = "/getReviewUnChecked", method = { RequestMethod.GET})
	@ResponseBody
	public List<Review> reviewNUnchecked(HttpServletRequest request) {
		List<Review> res = reviewN(request);
		Iterator<Review> it = res.iterator();
		while (it.hasNext()){
			Review r = it.next();
			if(r.isChecked()) it.remove();
		}
		return res;
	}

	/*@RequestMapping(value = "/getReviewNum", method = { RequestMethod.GET})
	@ResponseBody
	public String reviewCheck(HttpServletRequest request) {
		String name = "";
		if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
		String uid = userRepository.findByUserName(name).getId();
		List<Note> notes = noteRepository.findByUserId(uid);
		List<Review> res = new ArrayList<Review>();
		for(int i=0; i<notes.size(); i++){
			List<Review> reviews = notes.get(i).getReviews();
			for(int j=0; j<reviews.size(); j++){

			}
			res.addAll(reviews);
		}
		return "";
	}*/

	@RequestMapping(value = "/checkMsg", method = { RequestMethod.POST})
	@ResponseBody
	public String checkMsg(@RequestBody List<Review> res, HttpServletRequest request) {
		String name = "";
		if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
		String uid = userRepository.findByUserName(name).getId();
		List<Note> notes = noteRepository.findByUserId(uid);
		boolean change = false;
		for(int i=0; i<notes.size(); i++){
			List<Review> rs = notes.get(i).getReviews();
			for(int j=0; j<rs.size(); j++){
				for(int k=0; k<res.size(); k++){
					if(res.get(k).getId().equals(rs.get(j).getId())) {
						rs.get(j).setChecked(true);
						change = true;
					}
				}
			}
			if(change){
				notes.get(i).setReviews(rs);
				noteRepository.save(notes.get(i));
			}
		}
		return "success";
	}
}
