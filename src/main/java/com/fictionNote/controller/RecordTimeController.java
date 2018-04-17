package com.fictionNote.controller;

import com.fictionNote.model.Time;
import com.fictionNote.repository.TimeRepository;
import com.fictionNote.repository.UserRepository;
import com.fictionNote.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
public class RecordTimeController {
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/addRecordTime", method = { RequestMethod.POST})
    @ResponseBody
    public String addNote(@RequestBody Time time) {
        System.out.println(time);
        time.setBegin(DateUtils.formateRecordTime(time.getBegin()));
        time.setEnd(DateUtils.formateRecordTime(time.getEnd()));
        /*time.setBegin(DateUtils.formateNoteTime(new Date()));
        if(!DateUtils.compareNoteTime(time.getBookId(), time.getEnd()))
                || !DateUtils.compareNoteTime(time.getTimee(), time.getTime())){
            return "Wrong time error";
        }
        if(time.getId()!=null && time.getId()!="")
            timeRepository.delete(timeRepository.findById(time.getId()));
        time.setTimeb(DateUtils.formateNoteTime(time.getTimeb()));
        time.setTimee(DateUtils.formateNoteTime(time.getTimee()));*/
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
}
