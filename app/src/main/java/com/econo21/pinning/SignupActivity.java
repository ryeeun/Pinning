package com.econo21.pinning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private ISessionCallback sessionCallBack;
    Session session;

    private FirebaseAuth firebaseAuth;
    EditText mName, mEmail, mPassword, mPasswordCheck;
    ImageView signup_back;
    ImageButton kakao_signup_btn;
    Button finish_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signup_back=findViewById(R.id.signup_back);
        signup_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        mName = findViewById(R.id.mName);
        mEmail = findViewById(R.id.mEmail);
        mPassword= findViewById(R.id.mPassword);
        mPasswordCheck = findViewById(R.id.mPasswordCheck);

        finish_signup=findViewById(R.id.finish_signup);
        finish_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 가입 정보 가져오기
                final String email = mEmail.getText().toString().trim();
                String pwd = mPassword.getText().toString().trim();
                String pwdcheck = mPasswordCheck.getText().toString().trim();

                if(pwd.equals(pwdcheck)){
                    Log.d("SignupActivity","등록 email:" + email + ", password: " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(SignupActivity.this);
                    mDialog.setMessage("가입중...");
                    mDialog.show();

                    // 파이어베이스에 신규계정 등록
                    firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // 가입 성공
                            if(task.isSuccessful()){
                                mDialog.dismiss();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = mName.getText().toString().trim();


                                // 해쉬맵 테이블을 파이어베이스에 저장
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid",uid);
                                hashMap.put("email", email);
                                hashMap.put("name", name);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                // 가입이 이루어지면 가입 화면을 나감
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                mDialog.dismiss();
                                Toast.makeText(SignupActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignupActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        kakao_signup_btn = findViewById(R.id.kakao_signup_btn);
        kakao_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session = Session.getCurrentSession();
                session.addCallback(sessionCallBack);
                session.open(AuthType.KAKAO_LOGIN_ALL, SignupActivity.this);
            }
        });

        sessionCallBack = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                // 로그인 요청
                UserManagement.getInstance().me(new MeV2ResponseCallback() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        // 로그인 실패
                        Toast.makeText(SignupActivity.this, "로그인 도중에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        // 세션이 닫힘
                        Toast.makeText(SignupActivity.this, "세션이 닫혔습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        String uid = result.getId()+"";
                        String email = result.getKakaoAccount().getEmail();
                        String name = result.getKakaoAccount().getProfile().getNickname();
                        String pwd = result.getKakaoAccount().getPhoneNumber();

                        firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                // 가입 성공
                                if(task.isSuccessful()){

                                    // 해쉬맵 테이블을 파이어베이스에 저장
                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("uid",uid);
                                    hashMap.put("email", email);
                                    hashMap.put("name", name);

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(uid).setValue(hashMap);

                                    // 가입이 이루어지면 가입 화면을 나감
                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(SignupActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                Toast.makeText(SignupActivity.this, "onSessionOpenFailed", Toast.LENGTH_SHORT).show();
            }
        };

        Session.getCurrentSession().addCallback(sessionCallBack);
        Session.getCurrentSession().checkAndImplicitOpen();

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallBack);
    }

}