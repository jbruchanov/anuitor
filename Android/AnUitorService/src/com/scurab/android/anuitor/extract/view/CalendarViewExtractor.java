package com.scurab.android.anuitor.extract.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.CalendarView;

import com.scurab.android.anuitor.extract.Translator;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by jbruchanov on 04/07/2014.
 */
@TargetApi(value = Build.VERSION_CODES.HONEYCOMB)
public class CalendarViewExtractor extends ViewGroupExtractor {

    public CalendarViewExtractor(Translator translator) {
        super(translator);
    }

    @Override
    public HashMap<String, Object> fillValues(View v, HashMap<String, Object> data, HashMap<String, Object> parentData) {
        super.fillValues(v, data, parentData);

        CalendarView cv = (CalendarView) v;

        data.put("Date", cv.getDate());
        data.put("DateLocaleString", new Date(cv.getDate()).toLocaleString());
        data.put("MinDate", cv.getMinDate());
        data.put("MinDateLocaleString", new Date(cv.getMinDate()).toLocaleString());
        data.put("MaxDate", cv.getMaxDate());
        data.put("MaxDateLocaleString", new Date(cv.getMaxDate()).toLocaleString());
        data.put("FirstDayOfWeek", cv.getFirstDayOfWeek());
        data.put("ShowWeekNumber", cv.getShowWeekNumber());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            data.put("FocusedMonthDateColor", getStringColor(cv.getFocusedMonthDateColor()));
            data.put("ShownWeekCount", cv.getShownWeekCount());
        }
        return data;
    }
}
