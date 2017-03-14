package edu.fpt.prm.com.mediamanagement;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HuongLX on 3/14/2017.
 */

public class DateTimeUtils {
    public static String convertToDate(long l){
        SimpleDateFormat format =new SimpleDateFormat("E, dd/MMM/yyyy");
        return format.format(new Date(l*1000));
    }
}
