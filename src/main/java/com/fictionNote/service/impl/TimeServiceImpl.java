package com.fictionNote.service.impl;

import com.fictionNote.model.BookList;
import com.fictionNote.model.Note;
import com.fictionNote.model.Time;
import com.fictionNote.model.TimelineItem;
import com.fictionNote.repository.*;
import com.fictionNote.service.TimeService;
import com.fictionNote.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TimeServiceImpl implements TimeService {
    private static final String DONE = "Done";
    private static final int MINS_OF_DAY = 24*60;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookListRepository bookListRepository;
    @Autowired
    private BookRepository bookRepository;

    public List<TimelineItem> putBookList(String uid){
        List<TimelineItem> items = new ArrayList<TimelineItem>();
        Iterator it = bookListRepository.findByUserId(uid).iterator();
        while(it.hasNext()){
            BookList bl = (BookList) it.next();
            TimelineItem timeline = new TimelineItem();
            timeline.setBegin(formatBeginTime(bl.getTime()));
            timeline.setContent("book list");
            timeline.setMsg(bookRepository.findById(bl.getBookId()).getTitle());
            items.add(timeline);
        }
        return items;
    }

    public List<TimelineItem> putNotes(String uid){
        List<TimelineItem> items = new ArrayList<TimelineItem>();
        Iterator it = noteRepository.findByUserId(uid).iterator();
        while(it.hasNext()){
            Note note = (Note) it.next();
            TimelineItem timeline = new TimelineItem();
            timeline.setBegin(formatBeginTime(note.getTime()));
            String book = bookRepository.findById(note.getBookId()).getTitle();
            timeline.setEnd(book);
            timeline.setContent("note");
            timeline.setMsg(note.getFromPage()+" "+note.getToPage());
            items.add(timeline);
        }
        return items;
    }

    public List<TimelineItem> putTimes(String uid){
        List<TimelineItem> items = new ArrayList<TimelineItem>();
        Iterator it = timeRepository.findByUserId(uid).iterator();
        while(it.hasNext()){
            Time time = (Time) it.next();
            TimelineItem timeline = new TimelineItem();
            timeline.setBegin(formatBeginTime(time.getBegin()));
            timeline.setEnd(time.getEnd());
            String book="";
            if(time.getBooks().length>0)
                book = bookRepository.findById(time.getBooks()[0]).getTitle();
            timeline.setContent("time "+(time.getType().equals(DONE)));
            timeline.setMsg(book);
            items.add(timeline);
        }
        return items;
    }

    public Map<String, Integer> recentTimes(String uid, int days){
        Map<String, Integer> map = new HashMap<String, Integer>();
        List<Time> times = timeRepository.findByUserIdAndType(uid, DONE);
        //System.out.println(times);
        Iterator it = times.iterator();
        while(it.hasNext()){
            Time time = (Time)it.next();
            if(!checkDays(time.getBegin().substring(0,10), days)) continue;
            List<String> dates = new ArrayList<String>();
            if(time.getBegin().substring(0, 10).equals(time.getEnd().substring(0,10)))
                map.put(time.getBegin().substring(0,10), Integer.valueOf(time.getDuring()));
            else{
                dates = getDuringDate(time.getBegin(), time.getEnd());
                try {
                    String tempDate = time.getBegin().substring(0,10);
                    Integer tempTime = map.get(tempDate);
                    if(map.containsKey(tempDate)){
                        map.remove(tempDate);
                        tempTime += MINS_OF_DAY - getSpentTimeOfADay(time.getBegin());
                    }else tempTime = MINS_OF_DAY - getSpentTimeOfADay(time.getBegin());
                    map.put(tempDate, tempTime);

                    tempDate = time.getEnd().substring(0,10);
                    tempTime = map.get(tempDate);
                    if(map.containsKey(tempDate)){
                        map.remove(tempDate);
                        tempTime += getSpentTimeOfADay(time.getEnd());
                    }else tempTime = getSpentTimeOfADay(time.getEnd());
                    map.put(tempDate, tempTime);

                }catch (Exception e){
                    e.printStackTrace();
                }
                for (int i=1; i<dates.size()-1; i++){
                    map.put(dates.get(i), MINS_OF_DAY);
                }
            }
        }
        return sortMap(map);
    }

    public String formatBeginTime(String begin){
        if(begin.length() != 19)
            return begin+":00";
        return begin;
    }

    public List<String> getDuringDate(String begin, String end){
        List<String> days = new ArrayList<String>();
        Date b = DateUtils.stringToDate(begin, DateUtils.patternC);
        Date e = DateUtils.stringToDate(end, DateUtils.patternC);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(b);
        int dayB = calendar.get(Calendar.DATE);
        calendar.setTime(e);
        int dayE = calendar.get(Calendar.DATE);
        for(int i = 0; i<=dayE-dayB; i++){
            calendar.set(Calendar.DATE, dayB+i);
            days.add(DateUtils.dateToString(calendar.getTime(), DateUtils.patternA));
        }
        return days;
    }

    public int getSpentTimeOfADay(String to)throws Exception{
        return DateUtils.countDuring(to.substring(0, 10)+" 00:00:00" , to);
    }

    public boolean checkDays(String day, int d){
        Date b = DateUtils.stringToDate(day, DateUtils.patternA);
        Date now = new Date();
        long dayBetween = (now.getTime() - b.getTime()+1000000)/(3600*24*1000);
        System.out.println(dayBetween);
        if(dayBetween<d)    return true;
        else
        return false;
    }

    public Map<String, Double> recentPages(String uid, int days){
        Map<String, Double> map = new HashMap<String, Double>();
        List<Time> times = timeRepository.findByUserIdAndType(uid, DONE);
        Iterator it = times.iterator();
        while(it.hasNext()) {
            Time time = (Time) it.next();
            if (!checkDays(time.getBegin().substring(0, 10), days)) continue;
            if(time.getBegin().substring(0, 10).equals(time.getEnd().substring(0,10))) {
                double pages = time.getToPage() - time.getFromPage() + 1;
                if(time.getFromPage() == 0 || time.getToPage() == 0)
                    pages = bookRepository.findById(time.getBooks()[0]).getTotalPage();
                map.put(time.getBegin().substring(0, 10), pages);
            }else{
                Iterator<String> dates = getDuringDate(time.getBegin(), time.getEnd()).iterator();
                int n = getDuringDate(time.getBegin(), time.getEnd()).size();
                while (dates.hasNext()){
                    String date = dates.next();
                    map.put(date, (time.getToPage()-time.getToPage())*1.0/n);
                }
            }
        }
        return sortMap(map);
    }

    public Map sortMap(Map map){
        Map<String, Object> newMap = new TreeMap<String, Object>(
                new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        if (DateUtils.stringToDate(o1, DateUtils.patternA).before(DateUtils.stringToDate(o2, DateUtils.patternA)))
                            return -1;
                        else return 1;
                    }
                }

        );
        Set<Map.Entry<String, Object>> set = map.entrySet();
        Iterator<Map.Entry<String, Object>> itMap = set.iterator();
        while (itMap.hasNext()){
            Map.Entry<String, Object> next = itMap.next();
            newMap.put(next.getKey(), next.getValue());
        }

        return newMap;
    }
}
