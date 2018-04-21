package com.fictionNote.repository;

import com.fictionNote.model.Time;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TimeRepository extends MongoRepository<Time, String> {
    List<Time> findByUserId(String uid);
    List<Time> findByUserIdAndType(String uid, String type);
    Time findById(String id);
}

