package com.example.graduationproject.utils;
import com.example.graduationproject.models.DateTimeModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {

    public static boolean checkForConflict(DateTimeModel requestCourseDate,DateTimeModel otherRequestsDate) throws ParseException {
        try{
            String startDate1 = requestCourseDate.getStartDate();
            String endDate1 = requestCourseDate.getEndDate();
            String startTime1 = requestCourseDate.getStartTime();
            String endTime1 = requestCourseDate.getEndTime();
            String daysString1 = requestCourseDate.getDays();

            String startDate2 = otherRequestsDate.getStartDate();
            String endDate2 = otherRequestsDate.getEndDate();
            String startTime2 = otherRequestsDate.getStartTime();
            String endTime2 = otherRequestsDate.getEndTime();
            String daysString2 = otherRequestsDate.getDays();

            boolean conflict = isConflict(startDate1, startTime1, endDate1, endTime1, daysString1,
                    startDate2, startTime2, endDate2, endTime2, daysString2);

            if (conflict) {
                return true;
            } else {
                return false;
            }

        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Date parseDateTime(String date, String time) throws ParseException {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        return dateTimeFormat.parse(date + " " + time);
    }

    public static int getDayOfWeek(String day) {
        Map<String, Integer> dayOfWeekMap = new HashMap<>();
        dayOfWeekMap.put("Sun", Calendar.SUNDAY);
        dayOfWeekMap.put("Mon", Calendar.MONDAY);
        dayOfWeekMap.put("Tues", Calendar.TUESDAY);
        dayOfWeekMap.put("Wed", Calendar.WEDNESDAY);
        dayOfWeekMap.put("Thurs", Calendar.THURSDAY);
        dayOfWeekMap.put("Fri", Calendar.FRIDAY);
        dayOfWeekMap.put("Sat", Calendar.SATURDAY);
        return dayOfWeekMap.getOrDefault(day, -1);
    }

    public static Set<Integer> parseDays(String daysString) {
        Set<Integer> days = new HashSet<>();
        String[] daysArray = daysString.split(",");
        for (String day : daysArray) {
            int dayOfWeek = getDayOfWeek(day.trim());
            if (dayOfWeek != -1) {
                days.add(dayOfWeek);
            }
        }
        return days;
    }

    public static boolean haveCommonDays(Set<Integer> days1, Set<Integer> days2) {
        for (Integer day : days1) {
            if (days2.contains(day)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isConflict(String startDate1, String startTime1, String endDate1, String endTime1, String daysString1,
                                     String startDate2, String startTime2, String endDate2, String endTime2, String daysString2) throws ParseException {

        Date start1 = parseDateTime(startDate1, startTime1);
        Date end1 = parseDateTime(endDate1, endTime1);
        Date start2 = parseDateTime(startDate2, startTime2);
        Date end2 = parseDateTime(endDate2, endTime2);

        Set<Integer> days1 = parseDays(daysString1);
        Set<Integer> days2 = parseDays(daysString2);

        if (!haveCommonDays(days1, days2)) {
            return false;
        }

        if (start1.after(end2) || start2.after(end1)) {
            return false;
        }

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(start1);
        cal2.setTime(start2);

        while (!cal1.getTime().after(end1) && !cal2.getTime().after(end2)) {
            if (days1.contains(cal1.get(Calendar.DAY_OF_WEEK)) && days2.contains(cal2.get(Calendar.DAY_OF_WEEK))) {
                if ((cal1.getTime().before(cal2.getTime()) && cal2.getTime().before(end1)) || (cal2.getTime().before(cal1.getTime()) && cal1.getTime().before(end2))) {
                    return true;
                }
            }
            cal1.add(Calendar.DATE, 1);
            cal2.add(Calendar.DATE, 1);
        }

        return false;
    }
}
