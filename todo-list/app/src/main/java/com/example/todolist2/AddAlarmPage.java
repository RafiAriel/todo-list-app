package com.example.todolist2;



import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

import models.Mission;

public class AddAlarmPage extends AppCompatActivity {



    int userChoiceMinute,userChoiceHour;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mission_page);

        ArrayList<Mission> missionList= (ArrayList<Mission>) getIntent().getSerializableExtra("missionsList");




        //start Time
        Calendar currentTime = Calendar. getInstance();
        int currentHour = currentTime. get(Calendar. HOUR_OF_DAY);
        int currentMin= currentTime.get(Calendar.MINUTE);



        findViewById(R.id.timepicker_btn).setOnClickListener(btn->{
            TimePickerDialog timePickerDialog =new TimePickerDialog(AddAlarmPage.this,(timePicker, hourOfDay, minutes)->{
                userChoiceMinute=minutes;
                userChoiceHour=hourOfDay;
                ((TextView)findViewById(R.id.deadLine_txt)).setText(String.format("%02d", userChoiceHour)+":"+String.format("%02d",userChoiceMinute));

            },userChoiceMinute,userChoiceHour,true);
            timePickerDialog.show();
        });

        // set alarm in clock app
        findViewById(R.id.set_alarm_btn).setOnClickListener(btn->{
            Intent alarm= new Intent(AlarmClock.ACTION_SET_ALARM);
            alarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            alarm.putExtra(AlarmClock.EXTRA_HOUR,userChoiceHour);
            alarm.putExtra(AlarmClock.EXTRA_MINUTES,userChoiceMinute);
            alarm.putExtra(AlarmClock.EXTRA_MESSAGE,"rafi");
            if (alarm.resolveActivity(getPackageManager()) != null) startActivity(alarm);
            else Toast.makeText(AddAlarmPage.this,"There is no clock app in your phone",Toast.LENGTH_SHORT).show();
        });


        // add details to DOTO list
        findViewById(R.id.add_btn).setOnClickListener(btn->{
            String phone = ((TextView)findViewById(R.id.Phone_edit)).getText().toString();
            String address = ((TextView)findViewById(R.id.address_edit)).getText().toString();
            String email = ((TextView)findViewById(R.id.email_edit)).getText().toString();
            String todotext=(((EditText)findViewById(R.id.add_MIssion_editText)).getText().toString());
            Mission mission=new Mission(userChoiceHour,userChoiceMinute,currentHour,currentMin,phone,todotext,address,email);

            Intent to_main_screen=new Intent(this,MainActivity.class);
            to_main_screen.putExtra("mission",mission);
            startActivity(to_main_screen);
        });



    }
}

