package com.pp.toptal.soccermanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeFormatter {
    
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    
    private DateTimeFormatter() {
    }
    
    public final static String toDateString(Date date) {
        return (date != null) ? DATE_FORMATTER.get().format(date) : null;
    }
    
    public final static String toDateTimeString(Date date) {
        return (date != null) ? DATE_TIME_FORMATTER.get().format(date) : null;
    }
    
    /**
     * @param date  either in form of "yyyy-MM-dd" or "yyyy-MM-dd HH:mm:ss"
     * @return  date or {@code null} if incorrect format
     */
    public final static Date fromString(String source) {
        try {
            return (source != null) ?
                   (source.length() == 10) ? DATE_FORMATTER.get().parse(source)
                                           : DATE_TIME_FORMATTER.get().parse(source)
                                    : null; 
        } catch (ParseException e) {
            return null;
        }
    }
    
}
