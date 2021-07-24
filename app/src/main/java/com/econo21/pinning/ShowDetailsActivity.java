package com.econo21.pinning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ShowDetailsActivity extends AppCompatActivity {

    private RecyclerView pin_photo;
    private ImageView details_back;
    private EditText pin_category;
    private EditText pin_name;
    private EditText pin_contents;

    private List<String> photo;
    private String contents;
    private ArrayList<Uri> arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details_activity);

        Intent intent = getIntent();
        Pin pin = (Pin) intent.getSerializableExtra("pin");

        photo = pin.getPhoto();
        contents = pin.getContents();

        pin_photo = findViewById(R.id.pin_photo);
        pin_photo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        details_back = findViewById(R.id.details_back);
        details_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pin_category = findViewById(R.id.pin_category);
        pin_category.setText(pin.getCategory());
        pin_name = findViewById(R.id.pin_name);
        pin_name.setText(pin.getPin_name());
        pin_contents = findViewById(R.id.pin_content);
        pin_contents.setText(pin.getContents());

        if(photo != null){
            for(String s : photo){
                arr.add(Uri.parse(s));
            }
        }

        UriImageAdapter adapter = new UriImageAdapter(arr, ShowDetailsActivity.this);
        pin_photo.setAdapter(adapter);
    }
}