package com.econo21.pinning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.List;

public class MypageActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보
    private String uid = user != null ? user.getUid():null;

    private BottomNavigationView bottomNavigationView;
    private ImageView options;
    private RecyclerView recyclerView;
    private ArrayList<Pin> pinArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        init();


        options = findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MypageActivity.this, LogoutActivity.class));
            }
        });


        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_Plus:
                        Intent picture_select = new Intent(MypageActivity.this, ImageActivity.class );
                        startActivity(picture_select);
                        finish();
                        break;
                    case R.id.menu_Home:
                        Intent profile = new Intent(MypageActivity.this, MainActivity.class);
                        startActivity(profile);
                        finish();
                        break;
                    case R.id.menu_Profile:
                        break;
                }
                return false;
            }
        });

    }

    private void init(){
        pinArr = new ArrayList<>();
        db.collection("user").document(uid).collection("pin").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                pinArr.add(document.toObject(Pin.class));
                            }
                            ListAdapter listAdapter = new ListAdapter(pinArr, MypageActivity.this);
                            recyclerView.setAdapter(listAdapter);

                            listAdapter.setOnItemClicklistener(new OnListItemClickListener() {
                                @Override
                                public void onItemClick(ListAdapter.ListViewHolder holder, View view, int position) {
                                    Pin pin = listAdapter.getItem(position);
                                    Intent intent = new Intent(MypageActivity.this, ShowDetailsActivity.class);
                                    intent.putExtra("pin", pin);
                                    intent.putExtra("activity", "MypageActivity");
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Log.d("@@@", "MypageActivity: Error getting document");
                        }

                    }
                });
    }
}