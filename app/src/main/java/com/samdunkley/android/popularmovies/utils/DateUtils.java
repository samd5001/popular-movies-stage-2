package com.samdunkley.android.popularmovies.utils;

import android.content.Context;
import android.widget.Toast;

import com.samdunkley.android.popularmovies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    public static String getYearFromDateString(String date, Context context) {

        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateParser.parse(date));
            return String.valueOf(calendar.get(Calendar.YEAR));

        } catch (ParseException e) {
            Toast.makeText(context, R.string.detail_parse_date_error, Toast.LENGTH_SHORT).show();
            return date;
        }
    }
}
