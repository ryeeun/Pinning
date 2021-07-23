package com.econo21.pinning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AddActivity extends AppCompatActivity {

    private TextView textView;
    private TextView add_complete;
    private TextView category;
    private ImageView add_back;
    private EditText pin_name, pin_content;
    private ArrayList<Uri> photo;
    private ImageButton btn_category;
    private RecyclerView category_recyclerview;
    private List<String> downloadURL;

    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    CategoryAdapter categoryAdapter;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보
    String uid = user != null ? user.getUid():null;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    CustomProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();

        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        String x = intent.getStringExtra("x");
        String y = intent.getStringExtra("y");

        if(intent.getSerializableExtra("photo") != null){
            photo = (ArrayList<Uri>)intent.getSerializableExtra("photo");
        }

        btn_category = findViewById(R.id.btn_category);
        btn_category.setOnClickListener(onClickListener);

        category = findViewById(R.id.category);
        category_recyclerview = findViewById(R.id.category_recyclerview);
        category_recyclerview.setLayoutManager(new LinearLayoutManager(AddActivity.this, LinearLayoutManager.VERTICAL,false));

        categoryAdapter = new CategoryAdapter(categoryArrayList, getApplicationContext(), category, category_recyclerview);
        category_recyclerview.setAdapter(categoryAdapter);

        pin_name = findViewById(R.id.pin_name);
        pin_content = findViewById(R.id.pin_content);

        textView = findViewById(R.id.textView);
        textView.setText(address);


        add_back = findViewById(R.id.add_back);
        add_back.setOnClickListener(onClickListener);

        dialog = new CustomProgressDialog(AddActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        add_complete = findViewById(R.id.add_complete);
        add_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                Log.d("@@@", "AddAcitivy: onClick");
                if(photo != null){
                    downloadURL = new ArrayList<>();
                    Log.d("@@@", "AddActivity: photo != null");
                    for(int i=0; i<photo.size();i++){
                        StorageReference localRef = storageRef.child(uid + "/" +photo.get(i).getLastPathSegment());
                        UploadTask uploadTask = localRef.putFile(photo.get(i));
                        int finalI = i;
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("@@@","AddActivity: 업로드 실패");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("@@@","AddActivity: 업로드 성공");

                                StorageReference pathReference = storageRef.child(uid);
                                if(pathReference != null){
                                    StorageReference download = storageRef.child(uid +"/" + photo.get(finalI).getLastPathSegment());
                                    download.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            Log.d("@@@", "AddActivity: 이미지 다운로드 성공");
                                            downloadURL.add(task.getResult().toString());
                                            if(downloadURL.size() == photo.size()){
                                                dialog.dismiss();
                                                upload(x, y, downloadURL);
                                                startActivity(new Intent(AddActivity.this, MainActivity.class));
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("@@@", "AddActivity: 이미지 다운로드 실패");
                                        }
                                    });
                                }
                            }
                        });
                    }
                    /*
                    Timer timer = new Timer(false);
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            upload(x, y, downloadURL);
                            Intent add_pin = new Intent(AddActivity.this, MainActivity.class);
                            startActivity(add_pin);
                        }
                    };
                    timer.schedule(timerTask,15000);

                     */
                }
                dialog.dismiss();
                upload(x, y, downloadURL);
                startActivity(new Intent(AddActivity.this, MainActivity.class));
            }

        });

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        boolean state = true;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_category:
                    if(state){
                        category_recyclerview.setVisibility(View.VISIBLE);
                        state = false;
                    }else{
                        category_recyclerview.setVisibility(View.GONE);
                        state = true;
                    }
                    break;
                case R.id.add_back:
                    finish();
                    break;
            }
        }
    };

    private void upload(String x, String y, List<String> result){

        Map<String, Object> pin = new HashMap<>();
        pin.put("pin_name", pin_name.getText().toString());
        pin.put("x", x);
        pin.put("y", y);
        pin.put("content", pin_content.getText().toString());
        pin.put("photo", result);
        pin.put("color", categoryAdapter.getDbColor());
        pin.put("category", categoryAdapter.getDbName());

        db.collection("user")
                .document(uid)
                .collection("pin")
                .add(pin)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("@@@","AddActivity: Pin 추가 성공");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("@@@","AddActivity: Pin 추가 실패");
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