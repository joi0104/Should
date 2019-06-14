package com.example.jinyoung.opencv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class AchieveActivity extends Activity{

    Intent intent;
    String id;
    LinearLayout achieve;
    android.widget.ProgressBar ProgressBar;
    TextView progresstxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achieve);
        intent = getIntent();
        id = intent.getExtras().getString("아이디");

        final ImageView popup_menu = (ImageView)findViewById(R.id.popup_menu);
        final DBHelper1 dbHelper1 = new DBHelper1(getApplicationContext(), "MemberData.db",null,1);
        final int course_num = dbHelper1.CourseCount_course(id);
        String a[] = dbHelper1.get_todo(id);
        int b[] = dbHelper1.get_achieve(id);
        achieve = findViewById(R.id.achieve_scroll_layout);
        ProgressBar = findViewById(R.id.progressBar);
        progresstxt = findViewById(R.id.progresstext);
        int one_count=0;
        int count_todo = dbHelper1.count_todo(id);


        popup_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.menu, p.getMenu());

                p.show();
            }
        });

        for(int i=0;i<count_todo;i++) {
            LinearLayout dynamic = new LinearLayout(this);
            dynamic.setOrientation(LinearLayout.HORIZONTAL);
            TextView newtxt = new TextView(this);
            newtxt.setText(a[i]);
            newtxt.setTextSize(15);
          TextView btn = new TextView(this);
            Log.d("b값",String.valueOf(b[i]),null);
            if(b[i]==1){
                btn.setText("성공");
                btn.setBackgroundColor(Color.BLUE);
                btn.setTextSize(15);
                btn.setTextColor(Color.WHITE);
                one_count++;
            }
            else{
                btn.setText("실패");
                btn.setBackgroundColor(Color.RED);
                btn.setTextSize(15);
                btn.setTextColor(Color.WHITE);
            }
            dynamic.setBackgroundResource(R.drawable.round_corner3);
            btn.setPadding(10,10,10,10);
            newtxt.setPadding(0,0,10,0);
            dynamic.setPadding(20,70,20,70);
            dynamic.addView(newtxt);
            dynamic.addView(btn);
            achieve.addView(dynamic);
        }

        ProgressBar.setMax(count_todo);
        ProgressBar.setProgress(one_count);
        String temp;
        if(count_todo==0) {temp = "0%";}
        else {temp = String.valueOf((int)(((double)one_count/(double)count_todo)*100)) + "%";}
        progresstxt.setText(temp);




    }

}
