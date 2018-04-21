package com.fictionNote.model;

import com.fictionNote.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TimelineItem implements Comparable<TimelineItem>{
    String begin;
    String end;
    String content;
    String msg;

    public int compareTo(TimelineItem item){
        if(DateUtils.compare(DateUtils.stringToDate(begin, DateUtils.patternC),
                DateUtils.stringToDate(item.begin, DateUtils.patternC))>0)
            return 1;
        else if(DateUtils.compare(DateUtils.stringToDate(begin, DateUtils.patternC),
                DateUtils.stringToDate(item.begin, DateUtils.patternC))== 0){
            if(DateUtils.compare(DateUtils.stringToDate(end, DateUtils.patternC),
                    DateUtils.stringToDate(item.end, DateUtils.patternC))>0)
                return 1;
            else return -1;
        } else return -1;
    }
}
