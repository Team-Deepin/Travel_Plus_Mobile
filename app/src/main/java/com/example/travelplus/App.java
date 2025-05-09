package com.example.travelplus;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY);
    }
}
