package com.simcoder.novalauncherclone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.appota.lunarcore.LunarCoreHelper;
import com.simcoder.novalauncherclone.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalenderFragment#'newInstance'} factory method to
 * create an instance of this fragment.
 */
public class CalenderFragment extends Fragment {


    CalendarView calendar;
    TextView sunDate;
    TextView moonDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        calendar = view.findViewById(R.id.calendar);
        sunDate = view.findViewById(R.id.sunDate);
        moonDate = view.findViewById(R.id.moonDate);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date date = new Date();
        int day, month, year;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            day = localDate.getDayOfMonth();
            month = localDate.getMonthValue();
            year = localDate.getYear();
        } else {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            cal.setTime(date);
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH) + 1;
            year = cal.get(Calendar.YEAR);
        }

        sunDate.setText(formatter.format(date));
        int[] lunarDay = LunarCoreHelper.convertSolar2Lunar(day, month, year, 7);

        String lunarDate = "(Âm lịch: " + lunarDay[0] + "/" + lunarDay[1] + "/" + lunarDay[2] + ")";
        moonDate.setText(lunarDate);
        return view;
    }
}