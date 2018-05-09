package com.fictionNote.controller;

import com.fictionNote.model.*;
import com.fictionNote.repository.*;
import com.fictionNote.service.TimeService;
import com.fictionNote.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class RecordTimeController {
    private static final String ALL_ITEMS = "0";
    private static final String TIME_ITEMS = "1";
    private static final String NOTE_ITEMS = "2";
    private static final String READING_AND_NOTE_ITEMS = "3";

    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookController bookController;
    @Autowired
    private TimeService timeService;

    @RequestMapping(value="/getTodo",method={ RequestMethod.GET})
    @ResponseBody
    public List<Time> usrInfo(HttpServletRequest request) {
        String name = request.getCookies()[0].getValue();
        List<Time> times = timeRepository.findByUserIdAndType(userRepository.findByUserName(name).getId(), "To do");
        return times;
    }

    @RequestMapping(value="/checkDone",method={ RequestMethod.POST})
    @ResponseBody
    public String updataMsg(@RequestParam(value="id", required=true) String id,
                            @RequestParam(value="msg", required=false) String msg) throws Exception{
        Time time = timeRepository.findById(id);
        timeRepository.delete(time);
        if(msg.equals("100")) {
            time.setType("Done");
            time.setMsg("");
            time.setFromPage(1);
            time.setToPage(bookController.get(time.getBooks()[0]).getTotalPage());
            timeRepository.save(time);
            return "done";
        }else{
            time.setMsg(msg);
        }timeRepository.save(time);
        return "success";

    }

    @RequestMapping(value = "/addRecordTime", method = { RequestMethod.POST})
    @ResponseBody
    public String addTime(@RequestBody Time time) throws Exception{
        if(time.getBegin()=="" || time.getEnd()=="") return "Failed! null time";
        time.setBegin(DateUtils.formateRecordTime(time.getBegin()));
        time.setEnd(DateUtils.formateRecordTime(time.getEnd()));
        if(time.getType() == "To do"){
            time.setFromPage(0);
            time.setToPage(0);
        }else{
            if(time.getFromPage() == 0 && time.getToPage() == 0) time.setFromPage(1);
            if(time.getToPage() == 0) time.setToPage(bookController.get(time.getBooks()[0]).getTotalPage());
        }
        int during = DateUtils.countDuring(time.getBegin(), time.getEnd());
        if(during<0)    return "Failed! end time before start time";
        time.setDuring(Integer.toString(during));

        if(time.getId()!="" && timeRepository.findById(time.getId())!=null){
            timeRepository.delete(timeRepository.findById(time.getId()));
        }
        timeRepository.save(time);
        return "success";
    }

    @RequestMapping(value = "/getTime", method = { RequestMethod.GET})
    @ResponseBody
    public List<Time> getTimes(HttpServletRequest request) {
        String name = "";
        if(request.getCookies()[0] != null)
            name = request.getCookies()[0].getValue();
        String uid = userRepository.findByUserName(name).getId();
        return timeRepository.findByUserId(uid);
    }


    @RequestMapping(value = "/timeline", method = { RequestMethod.GET})
    @ResponseBody
    public List<TimelineItem> timeline(@RequestParam(value="type", required=true) String type,
                                       @RequestParam(value="sort", required=true) String sort,HttpServletRequest request) {
        String name = "";
        if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
        String uid = userRepository.findByUserName(name).getId();
        List<TimelineItem> items = new ArrayList<TimelineItem>();

        if(type.equals(ALL_ITEMS)){
            items.addAll(timeService.putBookList(uid));
            items.addAll(timeService.putNotes(uid));
            items.addAll(timeService.putTimes(uid));
        }else if(type.equals(TIME_ITEMS)){
            items.addAll(timeService.putTimes(uid));
        }else if (type.equals(NOTE_ITEMS)) {
            items.addAll(timeService.putNotes(uid));
        }else if(type.equals(READING_AND_NOTE_ITEMS)){
            items.addAll(timeService.putNotes(uid));
            items.addAll(timeService.putTimes(uid));
        }
        //System.out.println(items);
        Collections.sort(items);
        if(sort.equals("0")) Collections.reverse(items);
        return items;
    }

    @RequestMapping(value = "/delTime", method = { RequestMethod.POST})
    @ResponseBody
    public String delTime(@RequestBody List<Time> times) {
        Iterator iterator = times.iterator();
        List<Time> delTimeList = new ArrayList<Time>();
        while(iterator.hasNext()){
            Time time = (Time)iterator.next();
            time = timeRepository.findById(time.getId());
            if(time == null) return "error";
            delTimeList.add(time);
        }
        timeRepository.delete(delTimeList);
        return "success";
    }

    @RequestMapping(value = "/recentReading", method = { RequestMethod.GET})
    @ResponseBody
    public String recentReading(@RequestParam(value="days", required=true) String days, HttpServletRequest request) {
        String name = "";
        if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
        String uid = userRepository.findByUserName(name).getId();
        Map map = timeService.recentTimes(uid, Integer.valueOf(days));
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }

    @RequestMapping(value = "/recentPage", method = { RequestMethod.GET})
    @ResponseBody
    public String recentPage(@RequestParam(value="days", required=true) String days, HttpServletRequest request) {
        String name = "";
        if(request.getCookies()[0] != null) name = request.getCookies()[0].getValue();
        String uid = userRepository.findByUserName(name).getId();
        Map map = timeService.recentPages(uid, Integer.valueOf(days));
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }
}
