package com.econo21.pinning;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;


public class GlobalApplication extends MultiDexApplication {

    private static volatile GlobalApplication instance = null;

    private static class KakaoSDKAdapter extends KakaoAdapter{
        public ISessionConfig getSessionConfig(){
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Nullable
                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig(){
            return new IApplicationConfig(){
                @Override
                public Context getApplicationContext(){
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }
    }

    public static GlobalApplication getGlobalApplicationContext(){
        if(instance == null){
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        KakaoSDK.init(new KakaoSDKAdapter());
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        instance = null;
    }

}
