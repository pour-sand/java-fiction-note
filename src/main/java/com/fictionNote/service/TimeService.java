package com.fictionNote.service;

import com.fictionNote.model.TimelineItem;

import java.util.List;
import java.util.Map;

public interface TimeService {
    public List<TimelineItem> putBookList();
    public List<TimelineItem> putNotes();
    public List<TimelineItem> putTimes();
    public Map<String, Integer> recentTimes(String uid, int days);
    public List<String> getDuringDate(String begin, String end);
}
