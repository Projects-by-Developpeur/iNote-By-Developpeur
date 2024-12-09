package com.example.inote;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class GenerateTimeStamp {

    /**
     * Generates a timestamp string representing the current time in a specific format and time zone.
     * The format used is "HH:mm | dd/MM/yyyy". The time zone is set to "Africa/Algiers".
     *
     * @return A formatted timestamp string.
     */
    public static String generateTimestamp() {
        // Create a SimpleDateFormat object with the desired date/time format
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss | dd/MM/yyyy", Locale.getDefault());
        // Set the time zone to the desired time zone (adjust as needed)
        TimeZone timeZone = TimeZone.getTimeZone("Africa/Algiers");
        sdf.setTimeZone(timeZone);

        return sdf.format(Calendar.getInstance().getTime());
    }
}
