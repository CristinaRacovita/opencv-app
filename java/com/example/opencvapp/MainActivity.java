package com.example.opencvapp;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= 25) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){ //daca am ceva rezultat dat
                Uri imageUri = data.getData();
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("url",imageUri.toString());
                startActivity(intent);
            }
        }
        if(requestCode == 1234){
            Uri photoURI = FileProvider.getUriForFile(this,"com.example.cristina.myapplication.fileprovider",photoFile);
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("url",photoURI.toString());
            startActivity(intent);
        }
    }

    public void go_to_galery(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,1000);
    }

    public void make_photo(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = createPhotoFile();
        Uri photoURI = FileProvider.getUriForFile(this,"com.example.cristina.myapplication.fileprovider",photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        startActivityForResult(intent,1234);
    }

    public File createPhotoFile(){
        String name = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File storageFile = Environment.getExternalStorageDirectory();
        try{
            File image = File.createTempFile(name,".jpg",storageFile);
            return image;
        }
        catch (Exception ex){
            Log.e("EDIT","EXCEPTIE");
        }
        return null;
    }

    public void make_collage(View view) {
        Intent intent = new Intent(MainActivity.this, SablonActivity.class);
        startActivity(intent);
    }
}
