package com.example.jinyoung.opencv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SecondActivity extends Activity {
    String id;
    ImageView profileView; //프로필 기본 이미지
    LinearLayout second_layout1; //프로필 레이아웃
    LinearLayout layout_achievement, layout_course, layout_membership;
    Button btn_workout;
    ImageView popup_menu;
    TextView profile_name;
    TextView second_time_txt;
    TextView second_course_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        long now = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final DBHelper1 dbHelper1 = new DBHelper1(getApplicationContext(), "MemberData.db",null,1);

        Intent intent = getIntent();
        id = intent.getExtras().getString("아이디");

        profileView = (ImageView)findViewById(R.id.profileView); //프로필사진(둥글게 할라고)
        second_layout1 = (LinearLayout)findViewById(R.id.second_layout1); //프로필 있는 레이아웃
        layout_achievement = (LinearLayout)findViewById(R.id.layout_achievement); //My Achievement 레이아웃
        layout_course = (LinearLayout)findViewById(R.id.layout_course); //Course Settings 레이아웃
        layout_membership = (LinearLayout)findViewById(R.id.layout_membership); //Membership Info 레이아웃
        btn_workout = (Button)findViewById(R.id.btn_workout); //Workout 버튼
        popup_menu = (ImageView)findViewById(R.id.popup_menu);
        profile_name = (TextView)findViewById(R.id.profile_name);
        second_time_txt = (TextView)findViewById(R.id.second_time_txt);
        second_course_txt = (TextView)findViewById(R.id.second_course_txt);

        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 dd일");
        final String todayDate = sdf.format(date);

        profileView.setBackground(new ShapeDrawable(new OvalShape())); //프로필 사진 둥글게
        profileView.setClipToOutline(true);
        Uri temp = Uri.parse(dbHelper1.getResult_profile_image(id));
        Bitmap bmp1 = null;
        try {
            bmp1 = MediaStore.Images.Media.getBitmap( getContentResolver(), temp );
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileView.setImageBitmap(bmp1);

        profile_name.setText(dbHelper1.getResult_member_name(id) + " 님");
        if(dbHelper1.getResult_time_todo(id,todayDate).length() > 0) {
            second_time_txt.setText(dbHelper1.getResult_time_todo(id, todayDate) + " : ");
            second_course_txt.setText(dbHelper1.getResult_course_todo(id, todayDate));
        }
        else {
            second_time_txt.setText("일정이 ");
            second_course_txt.setText("없습니다.");
        }

        second_layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodoActivity.class);
                intent.putExtra("아이디",id);
                startActivity(intent);
            }
        });

        layout_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseActivity.class);
                intent.putExtra("아이디",id);
                startActivity(intent);
            }
        });

        layout_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemberActivity.class);
                intent.putExtra("아이디",id);
                startActivity(intent);
            }
        });

        popup_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.menu, p.getMenu());

                p.show();
            }
        });

        layout_achievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AchieveActivity.class);
                intent.putExtra("아이디",id);
                startActivity(intent);
            }
        });

        btn_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbHelper1.isExist_todo(id,todayDate)) {
                    Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                    intent.putExtra("아이디", id);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"오늘의 일정이 없습니다.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    public void onResume() {

        super.onResume();
        long now = System.currentTimeMillis();

        profile_name = (TextView)findViewById(R.id.profile_name);
        second_time_txt = (TextView)findViewById(R.id.second_time_txt);
        second_course_txt = (TextView)findViewById(R.id.second_course_txt);
        profileView = (ImageView)findViewById(R.id.profileView); //프로필사진(둥글게 할라고)

        final DBHelper1 dbHelper1 = new DBHelper1(getApplicationContext(), "MemberData.db",null,1);

        Uri temp = Uri.parse(dbHelper1.getResult_profile_image(id));
        Bitmap bmp1 = null;
        try {
            bmp1 = MediaStore.Images.Media.getBitmap( getContentResolver(), temp );
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileView.setImageBitmap(bmp1);

        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 dd일");
        final String todayDate = sdf.format(date);



        profile_name.setText(dbHelper1.getResult_member_name(id) + " 님");
        if(dbHelper1.getResult_time_todo(id,todayDate).length() > 0) {
            second_time_txt.setText(dbHelper1.getResult_time_todo(id, todayDate) + " : ");
            second_course_txt.setText(dbHelper1.getResult_course_todo(id, todayDate));
        }
        else {
            second_time_txt.setText("일정이 ");
            second_course_txt.setText("없습니다.");
        }
    }

}
