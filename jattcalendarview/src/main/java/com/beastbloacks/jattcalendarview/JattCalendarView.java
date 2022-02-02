package com.beastbloacks.jattcalendarview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class JattCalendarView extends LinearLayout {
    public static Activity activity;
    public static TextView monthyear;
    public static RecyclerView Dates;
    public static int currentmonth = 0;
    public static int currentyear = 0;
    public static int currentday = 0;
    public static int SundayBackgroundColor = R.color.white;
    public static int SundayTextColor = R.color.grey;
    public static int CurrentBackgroundColor = R.color.blue_100;
    public static int CurrentTextColor = R.color.blue_800;
    public static ImageView increase, decrease;
    public static List<JattCalendarModel> data = new ArrayList<>();
    public static List<JattCalendarModel> nonselecteddates = new ArrayList<>();
    public static List<JattCalendarModel> sundaydates = new ArrayList<>();
    public static onCalendarLeftRight cc;
    public static JattCalendarViewAdapter adapter;
    final public static String[] SelectedMonth = new String[]{"January", "February", "March", "April",
            "May", "June", "July", "Augest", "September", "October", "November", "December"};

    public JattCalendarView(@NonNull Activity activity, @NonNull onCalendarLeftRight cc) {
        super(activity);
        this.activity = activity;
        this.cc = cc;
    }

    public JattCalendarView(@NonNull Activity activity, @NonNull List<JattCalendarModel> data, @NonNull onCalendarLeftRight cc) {
        super(activity);
        this.activity = activity;
        this.data = data;
        this.cc = cc;
    }



    public void initiate() {
        LayoutInflater inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.jatt_calendar_view,this);
        Init();
        onClick();
        SetCalendar(currentyear, currentmonth - 1);
    }

    public void initiate(int SunBackgroundColor, int SunTextColor) {
        LayoutInflater inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.jatt_calendar_view,this);
        SundayBackgroundColor = SunBackgroundColor;
        SundayTextColor = SunTextColor;
        Init();
        onClick();
        SetCalendar(currentyear, currentmonth - 1);
    }

    public void initiate(int SunBackgroundColor, int SunTextColor, int CurrBackgroundColor, int CurrTextColor) {
        LayoutInflater inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.jatt_calendar_view,this);
        SundayBackgroundColor = SunBackgroundColor;
        SundayTextColor = SunTextColor;
        CurrentBackgroundColor = CurrBackgroundColor;
        CurrentTextColor = CurrTextColor;
        Init();
        onClick();
        SetCalendar(currentyear, currentmonth - 1);
    }

    public void notifyDataSetChanged(@NonNull List<JattCalendarModel> Data) {
        data.clear();
        this.data.addAll(Data);
        SetCalendar(currentyear, currentmonth - 1);
    }

    public void Init() {
        currentmonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        currentyear = Calendar.getInstance().get(Calendar.YEAR);
        increase = activity.findViewById(R.id.increase);
        decrease = activity.findViewById(R.id.decrease);
        monthyear = activity.findViewById(R.id.monthyear);
        monthyear.setText( SelectedMonth[currentmonth-1]+ ", " + currentyear);
        Dates = activity.findViewById(R.id.dates);
        Dates.setLayoutManager(new GridLayoutManager(activity, 7));
    }

    public void onClick() {
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentmonth == 1) {
                    currentmonth = 12;
                    currentyear--;
                } else {
                    currentmonth--;
                }
                monthyear.setText( SelectedMonth[currentmonth-1]+ ", " + currentyear);
                SetCalendar(currentyear, currentmonth - 1);
                cc.onLeft("1", String.valueOf(currentmonth), String.valueOf(currentyear));

            }
        });
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentmonth == 12) {
                    currentmonth = 1;
                    currentyear++;
                } else {
                    currentmonth++;
                }
                monthyear.setText( SelectedMonth[currentmonth-1]+ ", " + currentyear);
                SetCalendar(currentyear, currentmonth - 1);
                cc.onRight("1", String.valueOf(currentmonth), String.valueOf(currentyear));
            }
        });
        monthyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog dpd = new MonthYearPickerDialog(activity);
                dpd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        monthyear.setText(month + ", " + year);
                        SetCalendar(year, month);
                    }
                });
            }
        });
    }

    public void SetCalendar(int Year, int Month) {
        sundaydates.clear();
        nonselecteddates.clear();
        int GetBlanks = GetBlanks(Year, Month);
        int startDate = 1;
        int GetAllDates = GetAllDates(Year, Month);
        List<JattCalendarModel> dates = new ArrayList<>();
        for (int i = 1; i <= (GetAllDates + GetBlanks); i++) {
            if (GetBlanks > 0 && i <= GetBlanks) {
                dates.add(new JattCalendarModel("", "", "", "", true, 0, 0));

            } else {
                boolean marked = false;
                String type = "";
                int color = 0;
                int backgroundColot = 0;
                if (data.size() > 0) {
                    JattCalendarModel jattt = null;
                    for (JattCalendarModel jatt : data) {
                        if ((serverFromDate(jatt.getYear() + "-" + jatt.getMonth() + "-" + jatt.getDate())).equals(serverFromDate(Year + "-" + Month + "-" + String.valueOf(startDate)))) {
                            jattt = jatt;
                            break;
                        }
                    }
                    if (null != jattt) {
                        dates.add(new JattCalendarModel(String.valueOf(startDate), String.valueOf(Month), String.valueOf(Year), jattt.getType(), jattt.isMarked(), jattt.getColor(), jattt.getBackgroundColor()));
                    } else {
                        dates.add(new JattCalendarModel(String.valueOf(startDate), String.valueOf(Month), String.valueOf(Year), type, marked, color, backgroundColot));
                    }
                } else {
                    dates.add(new JattCalendarModel(String.valueOf(startDate), String.valueOf(Month), String.valueOf(Year), type, marked, color, backgroundColot));
                }
                startDate++;
            }

        }

        for (int i = 0; i < dates.size(); i++) {
            if ((((i) % 7) == 0)&&!dates.get(i).isMarked()) {
                sundaydates.add(dates.get(i));
            }
        }

        for (int i = 0; i < dates.size(); i++) {
            if (!(((i) % 7) == 0) && !dates.get(i).isMarked()) {
                nonselecteddates.add(dates.get(i));
            }
        }

        adapter = new JattCalendarViewAdapter(dates, activity);
        Dates.setAdapter(adapter);
        cc.onsetCalenarlistner("1", String.valueOf(Month + 1), String.valueOf(Year), SundayDates(), NonSelectedDates());
    }

    public List<JattCalendarModel> NonSelectedDates() {
        return nonselecteddates;
    }

    public List<JattCalendarModel> SundayDates() {
        return sundaydates;
    }

    public class MonthYearPickerDialog extends Dialog {

        private static final int MAX_YEAR = 2099;
        private DatePickerDialog.OnDateSetListener listener;
        Context context;

        public MonthYearPickerDialog(@NonNull Context context) {
            super(context);
            this.context = context;
        }

        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }

        public Dialog create(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
            LayoutInflater inflater = getLayoutInflater();

            Calendar cal = Calendar.getInstance();

            View dialog = inflater.inflate(R.layout.year_month_picker_dialog, null);
            final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
            final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

            int year = cal.get(Calendar.YEAR);
            yearPicker.setMinValue(1900);
            yearPicker.setMaxValue(year);
            yearPicker.setValue(year);

            builder.setView(dialog).setPositiveButton(Html.fromHtml("<font color='#FF4081'>Ok</font>"), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                }
            }).setNegativeButton(Html.fromHtml("<font color='#FF4081'>Cancel</font>"), new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dismiss();
                }
            });
            return builder.create();
        }
    }

    public int GetBlanks(int Year, int Month) {
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = inFormat.parse("1-" + (Month + 1) + "-" + Year);
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            String goal = outFormat.format(date);
            int blanks = DaystoAddBlank(goal);
            return blanks;
        } catch (Exception e) {
            return 0;
        }
    }

    public int GetAllDates(int Year, int Month) {
        Calendar mycal = new GregorianCalendar(Year, Month, 1);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return daysInMonth;
    }

    public int DaystoAddBlank(String day) {
        if (day.equalsIgnoreCase("sunday")) {
            return 0;
        } else if (day.equalsIgnoreCase("monday")) {
            return 1;
        } else if (day.equalsIgnoreCase("tuesday")) {
            return 2;
        } else if (day.equalsIgnoreCase("wednesday")) {
            return 3;
        } else if (day.equalsIgnoreCase("thursday")) {
            return 4;
        } else if (day.equalsIgnoreCase("friday")) {
            return 5;
        } else if (day.equalsIgnoreCase("saturday")) {
            return 6;
        } else {
            return 0;
        }
    }

    public class JattCalendarViewAdapter extends RecyclerView.Adapter<JattCalendarViewAdapter.ViewHolder> {
        List<JattCalendarModel> teacherItems;
        Context context;

        public JattCalendarViewAdapter(List<JattCalendarModel> teacherItems, Context context) {
            this.teacherItems = teacherItems;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.jatt_calendar_date, parent, false);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (teacherItems.get(position).getDate().length() > 0) {
                holder.date.setText(teacherItems.get(position).getDate());
                if (teacherItems.get(position).isMarked()) {
                    if (((position) % 7) == 0) {
                        if (serverFromDate(getcurrentDateString()).equals(serverFromDate(currentyear + "-" + currentmonth + "-" + teacherItems.get(position).getDate()))) {
                            holder.date.setBackgroundTintList(ColorStateList.valueOf(ActivityCompat.getColor(context, CurrentBackgroundColor)));
                            holder.date.setTextColor(ActivityCompat.getColor(context, CurrentTextColor));
                        } else {
                            holder.date.setBackgroundTintList(ColorStateList.valueOf(ActivityCompat.getColor(context, SundayBackgroundColor)));
                            holder.date.setTextColor(ActivityCompat.getColor(context, SundayTextColor));
                        }
                    } else {
                        holder.date.setBackgroundTintList(ColorStateList.valueOf(ActivityCompat.getColor(context, teacherItems.get(position).getColor())));
                        holder.date.setTextColor(ActivityCompat.getColor(context, teacherItems.get(position).getBackgroundColor()));
                    }
                } else {
                    if (((position) % 7) == 0) {
                        if (serverFromDate(getcurrentDateString()).equals(serverFromDate(currentyear + "-" + currentmonth + "-" + teacherItems.get(position).getDate()))) {
                            holder.date.setBackgroundTintList(ColorStateList.valueOf(ActivityCompat.getColor(context, CurrentBackgroundColor)));
                            holder.date.setTextColor(ActivityCompat.getColor(context, CurrentTextColor));
                        } else {
                            holder.date.setBackgroundTintList(ColorStateList.valueOf(ActivityCompat.getColor(context, SundayBackgroundColor)));
                            holder.date.setTextColor(ActivityCompat.getColor(context, SundayTextColor));
                        }
                    } else {
                        if (serverFromDate(getcurrentDateString()).equals(serverFromDate(currentyear + "-" + currentmonth + "-" + teacherItems.get(position).getDate()))) {
                            holder.date.setBackgroundTintList(ColorStateList.valueOf(ActivityCompat.getColor(context, CurrentBackgroundColor)));
                            holder.date.setTextColor(ActivityCompat.getColor(context, CurrentTextColor));
                        } else {
                            holder.date.setBackgroundTintList(ColorStateList.valueOf(ActivityCompat.getColor(context, R.color.white)));
                            holder.date.setTextColor(ActivityCompat.getColor(context, R.color.black));
                        }
                    }
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cc.onDateClick(teacherItems.get(position).getDate(), teacherItems.get(position).getMonth(), teacherItems.get(position).getYear());
                    }
                });
            } else {
                holder.datelay.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return teacherItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView date;
            FrameLayout datelay;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                datelay = itemView.findViewById(R.id.datelay);
            }
        }
    }

    public static String getcurrentDateString() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static class JattCalendarModel {
        String Date, Month, Year, Type;
        boolean Marked;
        int Color, BackgroundColor;

        public JattCalendarModel(String date, String month, String year, String type, boolean marked, int color, int backgroundColor) {
            Date = date;
            Month = month;
            Year = year;
            Type = type;
            Marked = marked;
            Color = color;
            BackgroundColor = backgroundColor;
        }

        public String getMonth() {
            return Month;
        }

        public void setMonth(String month) {
            Month = month;
        }

        public String getYear() {
            return Year;
        }

        public void setYear(String year) {
            Year = year;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            Date = date;
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }

        public boolean isMarked() {
            return Marked;
        }

        public void setMarked(boolean marked) {
            Marked = marked;
        }

        public int getColor() {
            return Color;
        }

        public void setColor(int color) {
            Color = color;
        }

        public int getBackgroundColor() {
            return BackgroundColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            BackgroundColor = backgroundColor;
        }
    }

    public interface onCalendarLeftRight {
        public void onLeft(String day, String month, String year);

        public void onDateClick(String day, String month, String year);

        public void onsetCalenarlistner(String day, String month, String year, List<JattCalendarModel> Sundays, List<JattCalendarModel> NonSelectedDates);

        public void onRight(String day, String month, String year);
    }

    public static Date serverFromDate(String fromdate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = df.parse(fromdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String GetCurrentDate(Date dd) {
        Date c = dd;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;
    }
}
