package com.fictionNote.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
	private static final long ONE_DAY = 24*60*60*1000L;
    public static final String patternA = "yyyy-MM-dd";  
    public static final String patternB = "yyyy-MM-dd HH:mm";
	public static final String patternC = "yyyy-MM-dd HH:mm:ss";
    private static final String[] sevenDays = new String[] {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    public static Integer dayToInteger(String s) {
		if(s.equals("Mon")) return 1;
		else if(s.equals("Tue")) return 2;
		else if(s.equals("Wed")) return 3;
		else if(s.equals("Thu")) return 4;
		else if(s.equals("Fri")) return 5;
		else if(s.equals("Sat")) return 6;
		else if(s.equals("Sun")) return 7;
		else return -1;
	}
	public boolean isLatest(Date check,Date now,int days){
		Calendar calendar = Calendar.getInstance();  //得到日历
		calendar.setTime(now);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -days);  //设置为7天前
		Date before7days = calendar.getTime();   //得到7天前的时间
		if(before7days.getTime() < check.getTime()){
			return true;
		}else{
			return false;
		}
	}
	public static String formateRecordTime(String time){
    	return time.replace("T", " ").substring(0, 19);
	}
	public static int countDuring(String t1, String t2) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat(patternC);
		Date d1 = null;
		Date d2 = null;

		try {
			d1 = sdf.parse(t1);
			d2 = sdf.parse(t2);
		} catch (ParseException pe){
			System.out.println(pe.getMessage());
		}
		long dd1 = d1.getTime();
		long dd2 = d2.getTime();
		double hours = (double)(dd2-dd1)/3600/1000;
		System.out.println("时间差是："+hours+"（小时）");
		return (int) ((dd2 - dd1)/(1000 * 60));
	}
    public static String formateNoteTime(Date date){
    	return dateToString(date, patternB);
    }
    public static String formateNoteTime(String s){
    	return s.replace("T", " ").substring(0, 19);
    }
    public static boolean compareNoteTime(String t1, String t2){
    	Date d1 = stringToDate(formateNoteTime(t1), patternB);
    	Date d2 = stringToDate(formateNoteTime(t2), patternB);
    	return d1.before(d2) || d1.equals(d2);
    }
    public static String formateDate(Date date){  
        return dateToString(date, patternA);
    }  
    
    public static String getWeekOfDate(Date date) {	
		Calendar calendar = Calendar.getInstance();
		if(date != null) calendar.setTime(date);		
		int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)	w = 0;
		return sevenDays[w];
	}
    
    public static String getWeekByDate(String t, int w, String fs, int f) {
		Date time = DateUtils.stringToDate(t, fs);
		if(w < 7 && w >= 1) w += 1;
		else if(w == 7) w = 1;
		else return "Please input right week!";
        SimpleDateFormat sdf = new SimpleDateFormat(fs); 
        Calendar cal = Calendar.getInstance();  
        cal.setTime(time);
        if( f==1 ) {
        	//cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
        	cal.roll(Calendar.DATE, 1);
        }
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if(w >= day) {
        	cal.add(Calendar.DATE, w - day); 
        	return sdf.format(cal.getTime());
        }else {
        	cal.add(Calendar.DATE, 7 + w - day);
        	return sdf.format(cal.getTime());
        }
    }
    
    public static String dateToString(Date date, String pattern) {  
        if (date == null) {  
            return "";  
        } else {  
            SimpleDateFormat format = new SimpleDateFormat(pattern);  
            return format.format(date);  
        }  
    }  
    
    public static Date stringToDate(String s, String fs) {  
        DateFormat format = new SimpleDateFormat(fs);  
        Date date = new Date();  
        try {  
            date = format.parse(s);   
        } catch (ParseException e) {
            logger.error("========"+e);
        }
        return date;
    } 
    
    public static Date getDate() {  
        return Calendar.getInstance().getTime();  
    }
    
    public static int compare(Date date1, Date date2) {
		return date1.compareTo(date2);
	}
    
    public static Date parseToDate(String stringDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		Date parsedDate = sdf.parse(stringDate);
		return parsedDate;
	}
    
    public static boolean judgeDateOverlapped(Date d1_start,Date d1_end,Date d2_start,Date d2_end) {
		int judge_1=compare(d1_start, d2_start);
		int judge_2=compare(d1_end, d2_end);
		int judge_3=compare(d1_end, d2_start);
		int judge_4=compare(d1_start,d2_end);
		if((judge_1==-1 && judge_2==-1 && judge_3==1)||(judge_1==1 && judge_2==1 && judge_4==-1)
				||(judge_1==1 && judge_2==-1)||(judge_1==-1 && judge_2==1)||(judge_1==0 && judge_2==0))
			return true;
		else
			return false;
	}
    public static List<Date> CalStartAndEndTime(String start,String classHours,String[] days) throws ParseException {
		List<Date> startAndEnd = new ArrayList<Date>();
		int flag1=0;
		int[] flag2=new int[days.length];
		String startDayOfWeek = getWeekOfDate(DateUtils.parseToDate(start));		
		Date endDate = new Date();
		Date startDate = new Date();
		for(int i=0;i<sevenDays.length;i++) {
			if(startDayOfWeek.equals(sevenDays[i])) {
				flag1=i;	
				break;
			}				
		}
		for(int m=0;m<days.length;m++) {
			for(int n=0;n<sevenDays.length;n++) {
				if(days[m].substring(0, 3).equals(sevenDays[n])) {
					flag2[m]=n;
				}	
			}
		}
		if(flag1<=flag2[0]) {//test
			startDate = new Date((DateUtils.parseToDate(start).getTime()+(long)(flag2[0]-flag1)*ONE_DAY));
			endDate =new Date(startDate.getTime()+(long)(getWeeks(classHours,days)-1)*7*ONE_DAY+(long)((flag2[flag2.length-1]-flag2[0])*ONE_DAY));	
			startAndEnd.add(startDate);
			startAndEnd.add(endDate);
		}
		else if(flag1>= flag2[flag2.length-1]) {//pass
			startDate = new Date((DateUtils.parseToDate(start).getTime()+(long)(7-flag1+flag2[flag2.length-1])*ONE_DAY));
			endDate =new Date(startDate.getTime()+(long)(getWeeks(classHours,days)+flag2[flag2.length-1]-flag2[0]-1)*7*ONE_DAY);
			startAndEnd.add(startDate);
			startAndEnd.add(endDate);
		}else {
			for(int i=0;i<flag2.length;i++) {
				if(flag1>flag2[i]&&flag1<=flag2[i+1]) {
					startDate = new Date((parseToDate(start).getTime()+(long)(flag2[i+1]-flag1)*ONE_DAY));
					endDate=new Date(startDate.getTime()+(long)((Integer.valueOf(classHours)-(flag2.length-1-i))/days.length)*7*ONE_DAY);			
					startAndEnd.add(startDate);
					startAndEnd.add(endDate);
					break;
				}
			}
		}
		return startAndEnd;
	}
    
    public static int getWeeks(String classHours,String[] days) {
    	return Integer.valueOf(classHours)/days.length;
    }
    
	public static boolean checkOverlap(List<String> list) throws ParseException{  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Collections.sort(list);    
        boolean flag = false; 
        for(int i=0; i<list.size(); i++){  
            if(i>0){  
                String[] itime = list.get(i).split("-");  
                for(int j=0; j<list.size(); j++){   
                    if(j==i || j>i){  
                        continue;  
                    }                       
                    String[] jtime = list.get(j).split("-");  
                    Date date1=sdf.parse("2018-01-08 "+itime[0]+":00");
                    Date date2=sdf.parse("2018-01-08 "+jtime[1]+":00");
                    int compare = DateUtils.compare(date1,date2);  
                    if(compare<0){  
                        flag = true;            
                        break;
                    }  
                }  
            } 
            if(flag){  
                break;  
            }  
        }          
        return flag;  
    }  
}