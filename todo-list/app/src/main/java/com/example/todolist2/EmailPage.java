package com.example.todolist2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist2.databinding.ActivityMainBinding;

public class EmailPage extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_page);


        //our floating  display
        DisplayMetrics display= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        int width=display.widthPixels;
        int height=display.heightPixels;

        getWindow().setLayout((int) (width*.8),(int) (height*.7));

       String email='"'+(String)getIntent().getSerializableExtra("email")+'"';


        // Action Send - SEND Email
        Button send = ((Button)findViewById(R.id.send_popup_btn));
        send.setOnClickListener(btn->{


            String subject = ((EditText)findViewById(R.id.subject_popup_edit)).getText().toString();
            String content = ((EditText)findViewById(R.id.content_popup_edit)).getText().toString();

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, content);
            System.out.println(content);

            //need this to prompts email client only
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));

        });


    }
}
