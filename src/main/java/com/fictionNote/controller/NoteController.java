package com.fictionNote.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fictionNote.model.Review;
import com.fictionNote.repository.UserRepository;
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
	
	@RequestMapping(value = "/addNote", method = { RequestMethod.POST})
	@ResponseBody
	public String addNote(@RequestBody Note note) {
		note.setTime(DateUtils.formateNoteTime(new Date()));
		/*if(!DateUtils.compareNoteTime(note.getTimeb(), note.getTimee())
				|| !DateUtils.compareNoteTime(note.getTimee(), note.getTime())){
			return "Wrong time error";
		}*/
		if(note.getId()!=null && note.getId()!="")
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
		String name = "";
		if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
		String uid = userRepository.findByUserName(name).getId();
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

		}else{
			likes.add(name);
		}
		note.setLikes(likes);
		noteRepository.delete(note);
		noteRepository.save(note);
		if(likes.contains(name))	return "removed";
		return "added";
	}
}
