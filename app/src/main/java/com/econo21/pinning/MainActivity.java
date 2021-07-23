package com.econo21.pinning;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapReverseGeoCoder;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.econo21.pinning.ImageActivity;
import com.econo21.pinning.MypageActivity;
import com.econo21.pinning.Pin;
import com.econo21.pinning.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    private static final String LOG_TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보
    private String uid = user != null ? user.getUid():null;

    private BottomNavigationView bottomNavigationView;
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private ImageButton btn_move;
    private EditText et_id;
    private Spinner spinner;
    private MapPOIItem[] customMark;
    private ArrayList<MapPOIItem> searchMark = new ArrayList<>();
    String[] item = {"핀명", "카테고리"};
    String selected = "핀명";
    private ArrayList<Category> categoryArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = new MapView(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); //현재 자기 위치로 이동

        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

        ArrayList<Pin> pinArr = new ArrayList<>();
        db.collection("user").document(uid).collection("pin").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d("@@@", "MainActiviy: " + document.getId() + "=>" + document.getData() );
                                pinArr.add(document.toObject(Pin.class));
                            }
                            ArrayList<MapPOIItem> markerArr = new ArrayList<>();
                            for(Pin pin : pinArr){
                                Log.d("@@@", "pin - " + pin);
                                MapPOIItem marker = new MapPOIItem();
                                marker.setItemName(pin.getPin_name());
                                marker.setUserObject(new String[] {pin.getCategory(), pin.getColor()});
                                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(pin.getY()), Double.parseDouble(pin.getX())));
                                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
                                chooseImage(pin, marker); //마커 이미지 설정
                                marker.setCustomImageAutoscale(true);
                                marker.setCustomImageAnchor(0.5f, 1.0f);
                                markerArr.add(marker);
                            }
                            MapPOIItem[] toArrMarker = markerArr.toArray(new MapPOIItem[markerArr.size()]);
                            mapView.addPOIItems(toArrMarker);
                            customMark = toArrMarker;
                        }else{
                            Log.d("@@@", "MainActivity: Error getting document");
                        }

                    }
                });

        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_Plus:
                        Intent picture_select = new Intent(MainActivity.this, ImageActivity.class );
                        startActivity(picture_select);
                        break;
                    case R.id.menu_Home:
                        break;
                    case R.id.menu_Profile:
                        Intent profile = new Intent(MainActivity.this, MypageActivity.class);
                        startActivity(profile);
                        break;
                }
                return false;
            }
        });

        btn_move = findViewById(R.id.btn_move); // 현재 위치로 지도 중점을 옮기는 버튼
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkLocationServicesStatus()){
                    showDialogForLocationServiceSetting();
                }
                else{
                    checkRunTimePermission();
                }
            }
        });


        spinner = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = item[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        et_id = findViewById(R.id.et_id);
        et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MapPOIItem[] searchPOI;
                if(s.length() >= 1){
                    mapView.removeAllPOIItems();
                    Log.d("@@@","MainActivity- onTextChanged: 입력됨");
                    searchMark.clear();
                    if(selected.equals("핀명")){
                        for(MapPOIItem mItem : customMark){
                            if(mItem.getItemName().contains(s)){
                                searchMark.add(mItem);
                            }
                        }
                    }
                    else{
                        for(MapPOIItem mItem : customMark){
                            String[] d = (String[]) mItem.getUserObject();
                            if(d[0].contains(s)){
                                searchMark.add(mItem);
                            }
                        }
                    }
                    mapView.addPOIItems(searchMark.toArray(new MapPOIItem[searchMark.size()]));
                }else if(s.length() == 0){
                    mapView.removeAllPOIItems();
                    mapView.addPOIItems(customMark);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void chooseImage(Pin pin, MapPOIItem marker){
        switch (pin.getColor()){
            case "black":
                marker.setCustomImageResourceId(R.drawable.pin_black_64);
                break;
            case "gray":
                marker.setCustomImageResourceId(R.drawable.pin_gray_64);
                break;
            case "492f10":
                marker.setCustomImageResourceId(R.drawable.pin_492f10);
                break;
            case "595b83":
                marker.setCustomImageResourceId(R.drawable.pin_595b83);
                break;
            case "333456":
                marker.setCustomImageResourceId(R.drawable.pin_333456);
                break;
            case "a7c5eb":
                marker.setCustomImageResourceId(R.drawable.pin_a7c5eb);
                break;
            case "DF5E5E":
                marker.setCustomImageResourceId(R.drawable.pin_df5e5e);
                break;
            case "E98580":
                marker.setCustomImageResourceId(R.drawable.pin_e98580);
                break;
            case "FDD2BF":
                marker.setCustomImageResourceId(R.drawable.pin_fdd2bf);
                break;
            default:
                marker.setCustomImageResourceId(R.drawable.pin_blue_64);
                break;
        }
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accurancyInMeters){
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accurancy (%f)",mapPointGeo.latitude,mapPointGeo.longitude, accurancyInMeters));
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v){
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView){
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView){
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s){
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder){
        onFinishReverseGeoCoding("Fail");
    }

    public void onFinishReverseGeoCoding(String result){}

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE이고,  요청한 퍼미션 개수만큼 수신되었으면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                Log.d("@@@", "start");

                // 위치 값 가져옴
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

            } else {
                // 거부한 퍼미션이 있으면 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "퍼미션이거부되었습니다. 설정에서 퍼미션을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    void checkRunTimePermission(){
        // 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            //위치 퍼미션을 가지고 있으면 위치 값을 가져옴
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        }
        else{
            // 위치 퍼미션을 가지고 있지 않으면
            // 1. 사용자가 퍼미션을 거부한 적이 있는 경우에는 퍼미션이 필요한 이유를 설명해준 뒤 퍼미션 요청
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])){
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else{
                // 퍼미션 요청을 한 적이 없는 경우에는 퍼미션 요청
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            //요청 결과는 onRequestPermissionResult에서 수신
        }
    }

    // GPS 활성화를 위한 메소드
    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                startActivityResult.launch(callGPSSettingIntent);
            }

            ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            // GPS 활성되었는지 검사
                            if(checkLocationServicesStatus()){
                                Log.d("@@@", "onActivityResult : GPS 활성화되어있음");
                                checkRunTimePermission();
                                return;
                            }
                        }
                    });
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) getSystemService((LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    @Override
    // 현재 위치로 옮긴 뒤에 지도를 이동하면 지도가 다시 현재 위치로 이동하는 걸 막기 위해 정의한 메소드
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        Log.i("디테일로그", "onMapViewCenterPointMoved");
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter{
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter(){
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.ballon_layout, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem){
            TextView click_text;
            String[] s = (String[]) poiItem.getUserObject();
            click_text = mCalloutBalloon.findViewById(R.id.textView);
            ((TextView) mCalloutBalloon.findViewById(R.id.ball_name)).setText(poiItem.getItemName());
            ((TextView) mCalloutBalloon.findViewById(R.id.ball_address)).setText(s[0]);

            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem){
            return mCalloutBalloon;  // null을 리턴했을 때 심하게 에러가 발생하는데 이유를 찾지 못함
        }

    }
}


