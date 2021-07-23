package com.econo21.pinning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.econo21.pinning.location.ApiClient;
import com.econo21.pinning.location.ApiInterface;
import com.econo21.pinning.location.CategoryResult;
import com.econo21.pinning.location.Document;
import com.econo21.pinning.location.LocationAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationActivity extends AppCompatActivity {

    private static final String REST_API = "KakaoAK d8ef7c37cb5fbe0a893f1a47a869ac40";

    private EditText mSearchEdit;
    private RecyclerView recyclerView;
    private ImageView location_next, location_back;
    private ImageButton btn_clear;

    private LocationAdapter locationAdapter;
    private ArrayList<Document> documentArrayList = new ArrayList<>();

    private ArrayList<Uri> photo;
    private GpsTracker gpsTracker;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_location);
        initView();

        Intent intent = getIntent();
        if(intent.getSerializableExtra("photo") != null){
            Log.d("@@@","LocationActivity- intent.getSerializableExtra: " + intent.getSerializableExtra("photo"));
            photo = (ArrayList<Uri>)intent.getSerializableExtra("photo");
        }


        location_back=findViewById(R.id.location_back);
        location_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        location_next = findViewById(R.id.location_next);
        location_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 빈칸일 경우에는 안넘어가도록 조건문 필요
                Intent intent = new Intent(LocationActivity.this, AddActivity.class);
                if(locationAdapter.getX() == null){
                    intent.putExtra("address", mSearchEdit.getText().toString().replace("대한민국 ",""));
                    intent.putExtra("x", String.valueOf(longitude));
                    intent.putExtra("y" ,String.valueOf(latitude));
                }else{
                    intent.putExtra("address", mSearchEdit.getText().toString());
                    intent.putExtra("x", locationAdapter.getX());
                    intent.putExtra("y", locationAdapter.getY());
                }
                if(photo != null){
                    Log.d("@@@", "LocationActivity: photo != null");
                    intent.putExtra("photo", photo);
                }
                startActivity(intent);
            }
        });

        btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEdit.setText(null);
            }
        });

    }

    private void initView() {
        mSearchEdit = findViewById(R.id.location_search);
        recyclerView = findViewById(R.id.map_recyclerview);

        locationAdapter = new LocationAdapter(documentArrayList, getApplicationContext(), mSearchEdit, recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저 생성
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(locationAdapter);

        gpsTracker = new GpsTracker(LocationActivity.this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        mSearchEdit.setText(getCurrentAddress(latitude, longitude));
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    Log.d("@@@", "onTextChange : 입력됨");
                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(REST_API, charSequence.toString(), 15);

                    // API 서버에 요청
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    Log.v("Test3", document.toString());
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {
                            Log.w("LocationActivity","통신 실패");
                        }
                    });
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때
            }
        });

        mSearchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        mSearchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCurrentAddress(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try{
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        }catch (IOException e){
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_SHORT).show();
            return "지오코더 서비스 사용불가";
        }catch (IllegalArgumentException e){
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_SHORT).show();
            return "잘못된 GPS 좌표";
        }

        if(addresses == null || addresses.size() == 0){
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_SHORT).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString().trim();
    }
}