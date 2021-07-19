package com.econo21.pinning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;


import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

   // private ISessionCallback sessionCallBack;
   // Session session;

    private FirebaseAuth firebaseAuth;
    private ImageView kakao_login_btn;
    private ImageView login_back;
    private EditText mEmail, mPassword;
    private Button finish_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        finish_login = findViewById(R.id.finish_login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        finish_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String pwd = mPassword.getText().toString().trim();
                firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        login_back = findViewById(R.id.login_back);
        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        session = Session.getCurrentSession();
        session.addCallback(sessionCallBack);


        kakao_login_btn = findViewById(R.id.kakao_login_btn);
        kakao_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });

         */

        /*
        sessionCallBack = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                // 로그인 요청
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        // 로그인 실패
                        Toast.makeText(LoginActivity.this, "로그인 도중에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        // 세션이 닫힘
                        Toast.makeText(LoginActivity.this, "세션이 닫혔습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("name", result.getKakaoAccount().getProfile().getNickname());
                        intent.putExtra("profileImg", result.getKakaoAccount().getProfile().getProfileImageUrl());
                        intent.putExtra("email",result.getKakaoAccount().getEmail());
                        startActivity(intent);

                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                Toast.makeText(LoginActivity.this, "onSessionOpenFailed", Toast.LENGTH_SHORT).show();
            }
        };


        Session.getCurrentSession().addCallback(sessionCallBack);
        Session.getCurrentSession().checkAndImplicitOpen();
        */
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //Session.getCurrentSession().removeCallback(sessionCallBack);
    }
}