package com.fictionNote.controller;

import com.fictionNote.model.Time;
import com.fictionNote.model.TimelineItem;
import com.fictionNote.repository.TimeRepository;
import com.fictionNote.repository.UserRepository;
import com.fictionNote.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class RecordTimeController {
    private static final String ALL_ITEMS = "0";
    private static final String READING_ITEMS = "1";
    private static final String NOTE_ITEMS = "2";
    private static final String READING_AND_NOTE_ITEMS = "3";
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookController bookController;

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
            time.setFromPage("1");
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

    @RequestMapping(value = "/timeline", method = { RequestMethod.POST})
    @ResponseBody
    public List<TimelineItem> delTime(@RequestParam(value="type", required=true) String type) {
        List<TimelineItem> items = new ArrayList<TimelineItem>();
        Collections.sort(items);
        return items;
    }

    @RequestMapping(value = "/delTime", method = { RequestMethod.POST})
    @ResponseBody
    public String timeline(@RequestBody List<Time> times) {
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
