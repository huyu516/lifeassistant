package com.hy.lifeassistant.page;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hy.lifeassistant.R;
import com.hy.lifeassistant.base.CommonUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.StateBuilder;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

// 女性日历
public class Menstruation extends Fragment implements OnDateSelectedListener {

    private static final String STORE_KEY = "menstruation";

    public static final int PERIOD = 29;

    private MaterialCalendarView calendarView;

    private TextView tvDays;

    private Calendar start;

    private Calendar end;

    private boolean canChoseMenstrt = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        start = getFirstDayOfprevMonth();
        end = getLastDayOfNextMonth();

        View view = inflater.inflate(R.layout.f_menustruation, container, false);

        calendarView = (MaterialCalendarView) view.findViewById(R.id.cpvMenstruation);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        calendarView.setOnDateChangedListener(this);

        StateBuilder builder = calendarView.state().edit();
        builder.setMinimumDate(CalendarDay.from(start));
        builder.setMaximumDate(CalendarDay.from(end));
        builder.setCalendarDisplayMode(CalendarMode.MONTHS);
        builder.commit();

        tvDays = (TextView) view.findViewById(R.id.tvDays);

        tryToshowMenstrts();

        return view;
    }

    private Calendar getFirstDayOfprevMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }

    private Calendar getLastDayOfNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        return calendar;
    }

    @Override
    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
        calendarView.setDateSelected(date.getDate(), false);

        if (canChoseMenstrt) {
            canChoseMenstrt = false;

            tryToSetMenstrt(date.getDate());
        }
    }

    private void tryToSetMenstrt(final Date date) {
        final MaterialDialog dialog = new MaterialDialog(getContext());
        dialog.setTitle("确认");
        dialog.setMessage(CommonUtils.formatDate(date) + " 是否为一个例假日期?");
        dialog.setPositiveButton("是", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setMenstrt(date);

                tryToshowMenstrts();

                dialog.dismiss();

                canChoseMenstrt = true;
            }

        });
        dialog.setNegativeButton("否", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

                canChoseMenstrt = true;
            }

        });
        dialog.show();
    }

    // 在日历里标注start和end之间的所有例假日
    private void tryToshowMenstrts() {
        calendarView.clearSelection();

        List<Date> mensList = getAllMenstrts();

        if (CommonUtils.isEmpty(mensList)) {
            tvDays.setText("请设置一个例假");
            Toast.makeText(getContext(), "请选择一个例假日, 双击日期即可", Toast.LENGTH_LONG).show();
            return;
        }

        Date firstMens = getFirstMenstrtFromDay(new Date());
        // 从现在开始距离第一个例假的日期
        int days = Math.abs(CommonUtils.daysBetween(new Date(), firstMens));
        // 距离10天以内字体使用红色,否则使用蓝色
        int color = getResources().getColor(days <= 10 ? R.color.red : R.color.blue);

        tvDays.setTextColor(color);
        tvDays.setText(String.valueOf(days));

        for (Date mens : mensList) {
            calendarView.setDateSelected(mens, true);
        }
    }

    // 获得start和end之间的所有例假
    private List<Date> getAllMenstrts() {
        Date date = getFirstMenstrtFromDay(start.getTime());

        if (date == null) {
            return null;
        }

        List<Date> result = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        while (calendar.before(end)) {
            result.add(calendar.getTime());

            calendar.add(Calendar.DATE, PERIOD);
        }

        return result;
    }

    // 如果该日期是例假,则返回该日期,否则返回此日期后的第一个例假
    private Date getFirstMenstrtFromDay(Date start) {
        Date menustrt = getMenstrt();

        if (menustrt == null) {
            return null;
        }

        int days = CommonUtils.daysBetween(menustrt, start);

        // 如果start是例假,则返回
        if (days % 29 == 0) {
            return start;
        }

        // 寻找start后的第一个例假日期
        int diff = (29 - (days % 29)) % 29;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, diff);

        return calendar.getTime();
    }

    // 获取一个例假日期
    private Date getMenstrt() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        long ldate = preferences.getLong(STORE_KEY, -1);
        return ldate == -1 ? null : new Date(ldate);
    }

    // 设置一个例假日期
    private void setMenstrt(Date date) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Editor editor = preferences.edit();
        editor.putLong(STORE_KEY, date.getTime());
        editor.commit();
    }

}
