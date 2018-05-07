package com.fictionNote.service;

import com.fictionNote.model.TimelineItem;

import java.util.List;
import java.util.Map;

public interface TimeService {
    public List<TimelineItem> putBookList(String uid);
    public List<TimelineItem> putNotes(String uid);
    public List<TimelineItem> putTimes(String uid);
    public Map<String, Integer> recentTimes(String uid, int days);
    public Map<String, Double> recentPages(String uid, int days);
    public List<String> getDuringDate(String begin, String end);
}
