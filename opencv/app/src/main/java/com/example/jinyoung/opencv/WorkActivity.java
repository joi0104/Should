package com.example.jinyoung.opencv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkActivity extends AppCompatActivity {

    TextView countdown;
    ImageView bmp1image;
    Button camera,insert;
    Bitmap bmp1,bmp2;
    Uri imgUri;
    String mCurrentPhotoPath;
    Intent intent;
    String id;
    long now = System.currentTimeMillis();
    int flag=0;
    int int_count_time=0;
    CountDownTimer count;



    final DBHelper1 dbHelper1 = new DBHelper1(WorkActivity.this, "MemberData.db",null,1);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work);


        countdown = findViewById(R.id.countdown);
        bmp1image = findViewById(R.id.bmp1image);
        camera = findViewById(R.id.camera);

        intent = getIntent();
        id = intent.getExtras().getString("아이디");

        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 d일");
        String todayDate = sdf.format(date);
        int i = dbHelper1.get_ordernum(todayDate,id);

        Uri temp = Uri.parse(dbHelper1.getResult_course_image(id,i));
        try {
            bmp1 =MediaStore.Images.Media.getBitmap( getContentResolver(), temp );
        } catch (IOException e) {
            e.printStackTrace();
        }

        bmp1image.setImageBitmap(bmp1);
        String count_time = dbHelper1.getResult_time_todo(id,todayDate);

        if(count_time.equals("10분")) int_count_time=600000;
        else if(count_time.equals("20분")) int_count_time=1200000;
        else if(count_time.equals("30분")) int_count_time=1800000;

        count = new CountDownTimer(int_count_time,1) {
            @Override
            public void onTick(long l) {
                countdown.setText((String.format(Locale.getDefault(), "%d sec.", l / 1000L)));
            }

            @Override
            public void onFinish() {
                Intent intent2 = new Intent(getApplicationContext(), LockActivity.class);
                intent2.putExtra("아이디",id);
                startActivity(intent2);
            }
        }.start();


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
                if (permissionCamera == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(WorkActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }
                else {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(WorkActivity.this, getPackageName(), photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 0);
                        }
                    }
                }

            }
        });


    }

    public int match(){
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
        byte[] file1 = baos1.toByteArray();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
        byte[] file2 = baos2.toByteArray();
        int ret = compareFeature(file1, file2);
        if (ret > 0) {
            count.cancel();
            Toast.makeText(getApplicationContext(),"오늘의 운동 성공!",Toast.LENGTH_LONG).show();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 d일");
            String todayDate = sdf.format(date);
            dbHelper1.update_achieve(todayDate,id,1);
            finish();
            return 1;
        }
        else {
            Toast.makeText(getApplicationContext(), "사진을 다시 입력해주세요", Toast.LENGTH_LONG).show();
            return 0;
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)

    {

        if(resultCode == RESULT_OK){

            if(requestCode==0)  {
                File file = new File(mCurrentPhotoPath);
                try {
                    Uri temp =  Uri.fromFile(file);
                    bmp2 = MediaStore.Images.Media.getBitmap(getContentResolver(), temp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                match();
            }

        }

    }


    public int  compareFeature(byte[] file1, byte[] file2){

        int retVal=0;

        System.loadLibrary("opencv_java3");

        Mat img1 = Imgcodecs.imdecode(new MatOfByte(file1), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        Mat img2 = Imgcodecs.imdecode(new MatOfByte(file2), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2= new Mat();

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

        detector.detect(img1, keypoints1);
        detector.detect(img2, keypoints2);

        extractor.compute(img1, keypoints1, descriptors1);
        extractor.compute(img2, keypoints2, descriptors2);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        MatOfDMatch matches = new MatOfDMatch();

        if(descriptors2.cols()== descriptors1.cols()) {
            matcher.match(descriptors1, descriptors2, matches);

            DMatch[] match = matches.toArray();
            double max_dist=0;
            double min_dist=100;

            for (int i=0; i<descriptors1.rows(); i++) {
                double dist = match[i].distance;
                if(dist < min_dist ) min_dist = dist;
                if(dist > max_dist ) max_dist = dist;
            }


            for(int i=0;i<descriptors1.rows();i++) {
                if(match[i].distance<=20) retVal++;
            }
        }


        return retVal;
    }


}
