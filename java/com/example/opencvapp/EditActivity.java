package com.example.opencvapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static org.opencv.core.CvType.CV_8UC3;

public class EditActivity extends AppCompatActivity {

    private ImageView mPhoto;
    private static Mat img;
    private SeekBar mSeekBar;
    private static Bitmap mBitmap;
    private final static String TAG = "EditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        OpenCVLoader.initDebug();
        mPhoto = findViewById(R.id.myphoto);
        mSeekBar = findViewById(R.id.seekbar);
        upload_image();
    }

    public void modify_contrast(View view) {
        mSeekBar.setProgress(1);
        mSeekBar.setOnSeekBarChangeListener(new ContrastListener());
        mSeekBar.setVisibility(View.VISIBLE);

    }

    public void black_and_white(View view) {
        mSeekBar.setVisibility(View.INVISIBLE);
        Bitmap bitmap = OpenCVOperation.thresholding(img,mBitmap);
        mPhoto.setImageBitmap(bitmap);
    }

    public void gaussian_filter(View view) {
        mSeekBar.setProgress(1);
        mSeekBar.setMax(11);
        mSeekBar.incrementProgressBy(2);
        mSeekBar.setOnSeekBarChangeListener(new BlurListener());
        mSeekBar.setVisibility(View.VISIBLE);

    }

    public void grey_filter(View view) {
        mSeekBar.setVisibility(View.INVISIBLE);
        Bitmap bitmap = OpenCVOperation.grey(img,mBitmap);
        mPhoto.setImageBitmap(bitmap);
    }

    public void negative_photo(View view) {
        mSeekBar.setVisibility(View.INVISIBLE);
        Bitmap bitmap = OpenCVOperation.make_negative(img,mBitmap);
        mPhoto.setImageBitmap(bitmap);
    }

    public void save_file(View view) {
        Toast.makeText(this,"Photo is saved!",Toast.LENGTH_LONG).show();
        save_photo();
    }

    public void corection(View view) {
        mSeekBar.setVisibility(View.INVISIBLE);
        Bitmap bitmap = OpenCVOperation.gamma_corection(img,mBitmap);
        mPhoto.setImageBitmap(bitmap);
    }

    public void edge_filter(View view) {
        Bitmap bitmap = OpenCVOperation.edgeFilter(img,mBitmap);
        mPhoto.setImageBitmap(bitmap);
    }


    public class ContrastListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            BitmapDrawable drawable = (BitmapDrawable) mPhoto.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Mat img = new Mat();
            Utils.bitmapToMat(bitmap,img);
            progress = progress * 5;
            if (progress >= 255){
                progress = 255;
            }
            Bitmap bitmap1 = OpenCVOperation.contrast(img,bitmap,progress);
            mPhoto.setImageBitmap(bitmap1);
        }
    }

    public class BrightnessListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            BitmapDrawable drawable = (BitmapDrawable) mPhoto.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Mat img = new Mat();
            Utils.bitmapToMat(bitmap,img);
            Bitmap newbitmap = OpenCVOperation.doBrightness(img,bitmap,progress);
            mPhoto.setImageBitmap(newbitmap);
        }
    }

    public class BlurListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if(progress < 3){
                progress = 3;
            }
            if(progress > 11){
                progress = 11;
            }
            Bitmap bitmap = OpenCVOperation.gaussianFilter(img,mBitmap,progress);
            mPhoto.setImageBitmap(bitmap);
        }
    }
    public void upload_image(){
        Uri imageUri = Uri.parse(getIntent().getStringExtra("url"));
        Log.d("HEIHEIHEI","url primit " + imageUri.toString());
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            mBitmap = BitmapFactory.decodeStream(inputStream);
            img = new Mat(mBitmap.getHeight(),mBitmap.getWidth(), CV_8UC3);
            Utils.bitmapToMat(mBitmap,img);
            mPhoto.setImageBitmap(mBitmap);
        }
        catch(FileNotFoundException ex){
            Log.d("Edit","Exceptie "+ex.getMessage());
        }

    }

    public void brightness(View view) {
        mSeekBar.setProgress(1);
        mSeekBar.setVisibility(View.VISIBLE);
        mSeekBar.setOnSeekBarChangeListener(new BrightnessListener());
    }

    public void save_photo(){
        BitmapDrawable drawable = (BitmapDrawable) mPhoto.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Mat img = new Mat();
        Utils.bitmapToMat(bitmap,img);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGR);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        SimpleDateFormat format = new SimpleDateFormat("DD:MM:YYYY:HH:MM:SS", Locale.US);
        String hour = format.format(new Date());
        String filename = hour+"_edit.png";
        File file = new File(path, filename);
        filename = file.toString();
        Imgcodecs.imwrite(filename,img);
    }
}
