package com.econo21.pinning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        String x = intent.getStringExtra("x");
        String y = intent.getStringExtra("y");

        textView = findViewById(R.id.textView);
        textView.setText(address);





    }
}