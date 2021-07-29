package com.econo21.pinning;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ShowDetailsActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보
    private String uid = user != null ? user.getUid():null;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private RecyclerView pin_photo;
    private ImageView details_back;
    private TextView pin_category;
    private TextView pin_name;
    private TextView pin_contents;
    private TextView place_name;
    private TextView setting;
    private Button details_delete;
    private ImageButton act_more;
    private TableLayout tableLayout;
    private Button details_correct;
    private Button pin_scrap;
    private Button scrap_pin;

    private List<String> photo;
    private ArrayList<Uri> arr = new ArrayList<>();
    private String activity;
    boolean isUp = false;
    String settingCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details_activity);
        final Animation translateup = AnimationUtils.loadAnimation(ShowDetailsActivity.this, R.anim.translate_up);

        Intent intent = getIntent();
        Pin pin = (Pin) intent.getSerializableExtra("pin");
        activity = intent.getStringExtra("activity");
        init();

        if(pin.isSetting()) {
            settingCheck = "공개 핀";
        }else{
            settingCheck ="비공개 핀";
        }

        pin_photo = findViewById(R.id.pin_photo);
        pin_photo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        setting = findViewById(R.id.setting);
        setting.setText(settingCheck);
        place_name = findViewById(R.id.textView);
        place_name.setText(pin.getAddress());
        pin_category = findViewById(R.id.pin_category);
        pin_category.setText(pin.getCategory());
        pin_name = findViewById(R.id.pin_name);
        pin_name.setText(pin.getPin_name());
        pin_contents = findViewById(R.id.pin_content);
        pin_contents.setText(pin.getContents());

        photo = pin.getPhoto();
        if(photo != null){
            for(String s : photo){
                arr.add(Uri.parse(s));
            }
        }

        UriImageAdapter adapter = new UriImageAdapter(arr, ShowDetailsActivity.this);
        pin_photo.setAdapter(adapter);

        details_back = findViewById(R.id.details_back);
        details_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        act_more = findViewById(R.id.act_more);
        tableLayout = findViewById(R.id.page);

        act_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUp){
                    // 페이지 올라왔을 때 처리 부분
                    tableLayout.setVisibility(View.INVISIBLE);
                    isUp = false;
                }
                else{
                    tableLayout.setVisibility(View.VISIBLE);
                    tableLayout.startAnimation(translateup);
                    isUp = true;
                }
            }
        });

        scrap_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ShowDetailsActivity.this, ImageActivity.class);
                intent1.putExtra("activity", "ShowDetailsActivity");
                intent1.putExtra("x", pin.getX());
                intent1.putExtra("y", pin.getY());
                intent1.putExtra("address", pin.getAddress());
                startActivity(intent1);
            }
        });

        pin_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference doRef = db.collection("user").document(uid).collection("scrap").document();
                pin.setId(doRef.getId());
                doRef.set(pin).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ShowDetailsActivity.this);
                alert_confirm.setMessage("스크랩되었습니다. 마이페이지로 이동하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(ShowDetailsActivity.this, MypageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                    }
                });
                alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert_confirm.show();
            }
        });

        details_correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent correct = new Intent(ShowDetailsActivity.this, CorrectActivity.class);
                correct.putExtra("pin", pin);
                startActivity(correct);
            }

        });

        details_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ShowDetailsActivity.this);
                alert_confirm.setMessage("Pin을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(activity.equals("MypageActivity.scrap")){
                            deleteScrap(pin.getId());
                        }else{
                            deleteDoc(pin.getId(), pin.getUri());
                        }
                    }
                });
                alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                alert_confirm.show();


            }
        });


    }

    private void init(){
        details_correct = findViewById(R.id.details_correct);
        details_delete = findViewById(R.id.details_delete);
        pin_scrap = findViewById(R.id.pin_scrap);
        scrap_pin = findViewById(R.id.scrap_pin);

        if(activity.equals("NewsActivity")){
            details_correct.setVisibility(View.GONE);
            details_delete.setVisibility(View.GONE);
        }
        else if(activity.equals("MypageActivity.scrap")){
            pin_scrap.setVisibility(View.GONE);
            details_correct.setVisibility(View.GONE);
            scrap_pin.setVisibility(View.VISIBLE);
        }
        else if(activity.equals("MypageActivity.pin")){
            pin_scrap.setVisibility(View.GONE);
        }
        else if(activity.equals("MainActivity")){
            pin_scrap.setVisibility(View.GONE);
        }
    }

    private void deleteDoc(String pid, List<String> pfile){
        db.collection("user").document(uid).collection("pin").document(pid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("@@@", "ShowDetailsActivity: pin 삭제 - " + pid);
                        Intent intent;
                        if(activity.equals("MainActivity")){
                            intent = new Intent(ShowDetailsActivity.this, MainActivity.class);
                        }else{
                            intent = new Intent(ShowDetailsActivity.this, MypageActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("@@@","ShowDetailsActivity: pin 삭제 오류");
                    }
                });

        if(pfile != null){
            for(String s : pfile){
                StorageReference desertRef = storageRef.child(s);
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("@@@", "ShowDetailsActivity: storage파일 삭제 성공");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("@@@", "ShowDetailsActivity: storage파일 삭제 실패");
                    }
                });
            }
        }

        if(settingCheck.equals("공개 핀")){
            db.collection("PinFeed").document(pid).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private void deleteScrap(String pid){
        db.collection("user").document(uid).collection("scrap").document(pid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("@@@", "ShowDetailsActivity: scrap 삭제 - " + pid);
                        Intent intent;
                        if(activity.equals("MainActivity")){
                            intent = new Intent(ShowDetailsActivity.this, MainActivity.class);
                        }else{
                            intent = new Intent(ShowDetailsActivity.this, MypageActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("@@@","ShowDetailsActivity: pin 삭제 오류");
                    }
                });


    }
}