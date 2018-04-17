package com.fictionNote.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fictionNote.model.Note;

public interface NoteRepository extends MongoRepository<Note, String>{
	List<Note> findByUserId(String uid);
	Note findById(String id);
	List<Note> findByBookId(String bid);
}
