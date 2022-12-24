package com.linkedin.oauth.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DateUtil {

    private static final String DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";

    public static Date convertDateToWelkinFormat(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        Date date = new Date(time );
        String date1 = formatter.format(date);
        try {
            formatter.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String convertDateToWelkinFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        return Objects.nonNull(date) ? formatter.format(date) : null;
    }

    public static boolean convertStringToBoolean(String flag){
        return Boolean.parseBoolean(flag);
    }
}
