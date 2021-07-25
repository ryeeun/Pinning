package com.econo21.pinning;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.Manifest.permission;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class ImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Uri> imageList;  // URI

    private ImageView image_next;
    private ImageView image_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        init();

        image_next = findViewById(R.id.image_next);
        image_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageActivity.this, LocationActivity.class);
                if(imageList != null){
                    Log.d("@@@", "ImageActivity: imageList != null");
                    Log.d("@@@", "ImageActivity- imageList: " + imageList);
                    intent.putExtra("photo", imageList);
                }
                startActivity(intent);
            }
        });

        image_back = findViewById(R.id.image_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageActivity.this, MainActivity.class));
                finish();
            }
        });


    }

    private void init(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityResult.launch(intent);
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result != null){
                        if(result.getData().getClipData() != null){
                            ClipData clipData = result.getData().getClipData();
                            if(clipData.getItemCount() >= 10){
                                Toast.makeText(ImageActivity.this, "사진은 10개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }else if(clipData.getItemCount() == 1){
                                Uri filePath = clipData.getItemAt(0).getUri();
                                imageList = new ArrayList<>();
                                imageList.add(filePath);
                            }else if(clipData.getItemCount() > 1 && clipData.getItemCount() < 10){
                                imageList = new ArrayList<>();
                                for(int i =0 ; i<clipData.getItemCount(); i++){
                                    imageList.add(clipData.getItemAt(i).getUri());
                                }
                            }
                        }

                        UriImageAdapter adapter = new UriImageAdapter(imageList, ImageActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
}