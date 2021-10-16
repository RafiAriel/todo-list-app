package com.example.todolist2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import models.Mission;

public class MainActivity extends AppCompatActivity {


    static ArrayList<Mission> missions = new ArrayList<>();
    static ArrayList<String> allSubjectList= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mission mission = (Mission) getIntent().getSerializableExtra("mission");
        if(mission != null) {
            missions.add(mission);
            allSubjectList.add(mission.getMission_for_today());
        }
         ListView list = (ListView) findViewById(R.id.listview);



        findViewById(R.id.to_add_mission_screen).setOnClickListener(btn -> {
            Intent add_mission_screen = new Intent(this, AddAlarmPage.class);
            add_mission_screen.putExtra("missionsList",(Serializable)missions);
            startActivity(add_mission_screen);

        });


        if(!missions.isEmpty()) {
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listitem,allSubjectList);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    movingToDetailsPage(missions.get(i),i);

                }
            });
        }


    }

    public void movingToDetailsPage(Mission mission, int index){
        Intent intent = new Intent(this,DetailsMissionPage.class);
        intent.putExtra("mission",mission);
        intent.putExtra("index",index);
        startActivity(intent);
    }
}