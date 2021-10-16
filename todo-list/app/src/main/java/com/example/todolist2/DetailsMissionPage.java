package com.example.todolist2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import models.Mission;

public class DetailsMissionPage extends AppCompatActivity {

    ArrayList<Integer> timeleftArr = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_page);

        Mission mission = (Mission) getIntent().getSerializableExtra("mission");
        int index=(int)getIntent().getSerializableExtra("index");



        TextView deadline = ((TextView) findViewById(R.id.deadline_view));
        TextView subject = ((TextView) findViewById(R.id.subject_view));
        TextView phone = ((TextView) findViewById(R.id.phone_view));
        TextView address = ((TextView) findViewById(R.id.address_view));
        TextView email = ((TextView) findViewById(R.id.email_view));



        //putting the text
        subject.setText(mission.getMission_for_today());
        deadline.setText(String.format("%02d", mission.getDeadlineTimeHour()) + ":" + String.format("%02d", mission.getDeadlineTimeMin()));
        phone.setText(mission.getPhone());
        address.setText(mission.getAddress());
        email.setText(mission.getemail());



        //make the text hyperLink
        ArrayList<TextView> textViewArrayList = new ArrayList<>();
        textViewArrayList.add(phone);
        textViewArrayList.add(address);
        textViewArrayList.add(email);
        setLink(textViewArrayList);


        // Action DIAL
        phone.setOnClickListener(click -> {
            Intent call = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone.getText().toString(), null));
            if (call.resolveActivity(getPackageManager()) != null) {
                startActivity(call);
            } else
                Toast.makeText(this, "NO service location in the phone", Toast.LENGTH_SHORT).show();
        });

        // google maps - Action View
        address.setOnClickListener(click -> {
            String locationString = address.getText().toString();
            Uri locationUri = Uri.parse("geo:0,0?q=" + locationString);
            Intent location = new Intent(Intent.ACTION_VIEW, locationUri);
            if (location.resolveActivity(getPackageManager()) != null) {
                startActivity(location);
            } else
                Toast.makeText(this, "NO service location in the phone", Toast.LENGTH_SHORT).show();
        });



        email.setOnClickListener(click -> {
            Intent emailIntent = new Intent(this, EmailPage.class);
            emailIntent.putExtra("email", email.getText().toString());
            startActivity(emailIntent);
        });



        ((Button)findViewById(R.id.popup)).setOnClickListener(btnToContact->{
            Intent popupIntent =new Intent(DetailsMissionPage.this, PopUp_contact.class);
            popupIntent.putExtra("mission",mission);
            startActivity(popupIntent);
        });

        ((Button)findViewById(R.id.move_to_calendarBtn)).setOnClickListener(btnToContact->{
            Intent moveToCalendar =new Intent(DetailsMissionPage.this, PopUp_Calendar.class);
            moveToCalendar.putExtra("mission",mission);
            startActivity(moveToCalendar);
        });



        ((Button)findViewById(R.id.delete_mission)).setOnClickListener(btnDelete->{
           MainActivity.missions.remove(index);
           MainActivity.allSubjectList.remove(index);
           Intent toMainPage= new Intent(this,MainActivity.class);
           startActivity(toMainPage);
        });






    }//end of onCreate


    // set TextView hyperLink
    public void setLink(ArrayList<TextView> textView) {
        for (TextView tv : textView) {
            final CharSequence text = tv.getText();
            final SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spannableString, TextView.BufferType.SPANNABLE);
        }
    }



}
