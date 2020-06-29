package com.example.external;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private ImageView image;
    private Uri imageUri;
    private File file2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        image = (ImageView) findViewById(R.id.image);

        String path = Environment.getExternalStorageDirectory() + "/FolderAndFiles";
        String path2 = Environment.getExternalStorageDirectory() + "/FolderAndFiles/logo.jpg";
        File file = new File(path);
        file.mkdirs();
        file2 = new File(path2);

        Uri path3 = FileProvider.getUriForFile(this,
                "com.example.external", new File(file2.getPath()));

        if(file2.exists()) {
            image.setBackgroundColor(Color.WHITE);
            Picasso.get().load(path3).into(image);
        }

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && RESULT_OK == resultCode && data.getData() != null){
            imageUri = data.getData();
            image.setBackgroundColor(Color.WHITE);
            Picasso.get().load(imageUri).into(image);
        }

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = Environment.getExternalStorageDirectory() + "/FolderAndFiles/logo.jpg";
        File file = new File(path);
        try {
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(View view) {
        file2.delete();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
