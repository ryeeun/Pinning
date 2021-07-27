package com.econo21.pinning;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.econo21.pinning.location.Document;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorrectActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보
    String uid = user != null ? user.getUid():null;

    private Button correct_complete;
    private Button correct_back;
    private EditText pin_name;
    private EditText pin_contents;
    private ImageButton btn_category;
    private TextView category;
    private RecyclerView category_recyclerview;
    private RecyclerView photo_recyclerview;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private ArrayList<Uri> arr = new ArrayList<>();
    private List<String> photo;
    boolean state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct);
        init();

        photo_recyclerview = findViewById(R.id.pin_photo);
        photo_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Intent intent = getIntent();
        Pin pin = (Pin) intent.getSerializableExtra("pin");
        pin_name = findViewById(R.id.pin_name);
        pin_name.setText(pin.getPin_name());
        pin_contents = findViewById(R.id.pin_content);
        pin_contents.setText(pin.getContents());
        category = findViewById(R.id.category);
        String s = pin.getCategory();
        category.setText(pin.getCategory());
        photo = pin.getPhoto();
        if(photo != null){
            for(String p : photo){
                arr.add(Uri.parse(p));
            }
        }

        UriImageAdapter adapter = new UriImageAdapter(arr, this);
        photo_recyclerview.setAdapter(adapter);


        category_recyclerview = findViewById(R.id.category_recyclerview);
        category_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryArrayList, getApplicationContext(), category, category_recyclerview);
        category_recyclerview.setAdapter(categoryAdapter);

        btn_category = findViewById(R.id.btn_category);
        btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state){
                    category_recyclerview.setVisibility(View.VISIBLE);
                    state = false;
                }else{
                    category_recyclerview.setVisibility(View.GONE);
                    state = true;
                }
            }
        });

        correct_back = findViewById(R.id.correct_back);
        correct_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        correct_complete = findViewById(R.id.correct_complete);
        correct_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> cPin = new HashMap<>();
                cPin.put("pin_name", pin_name.getText().toString());
                cPin.put("contents", pin_contents.getText().toString());
                if(!category.getText().toString().equals(s)){
                    cPin.put("color", categoryAdapter.getDbColor());
                    cPin.put("category", categoryAdapter.getDbName());
                }
                DocumentReference coRef = db.collection("user").document(uid).collection("pin").document(pin.getId());
                coRef.update(cPin);
                // PinFeed도 수정
                DocumentReference feedRedf = db.collection("PinFeed").document(pin.getId());
                feedRedf.update(cPin);

                Intent intent1 = new Intent(CorrectActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);


            }
        });
    }

    private void init(){

        db.collection("user").document(uid).collection("category").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("@@@", "AddActivity: " + document.getId() + "=>" + document.getData() );
                                categoryArrayList.add(document.toObject(Category.class));
                            }
                        }else {
                            Log.d("@@@", "MainActivity: Error getting document");
                        }
                    }
                });


    }
}