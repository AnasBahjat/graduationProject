package com.example.graduationproject.utils;

import android.util.Log;

import com.example.graduationproject.models.DateTimeModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static boolean isConflict(DateTimeModel currentRequest,DateTimeModel modelToCheckWith){
        String startDate1 = currentRequest.getStartDate();
        String endDate1 = currentRequest.getEndDate();
        String startTime1 = currentRequest.getStartTime();
        String endTime1 = currentRequest.getEndTime();
        String days1 = currentRequest.getDays();

        String startDate2 = modelToCheckWith.getStartDate();
        String endDate2 = modelToCheckWith.getEndDate();
        String startTime2 = modelToCheckWith.getStartTime();
        String endTime2 = modelToCheckWith.getEndTime();
        String days2 =  modelToCheckWith.getDays();


        Log.d("start date 1 "+startDate1,"start date 1 "+startDate1);
        Log.d("end date 1 "+endDate1,"end date 1 "+endDate1);
        Log.d("start time 1 "+startTime1,"start time 1 "+startTime1);
        Log.d("end time 1 "+endTime1,"end time 1 "+endTime1);
        Log.d("days 1 "+days1,"days 1 "+days1);
        Log.d("-------------------------------","---------------------------");

        Log.d("start date 2 "+startDate2,"start date2 "+startDate2);
        Log.d("end date 2 "+endDate2,"end date 2 "+endDate2);
        Log.d("start date 2 "+startTime2,"start date 2 "+startTime2);
        Log.d("end date 2 "+endTime2,"end date 2 "+endTime1);
        Log.d("end 2 "+days2,"start date 2 "+days2);




        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        try {
            Date start1 = dateFormat.parse(startDate1);
            Date end1 = dateFormat.parse(endDate1);
            Date start2 = dateFormat.parse(startDate2);
            Date end2 = dateFormat.parse(endDate2);

            Date timeStart1 = timeFormat.parse(startTime1);
            Date timeEnd1 = timeFormat.parse(endTime1);
            Date timeStart2 = timeFormat.parse(startTime2);
            Date timeEnd2 = timeFormat.parse(endTime2);

            boolean dateOverlap = !(end1.before(start2) || start1.after(end2));

            boolean timeOverlap = !(timeEnd1.before(timeStart2) || timeStart1.after(timeEnd2));

            String[] daysArray1 = days1.split(", ");
            String[] daysArray2 = days2.split(", ");
            boolean dayOverlap = false;
            for (String day : daysArray1) {
                if (days2.contains(day)) {
                    dayOverlap = true;
                    break;
                }
            }

            boolean conflict = dateOverlap && timeOverlap && dayOverlap;
            return conflict;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
