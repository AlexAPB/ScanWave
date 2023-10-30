package com.fatec.rfidscanwave.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class StringUtil {

    public static boolean isNumeric(String str){
        boolean isNumeric = true;

        for(char c : str.toCharArray()){
            if(!Character.isDigit(c)) {
                isNumeric = false;
                break;
            }
        }

        return isNumeric;
    }

    public static String getTime(LocalTime dateTime){
        StringBuffer str = new StringBuffer();

        if(dateTime.getHour() < 10)
            str.append(0);

        str.append(dateTime.getHour());

        str.append(":");

        if(dateTime.getMinute() < 10)
            str.append(0);

        str.append(dateTime.getMinute());

        return str.toString();
    }
}
