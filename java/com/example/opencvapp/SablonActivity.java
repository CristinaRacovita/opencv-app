package com.example.opencvapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.opencv.core.CvType.CV_8UC3;

public class SablonActivity extends AppCompatActivity {

    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;
    private Mat img1, img2, img3, img4;
    private Bitmap  mBitmap1, mBitmap2, mBitmap3, mBitmap4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sablon);
        OpenCVLoader.initDebug();
        mImage1 = findViewById(R.id.imagine1);
        mImage2 = findViewById(R.id.imagine2);
        mImage3 = findViewById(R.id.imagine3);
        mImage4 = findViewById(R.id.imagine4);

        if(Build.VERSION.SDK_INT >= 25) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2000);
        }
    }

    public Bitmap upload_image(Uri imageUri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            return BitmapFactory.decodeStream(inputStream);
        }
        catch (Exception ex){
            Log.e("Sablon1","Eroare la citire uri");
        }
        return null;
    }

    public Mat upload_photo(Mat img, Bitmap bitmap, Uri imageUri,ImageView imageView){
        bitmap = upload_image(imageUri);
        img = new Mat(bitmap.getHeight(), bitmap.getWidth(), CV_8UC3);
        Utils.bitmapToMat(bitmap,img);
        Mat resize = new Mat();
        Imgproc.resize(img,resize,new Size(200,200),0,0,Imgproc.INTER_AREA);
        Bitmap bmOut = Bitmap.createBitmap(200, 200, bitmap.getConfig());
        Utils.matToBitmap(resize,bmOut);
        imageView.setImageBitmap(bmOut);
        return resize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){ //daca am ceva rezultat dat
                Uri imageUri = data.getData();
                img1 = upload_photo(img1,mBitmap1,imageUri,mImage1);
            }
        }
        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){ //daca am ceva rezultat dat
                Uri imageUri = data.getData();
                img2 = upload_photo(img2,mBitmap2,imageUri,mImage2);
            }
        }
        if(requestCode == 3){
            if(resultCode == Activity.RESULT_OK){ //daca am ceva rezultat dat
                Uri imageUri = data.getData();
                img3 = upload_photo(img3,mBitmap3,imageUri,mImage3);
            }
        }
        if(requestCode == 4){
            if(resultCode == Activity.RESULT_OK){ //daca am ceva rezultat dat
                Uri imageUri = data.getData();
                img4 = upload_photo(img4,mBitmap4,imageUri,mImage4);
            }
        }
    }

    public void save_photo(View view){
        Mat img = OpenCVOperation.format_image(img1,img2,img3,img4);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGR);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        SimpleDateFormat format = new SimpleDateFormat("DD:MM:YYYY:HH:MM:SS", Locale.US);
        String hour = format.format(new Date());
        String filename = hour+"_colaj.png";
        File file = new File(path, filename);
        filename = file.toString();
        Imgcodecs.imwrite(filename,img);
        Toast.makeText(this,"Photo is saved!",Toast.LENGTH_LONG).show();
    }

    public void add_image1(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,1);
    }
    public void add_image2(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,2);
    }
    public void add_image3(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,3);
    }
    public void add_image4(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,4);
    }
}
