package com.fictionNote.service;

import java.util.List;

import com.fictionNote.model.Note;

public interface NoteService {
	public void addNote(Note note);
	public List<Note> oneBookNotes(String bid);
}
