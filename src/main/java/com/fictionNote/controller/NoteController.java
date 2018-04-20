package com.fictionNote.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@RequestMapping(value = "/userAllNotes", method = { RequestMethod.POST})
	@ResponseBody
	public List<Note> getNotes(HttpServletRequest request) {
		return noteRepository.findByUserId(request.getParameter("uid"));
	}
	
	@RequestMapping(value = "/oneBookNotes", method = { RequestMethod.POST})
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
	
}
