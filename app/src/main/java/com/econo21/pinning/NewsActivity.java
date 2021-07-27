package com.econo21.pinning;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.nio.file.Watchable;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Pin> pinArr;
    private ArrayList<Pin> searchArr = new ArrayList<>();
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        recyclerView = findViewById(R.id.pinfeed_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        init();

        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_News:
                        break;
                    case R.id.menu_Plus:
                        Intent picture_select = new Intent(NewsActivity.this, ImageActivity.class );
                        picture_select.putExtra("activity", "NewsActivity");
                        startActivity(picture_select);
                        break;
                    case R.id.menu_Home:
                        startActivity(new Intent(NewsActivity.this, MainActivity.class));
                        finish();
                        break;
                    case R.id.menu_Profile:
                        Intent profile = new Intent(NewsActivity.this, MypageActivity.class);
                        startActivity(profile);
                        finish();
                        break;
                }
                return false;
            }
        });

        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchArr.clear();
                if(s.length() == 0) {
                    setRecyclerView(pinArr);
                }else{
                    for(Pin pin : pinArr){
                        if(pin.getPin_name().contains(s) || pin.getCategory().contains(s)){
                            searchArr.add(pin);
                        }
                    }
                    setRecyclerView(searchArr);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
        pinArr = new ArrayList<>();
        db.collection("PinFeed").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                pinArr.add(document.toObject(Pin.class));
                            }
                            setRecyclerView(pinArr);
                        }else{
                            Log.d("@@@", "MypageActivity: Error getting document");
                        }

                    }
                });


    }

    private void setRecyclerView(ArrayList<Pin> arr){
        ListAdapter listAdapter = new ListAdapter(arr, NewsActivity.this);
        recyclerView.setAdapter(listAdapter);

        listAdapter.setOnItemClicklistener(new OnListItemClickListener() {
            @Override
            public void onItemClick(ListAdapter.ListViewHolder holder, View view, int position) {
                Pin pin = listAdapter.getItem(position);
                Intent intent = new Intent(NewsActivity.this, ShowDetailsActivity.class);
                intent.putExtra("pin", pin);
                intent.putExtra("activity", "NewsActivity");
                startActivity(intent);
            }
        });
    }
}