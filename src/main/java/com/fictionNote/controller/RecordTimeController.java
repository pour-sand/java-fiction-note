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
    @Autowired
    private BookController bookController;

    @RequestMapping(value = "/addRecordTime", method = { RequestMethod.POST})
    @ResponseBody
    public String addTime(@RequestBody Time time) throws Exception{
        if(time.getBegin()=="" || time.getEnd()=="") return "Failed! null time";
        time.setBegin(DateUtils.formateRecordTime(time.getBegin()));
        time.setEnd(DateUtils.formateRecordTime(time.getEnd()));
        if(time.getType() == "To do"){
            time.setFromPage("");
            time.setToPage("");
        }else{
            if(time.getFromPage() == "0" && time.getToPage() == "0") time.setFromPage("1");
            if(time.getToPage() == "0") time.setToPage(bookController.get(time.getBooks()[0]).getTotalPage());
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
