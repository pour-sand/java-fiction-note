package com.fictionNote.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fictionNote.model.Note;
import com.fictionNote.repository.NoteRepository;
import com.fictionNote.repository.UserRepository;
import com.fictionNote.service.NoteService;

@Service
public class NoteServiceImpl implements NoteService {
	@Autowired
	NoteRepository noteRepository;
	@Autowired
	UserRepository userRepository;
	
	public void addNote(Note note){
		noteRepository.save(note);
	}
	
	public List<Note> oneBookNotes(String bid) {
		List<Note> notes = noteRepository.findByBookIdAndVisible(bid, true);
		Iterator<Note> it = notes.iterator();
		while (it.hasNext()) {
			Note note = it.next();
			note.setUserId(userRepository.findById(note.getUserId()).getUserName());
		}
		return notes;
	}
}
