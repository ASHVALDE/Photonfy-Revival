package org.example;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {
    public static int countChar(String str, char c)
    {
        int count = 0;

        for(int i=0; i < str.length(); i++)
        {    if(str.charAt(i) == c)
            count++;
        }

        return count;
    }


    public static boolean isThisStringaNumber(String str){
        if(countChar(str,'.')>1){
            return false;
        }
        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) {
                if (c != '-') {
                    if (c != '.') {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    final static String DATE_FORMAT = "dd-MM-yyyy";

    public static boolean isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
