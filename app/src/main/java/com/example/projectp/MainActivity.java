package com.example.projectp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;

import android.view.View;
import android.view.Window;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_CAMERA=100;
    CameraSurfaceView surfaceView;
    Bitmap bitmap;

    String[] pokemonName;

    int[] pokemonImg = new int[41];
    int[] sampleImg = new int[41];
    int matchPokemon = 42;

    TextView textView;
    ImageButton btnShoot, btnInfo, btnOK, btnCancel;

    ImageView imageView;

    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent loding = new Intent(this, LoadingActivity.class);
        startActivity(loding);


        int permssionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permssionCheck!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this,"카메라부분 사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                Toast.makeText(this,"카메라부분 사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
        }


        for(int i = 1; i<42; i++){
            pokemonImg[i-1] =  getResources().getIdentifier("p"+i,"drawable",getPackageName());
        }
        for(int i = 1; i<42; i++){
            sampleImg[i-1] =  getResources().getIdentifier("sample"+i,"drawable",getPackageName());
        }
        pokemonName = getResources().getStringArray(R.array.pokemonName);


        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogview);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        surfaceView = findViewById(R.id.surfaceview);

        btnShoot = (ImageButton) findViewById(R.id.btnShoot);
        btnShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"잠시만 기다려주세요.",Toast.LENGTH_SHORT).show();
                btnShoot.setEnabled(false);
                btnInfo.setEnabled(false);
                capture();
                btnShoot.setEnabled(true);
                btnInfo.setEnabled(true);
            }
        });
        btnInfo  = (ImageButton) findViewById(R.id.btnInfomation);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(matchPokemon != 42){
                    Intent intent = new Intent(getApplicationContext(),InformationActivity.class);
                    intent.putExtra("number",matchPokemon);
                    intent.putExtra("imageId",pokemonImg[matchPokemon]);
                    startActivity(intent);
                    dialog.dismiss();
                }
                else
                    Toast.makeText(getApplicationContext(),"최근에 찾은 포켓몬이 없습니다.",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void capture(){
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                camera.startPreview();
                bitmap =  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/15,bitmap.getHeight()/5,true);
                matchPokemon = comparePokemon(bitmap);
                if(matchPokemon==42){
                    Toast.makeText(getApplicationContext(),"확인 후 다시 시도해주시기 바랍니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    showDialog();
                }
            }
        });
    }

    public int comparePokemon(Bitmap bitmap){
        OpenCVLoader.initDebug();
        double result0, result1, result2, result3;
        int match = 42;
        int index =0;
        double max = 0;

        Mat img2 = new Mat();

        Mat hsvImg1 = new Mat();
        Mat hsvImg2 = new Mat();

        List<Mat> listImg1 = new ArrayList<Mat>();
        List<Mat> listImg2 = new ArrayList<Mat>();

        Mat histImg1 = new Mat();
        Mat histImg2 = new Mat();

        MatOfFloat ranges = new MatOfFloat(0,255);
        MatOfInt histSize = new MatOfInt(50);
        MatOfInt channels = new MatOfInt(0);

        Utils.bitmapToMat(bitmap ,img2);
        img2 = removeBackgound(img2);

        for (int i : sampleImg){
            Mat img1 = new Mat();
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), i);
            Utils.bitmapToMat(bitmap1 ,img1);
            Imgproc.cvtColor(img1, hsvImg1,Imgproc.COLOR_BGR2HSV);
            Imgproc.cvtColor(img2, hsvImg2,Imgproc.COLOR_BGR2HSV);

            listImg1.add(hsvImg1);
            listImg2.add(hsvImg2);

            Imgproc.calcHist(listImg1, channels, new Mat(), histImg1, histSize, ranges);
            Imgproc.calcHist(listImg2, channels, new Mat(), histImg2, histSize, ranges);

            Core.normalize(histImg1, histImg1, 0, 1, Core.NORM_MINMAX, -1, new Mat());
            Core.normalize(histImg2, histImg2, 0, 1, Core.NORM_MINMAX, -1, new Mat());

            result0 = Imgproc.compareHist(histImg1,histImg2,0);
            result1 = Imgproc.compareHist(histImg1,histImg2,1);
            result2 = Imgproc.compareHist(histImg1,histImg2,2);
            result3 = Imgproc.compareHist(histImg1,histImg2,3);

            int count = 0;
            if(result0 > 0.95) {
                count++;
            }
            if(result1 < 0.6) {
                count++;
            }
            if(result2 > 1.14) {
                count++;
            }
            if(result3 < 0.3) {
                count++;
            }
            if(count >= 4) {
                if (max < result2) {
                    max = result2;
                    match = index;
                }
            }
            index++;
        }
        return match;
    }

    public Mat removeBackgound(Mat img){
        int row = img.rows();
        int col = img.cols();

        Point p1 = new Point(col/5, row/5);
        Point p2 = new Point(col-col/5, row-row/5);

        Rect rect = new Rect(p1,p2);
        
        Mat mask = new Mat();
        mask.setTo(new Scalar(125));
        Mat fgdModel = new Mat();
        fgdModel.setTo(new Scalar(255, 255, 255));
        Mat bgdModel = new Mat();
        bgdModel.setTo(new Scalar(255, 255, 255));

        Mat img3 = new Mat();
        Imgproc.cvtColor(img, img3, Imgproc.COLOR_RGBA2RGB);
        Imgproc.grabCut(img3, mask, rect, bgdModel, fgdModel, 10, Imgproc.GC_INIT_WITH_RECT);
        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));


        Core.compare(mask, source, mask, Core.CMP_EQ);
        Mat foreground = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        img.copyTo(foreground, mask);

        return foreground;
    }
    public void showDialog(){
        dialog.show();
        textView = dialog.findViewById(R.id.textView);
        textView.setText(pokemonName[matchPokemon]);
        imageView = dialog.findViewById(R.id.imageview);
        imageView.setImageResource(pokemonImg[matchPokemon]);
        btnOK = dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),InformationActivity.class);
                intent.putExtra("number",matchPokemon);
                intent.putExtra("imageId",pokemonImg[matchPokemon]);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                matchPokemon = 42;
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

}