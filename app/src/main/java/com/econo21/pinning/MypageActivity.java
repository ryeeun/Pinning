package com.econo21.pinning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.econo21.pinning.location.Document;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MypageActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser(); // 로그인한 유저의 정보
    private String uid = user != null ? user.getUid():null;

    private BottomNavigationView bottomNavigationView;
    private ImageView options;
    private TextView btn_pin;
    private TextView btn_scrap;
    private TextView pins, scrap;
    private TextView username, email;
    private RecyclerView pin_recyclerView;
    private RecyclerView scrap_recyclerView;
    private ArrayList<Pin> pinArr;
    private ArrayList<Pin> scrapArr;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mypage_recyclerview;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();;
    boolean isDown = false;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        pins = findViewById(R.id.pins);
        scrap = findViewById(R.id.scrap);
        pin_recyclerView = findViewById(R.id.recycler_view);
        pin_recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        scrap_recyclerView = findViewById(R.id.recycler_view2);
        scrap_recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mypage_recyclerview = findViewById(R.id.mypage_recyclerview);
        mypage_recyclerview.setLayoutManager(new LinearLayoutManager(MypageActivity.this, LinearLayoutManager.VERTICAL,false));
        init();
        setCategory();

        options = findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.END);
                //startActivity(new Intent(MypageActivity.this, LogoutActivity.class));
            }
        });

        btn_pin = findViewById(R.id.btn_pin);
        btn_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_pin.setBackgroundResource(R.drawable.border_bottom);
                btn_scrap.setBackgroundResource(R.drawable.round_corner);
                pin_recyclerView.setVisibility(View.VISIBLE);
                scrap_recyclerView.setVisibility(View.GONE);
            }
        });

        btn_scrap = findViewById(R.id.btn_scrap);
        btn_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_scrap.setBackgroundResource(R.drawable.border_bottom);
                btn_pin.setBackgroundResource(R.drawable.round_corner);
                pin_recyclerView.setVisibility(View.GONE);
                scrap_recyclerView.setVisibility(View.VISIBLE);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_News:
                        startActivity(new Intent(MypageActivity.this, NewsActivity.class));
                        finish();
                        break;
                    case R.id.menu_Plus:
                        Intent picture_select = new Intent(MypageActivity.this, ImageActivity.class );
                        picture_select.putExtra("activity", "MypageActivity");
                        startActivity(picture_select);
                        finish();
                        break;
                    case R.id.menu_Home:
                        startActivity(new Intent(MypageActivity.this, MainActivity.class));
                        finish();
                        break;
                    case R.id.menu_Profile:
                        break;
                }
                return false;
            }
        });

    }

    public void logout(View view){
        Toast.makeText(MypageActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
    }

    public void showRecyclerView(View view){
        if(isDown){
            mypage_recyclerview.setVisibility(View.GONE);
            isDown = false;
        }else{
            mypage_recyclerview.setVisibility(View.VISIBLE);
            isDown = true;
        }
    }

    private void init(){
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pinArr = new ArrayList<>();
        scrapArr = new ArrayList<>();

        db.collection("user").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    username.setText("     "+document.get("name").toString());
                    email.setText(document.get("email").toString());
                }else{
                    Log.d("@@@", "MypageActivity: Error getting document");
                }
            }
        });

        db.collection("user").document(uid).collection("pin").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().size()>0){
                                pins.setText(String.valueOf(task.getResult().size()));
                            }
                            for(QueryDocumentSnapshot document : task.getResult()){
                                pinArr.add(document.toObject(Pin.class));
                            }
                            ListAdapter listAdapter = new ListAdapter(pinArr, MypageActivity.this);
                            pin_recyclerView.setAdapter(listAdapter);

                            listAdapter.setOnItemClicklistener(new OnListItemClickListener() {
                                @Override
                                public void onItemClick(ListAdapter.ListViewHolder holder, View view, int position) {
                                    Pin pin = listAdapter.getItem(position);
                                    Intent intent = new Intent(MypageActivity.this, ShowDetailsActivity.class);
                                    intent.putExtra("pin", pin);
                                    intent.putExtra("activity", "MypageActivity.pin");
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Log.d("@@@", "MypageActivity: Error getting document");
                        }

                    }
                });

        db.collection("user").document(uid).collection("scrap").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().size()>0){
                                scrap.setText(String.valueOf(task.getResult().size()));
                            }
                            for(QueryDocumentSnapshot document : task.getResult()){
                                scrapArr.add(document.toObject(Pin.class));
                            }
                            ListAdapter scrapListAdapter = new ListAdapter(scrapArr, MypageActivity.this);
                            scrap_recyclerView.setAdapter(scrapListAdapter);

                            scrapListAdapter.setOnItemClicklistener(new OnListItemClickListener() {
                                @Override
                                public void onItemClick(ListAdapter.ListViewHolder holder, View view, int position) {
                                    Pin pin = scrapListAdapter.getItem(position);
                                    Intent intent = new Intent(MypageActivity.this, ShowDetailsActivity.class);
                                    intent.putExtra("pin", pin);
                                    intent.putExtra("activity", "MypageActivity.scrap");
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Log.d("@@@", "MypageActivity: Error getting document");
                        }

                    }
                });

    }

    private void setCategory(){
        TextView notShow = findViewById(R.id.notShow);
        categoryArrayList.clear();
        db.collection("user").document(uid).collection("category").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                categoryArrayList.add(document.toObject(Category.class));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryArrayList, getApplicationContext(),notShow, mypage_recyclerview);
                            mypage_recyclerview.setAdapter(categoryAdapter);

                            categoryAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                                @Override
                                public void onItemLongClick(CategoryAdapter.CategoryViewHolder holder, View view, int position) {
                                    Category category = categoryAdapter.getItem(position);
                                    deleteCategory(category);
                                }
                            });
                        }else {
                            Log.d("@@@", "MainActivity: Error getting document");
                        }
                    }
                });
    }

    private void deleteCategory(Category category){
        db.collection("user").document(uid).collection("category").document(category.getCid()).delete();
        setCategory();
        Toast.makeText(this, "카테고리 삭제", Toast.LENGTH_SHORT).show();
    }

    public void addBtnOnClicked(View view) {
        View v = getLayoutInflater().inflate(R.layout.add_category, null);
        final EditText category_name = (EditText) v.findViewById(R.id.category_name);
        final ImageButton btn1 = (ImageButton) v.findViewById(R.id.pin_black);
        final ImageButton btn2 = (ImageButton) v.findViewById(R.id.pin_gray);
        final ImageButton btn3 = (ImageButton) v.findViewById(R.id.pin_492f10);
        final ImageButton btn4 = (ImageButton) v.findViewById(R.id.pin_DF5E5E);
        final ImageButton btn5 = (ImageButton) v.findViewById(R.id.pin_595b83);
        final ImageButton btn6 = (ImageButton) v.findViewById(R.id.pin_333456);
        final ImageButton btn7 = (ImageButton) v.findViewById(R.id.pin_a7c5eb);
        final ImageButton btn8 = (ImageButton) v.findViewById(R.id.pin_E98580);
        final ImageButton btn9 = (ImageButton) v.findViewById(R.id.pin_FDD2BF);
        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
        btn5.setOnClickListener(onClickListener);
        btn6.setOnClickListener(onClickListener);
        btn7.setOnClickListener(onClickListener);
        btn8.setOnClickListener(onClickListener);
        btn9.setOnClickListener(onClickListener);



        AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("카테고리 추가")
                .setView(v)
                .setPositiveButton("완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();


        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button posBtn = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 여기서 조건에 따라 alertDialog.dismiss() 를 호출
                        String name = category_name.getText().toString();
                        String color = getColor();
                        if(!name.isEmpty() && color != ""){
                            DocumentReference docRef = db.collection("user")
                                    .document(uid)
                                    .collection("category").document();

                            Map<String, Object> category = new HashMap<>();
                            category.put("name", name);
                            category.put("color", color);
                            category.put("cid", docRef.getId());

                            docRef.set(category)
                                    .addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Log.d("@@@","LogoutActivity: Category 추가 성공");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("@@@","LogoutActivity: Category 추가 실패");
                                }
                            });
                            setCategory();
                            builder.dismiss();
                        }else{
                            Toast.makeText(MypageActivity.this, "카테고리명 또는 핀이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        builder.show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pin_black:
                    s = "black";
                    break;
                case R.id.pin_gray:
                    s = "gray";
                    break;
                case R.id.pin_492f10:
                    s = "492f10";
                    break;
                case R.id.pin_595b83:
                    s = "595b83";
                    break;
                case R.id.pin_333456:
                    s ="333456";
                    break;
                case R.id.pin_a7c5eb:
                    s = "a7c5eb";
                    break;
                case R.id.pin_DF5E5E:
                    s = "DF5E5E";
                    break;
                case R.id.pin_E98580:
                    s ="E98580";
                    break;
                case R.id.pin_FDD2BF:
                    s = "FDD2BF";
                    break;
            }
        }
    };

    public String getColor(){
        if(s == null){
            return "";
        }
        return s;
    }

}