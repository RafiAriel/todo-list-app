package com.example.todolist2;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import models.Mission;

public class PopUp_Calendar extends Activity {


    int minEnd,hourEnd,minStart,hourStart,year,month,day;
    String titleString,descriptionString,locationString,emailString;
    private String[] contactPermission;
    private static final int WRITE_CALENDAR_PERMISSION_CODE = 300;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_popup);


        //our floating  display
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        int width = display.widthPixels;
        int height = display.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        Mission mission = (Mission) getIntent().getSerializableExtra("mission");
        contactPermission = new String[]{Manifest.permission.WRITE_CALENDAR};

        EditText description = findViewById(R.id.descriptionCalendar_edit);
        EditText title = findViewById(R.id.titleCalender_edit);
        EditText location = findViewById(R.id.locationCalender_edit);
        EditText email= findViewById(R.id.emailCalender_edit);
        TextView startTime = findViewById(R.id.startTime_calendar);
        TextView endTime = findViewById(R.id.endTime_calendar);
        TextView date = findViewById(R.id.date_calendar);

        ArrayList<TextView> textViewsHyperLink = new ArrayList<>();  // create array to add TextViews to Hyperlink


        Calendar mClndr = Calendar.getInstance();
        day = mClndr.get(Calendar.DAY_OF_MONTH);
        month = mClndr.get(Calendar.MONTH);
        year = mClndr.get(Calendar.YEAR);
        hourStart = mClndr.get(Calendar.HOUR_OF_DAY);
        minStart = mClndr.get(Calendar.MINUTE);

        date.setText("Date selected: " + day + "." + month + "." + year);
        String time = String.format("%02d", hourStart) + ":" + String.format("%02d", minStart);
        startTime.setText("Start Time:" + time);
        time = String.format("%02d", mission.getDeadlineTimeHour()) + ":" + String.format("%02d", mission.getDeadlineTimeMin());
        endTime.setText("End Time: " + time);
        textViewsHyperLink.add(startTime); //0
        textViewsHyperLink.add(endTime); //1
        textViewsHyperLink.add(date);//2
        new DetailsMissionPage().setLink(textViewsHyperLink);


        description.setText(mission.getMission_for_today());
        location.setText(mission.getAddress());
        email.setText(mission.getemail());


        // start time text view
        startTime.setOnClickListener(textviewStartTime -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hourOfDay, minutes) -> {
                String temp = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minutes);
                ((TextView) findViewById(R.id.startTime_calendar)).setText(temp);
                hourStart=hourOfDay;
                minStart=minutes;
            }, hourStart, minStart, true);
            timePickerDialog.show();
        });

        // end time text view
        endTime.setOnClickListener(textviewEndTime -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hourOfDay, minutes) -> {
                String temp = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minutes);
                ((TextView) findViewById(R.id.endTime_calendar)).setText(temp);
                hourEnd=hourOfDay;
                minEnd=minutes;
            }, hourEnd, minEnd, true);
            timePickerDialog.show();
        });

        // date picker
        date.setOnClickListener(btnDatePicker -> {
            DatePickerDialog dpd = new DatePickerDialog(PopUp_Calendar.this, (datePicker, mYear, mMonth, mDay)-> {
                date.setText("Date selected: " + mDay + "/" + (mMonth + 1) + "/" + mYear);
                year=mYear;
                month=mMonth+1;
                day=mDay;
            }, year, month, day);
            dpd.show();
        });


        findViewById(R.id.add_to_calendarBtn).setOnClickListener(btnAddEvent -> {
            titleString=title.getText().toString();
            descriptionString=description.getText().toString();
            locationString=location.getText().toString();
            emailString=email.getText().toString();
            if(isWriteContactPermissionEnabled())
                addCalendarEvent();
            else {
                requestWriteContactPermission();
            }
        });
    }//OnCreate end


    public void addCalendarEvent() {
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, (month-1), day, hourStart, minStart);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, (month-1), day, hourEnd, minEnd);
        endMillis = endTime.getTimeInMillis();
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.CALENDAR_ID, 1);
        intent.putExtra(CalendarContract.Events.TITLE, titleString);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, locationString);
        intent.putExtra(CalendarContract.Events.DESCRIPTION,descriptionString);
        intent.putExtra(CalendarContract.Events.ALL_DAY, false);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,startMillis );
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,endMillis );
        intent.putExtra(Intent.EXTRA_EMAIL, emailString);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private boolean isWriteContactPermissionEnabled() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // request Permission
    private void requestWriteContactPermission() {
        ActivityCompat.requestPermissions(this, contactPermission, WRITE_CALENDAR_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
            if (requestCode == WRITE_CALENDAR_PERMISSION_CODE) {
                boolean haveWriteContactPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (haveWriteContactPermission) {
                    addCalendarEvent();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

                }

            }
        }
    }
}





