package com.econo21.pinning;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.yongbeam.y_photopicker.util.photopicker.PhotoPagerActivity;
import com.yongbeam.y_photopicker.util.photopicker.PhotoPickerActivity;
import com.yongbeam.y_photopicker.util.photopicker.utils.YPhotoPickerIntent;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    ImageView imageView;
    Button btn_cancel;
    Button btn_gallery;

    public static ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageActivity.this , LocationActivity.class);
                startActivity(intent); // 액티비티 이동 : 사진->위치 화면으로

            }
        });

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_gallery = findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YPhotoPickerIntent intent = new YPhotoPickerIntent(ImageActivity.this);
                intent.setMaxSelectCount(10);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                intent.setSelectCheckBox(true);
                intent.setMaxGrideItemCount(3);
                startActivityResult.launch(intent);
            }

            ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            List<String> photos = null;
                            if(result.getResultCode() == RESULT_OK){
                                if(result.getData() != null) {
                                    photos = result.getData().getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                                }
                                if(photos != null){
                                    selectedPhotos.addAll(photos);
                                }

                                Intent startActivity = new Intent(ImageActivity.this, PhotoPagerActivity.class);
                                startActivity.putStringArrayListExtra("photos", selectedPhotos);
                                startActivity(startActivity);
                            }
                        }
                    });
        });

    }
}

