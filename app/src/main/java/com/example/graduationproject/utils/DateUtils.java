package com.example.graduationproject.utils;

import android.util.Log;

import com.example.graduationproject.models.DateTimeModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    public static long calculateDurationInDays(String startDateStr, String endDateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            return diffInDays;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean isDateTimeExpired(String startDate, String startTime, String endDate, String endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        try {
            Date startDateParsed = dateFormat.parse(startDate);
            Date endDateParsed = dateFormat.parse(endDate);
            Date startTimeParsed = timeFormat.parse(startTime);
            Date endTimeParsed = timeFormat.parse(endTime);

            String startDateTimeString = dateFormat.format(startDateParsed) + " " + timeFormat.format(startTimeParsed);
            String endDateTimeString = dateFormat.format(endDateParsed) + " " + timeFormat.format(endTimeParsed);

            Date startDateTime = dateTimeFormat.parse(startDateTimeString);
            Date endDateTime = dateTimeFormat.parse(endDateTimeString);

            Date currentDateTime = new Date();

            return currentDateTime.after(endDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
