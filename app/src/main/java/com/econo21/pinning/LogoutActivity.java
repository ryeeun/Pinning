package com.econo21.pinning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LogoutActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Button btn_logout, btn_delete;
    private ImageButton add_category;

    String s;

    FirebaseUser user = firebaseAuth.getCurrentUser(); // 로그인한 유저의 정보
    String uid = user != null ? user.getUid():null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        if(user == null){
            finish();
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
        }

        add_category = findViewById(R.id.add_category);

        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LogoutActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), StartActivity.class));

            }
        });

        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(LogoutActivity.this);
                alert_confirm.setMessage("정말 계정을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(LogoutActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(LogoutActivity.this,StartActivity.class));
                            }
                        });
                    }
                });
                alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LogoutActivity.this, "취소", Toast.LENGTH_SHORT).show();
                    }
                });
                alert_confirm.show();
            }
        });

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
                            Map<String, Object> category = new HashMap<>();
                            category.put("name", name);
                            category.put("color", color);

                            db.collection("user")
                                    .document(uid)
                                    .collection("category")
                                    .add(category)
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
                            builder.dismiss();
                        }else{
                            Toast.makeText(LogoutActivity.this, "카테고리명 또는 핀이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
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