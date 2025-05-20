package com.example.travelplus.login;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.travelplus.MainActivity;
import com.example.travelplus.R;
import com.example.travelplus.inquiry.InquireResponse;
import com.example.travelplus.network.RetrofitClient;
import com.example.travelplus.onboarding.OnboardingActivity;
import com.example.travelplus.register.RegisterActivity;
import com.example.travelplus.network.ApiService;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.model.ClientErrorCause;
import com.kakao.sdk.user.UserApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "KakaoLogin";
    EditText email, password;
    ImageView kakaoLogin;
    CardView loginBtn;
    TextView register;
    Typeface font;
    ApiService apiService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        apiService = RetrofitClient.getLoginInstance().create(ApiService.class);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_button);
        register = findViewById(R.id.register);
        kakaoLogin = findViewById(R.id.kakao_login);

        Function2<OAuthToken,Throwable, Unit> callback =new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            // 콜백 메서드 ,
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                Log.e(TAG,"CallBack Method");
                //oAuthToken != null 이라면 로그인 성공
                if(oAuthToken!=null){
                    // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                    String accessToken = oAuthToken.getAccessToken();
                    Log.d(TAG, "카카오 로그인 성공, 토큰: " + accessToken);
//                    checkAgreements(LoginActivity.this);
                    Log.d(TAG, "checkAgreements 호출됨");
                    UserApiClient.getInstance().me((user, error)->{
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error);
                        } else if (user != null) {
                            String kakaoEmail = user.getKakaoAccount().getEmail();
                            String nickname = user.getKakaoAccount().getProfile().getNickname();

                            if (kakaoEmail != null && nickname != null){
                                KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest(accessToken, kakaoEmail, nickname);
                                Call<KakaoResponse> call = apiService.kakao(kakaoLoginRequest);
                                call.enqueue(new Callback<KakaoResponse>() {
                                    @Override
                                    public void onResponse(Call<KakaoResponse> call, Response<KakaoResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            KakaoResponse res = response.body();
                                            Log.d(TAG, res.resultMessage);
                                            if (res.resultCode == 200) {
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                                        "환영합니다! "+nickname+"님!", Toast.LENGTH_SHORT).show());
                                            }else {
                                                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show());
                                                Log.d(TAG, String.valueOf(res.resultCode));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<KakaoResponse> call, Throwable t) {
                                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show());
                                        t.printStackTrace();
                                    }
                                });
                            }else {
                                Log.d(TAG, "이메일, 닉네임 가져오기 실패");
                            }
                        }
                        return null;
                    });
                }else {
                    Log.e(TAG, "invoke: login fail" );
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                            "카카오 로그인 실패", Toast.LENGTH_SHORT).show());
                }
                return null;
            }
        };

        font = ResourcesCompat.getFont(this,R.font.bmeuljirottf);
        loginBtn.setEnabled(false);
        email.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                password.requestFocus();
                return true;
            }
            return false;
        });
        password.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                loginBtn.requestFocus();
                return true;
            }
            return false;
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkInputAndSetButton();
            }
        };
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 로그인 버튼 클릭
        loginBtn.setOnClickListener(view -> {

            // 사용자 입력 가져오기
            String emailStr = email.getText().toString().trim();
            String pwStr = password.getText().toString().trim();

            // 요청 객체 생성
            LoginRequest request = new LoginRequest(emailStr, pwStr);

            // Retrofit API 호출
            Call<LoginResponse> call = apiService.login(request);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String authorization = response.headers().get("Authorization");
                        if (authorization != null) {
                            SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("authorization", authorization);
                            editor.apply();
                            Log.d("Login", "저장 완료: " + authorization);
                        }
                        LoginResponse res = response.body();
                        Log.d("Login",res.resultMessage);
                        if (res.resultCode == 200) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (res.resultCode == 401) {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show());
                            Log.d("Login",String.valueOf(res.resultCode));
                        }else {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show());
                            Log.d("Login",String.valueOf(res.resultCode));
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show());
                        Log.d("Login","로그인 실패");
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show());
                    t.printStackTrace();
                }
            });
        });

        // 카카오 로그인 클릭
        kakaoLogin.setOnClickListener(view -> {
//            Intent intent = new Intent(this, KakaoLoginActivity.class);
//            startActivity(intent);

            if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
                    @Override
                    public Unit invoke(OAuthToken token, Throwable error) {
                        if (error != null) {
                            Log.e(TAG, "카카오톡으로 로그인 실패", error);

                            // 카카오톡 로그인 취소한 경우
                            if (error instanceof ClientError &&
                                    ((ClientError) error).getReason() == ClientErrorCause.Cancelled) {
                                // 사용자가 명시적으로 취소한 것 -> 아무 처리 안 함
                                return Unit.INSTANCE;
                            }

                            // 실패 시 카카오계정 로그인으로 대체
                            UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                        } else if (token != null) {
                            Log.i(TAG, "카카오톡으로 로그인 성공 " + token.getAccessToken());
//                            checkAgreements(LoginActivity.this);
                            Log.d(TAG, "checkAgreements 호출됨");
                        }
                        return Unit.INSTANCE;
                    }
                });
            } else {
                UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
//                checkAgreements(LoginActivity.this);
                Log.d(TAG, "checkAgreements 호출됨");
            }

        });

    }
//    private void checkAgreements(Context context){
//        Log.d(TAG, "checkAgreements() 함수 실행 시작");
//        UserApiClient.getInstance().me(new Function2<com.kakao.sdk.user.model.User, Throwable, Unit>() {
//            @Override
//            public Unit invoke(com.kakao.sdk.user.model.User user, Throwable error) {
//                if (error != null) {
//                    Log.e(TAG, "사용자 정보 요청 실패", error);
//                } else if (user != null) {
//                    List<String> scopes = new ArrayList<>();
//
//                    if (user.getKakaoAccount().getEmailNeedsAgreement() == Boolean.TRUE) scopes.add("account_email");
//                    if (user.getKakaoAccount().getBirthdayNeedsAgreement() == Boolean.TRUE) scopes.add("birthday");
//                    if (user.getKakaoAccount().getBirthyearNeedsAgreement() == Boolean.TRUE) scopes.add("birthyear");
//                    if (user.getKakaoAccount().getGenderNeedsAgreement() == Boolean.TRUE) scopes.add("gender");
//                    if (user.getKakaoAccount().getPhoneNumberNeedsAgreement() == Boolean.TRUE) scopes.add("phone_number");
//                    if (user.getKakaoAccount().getProfileNeedsAgreement() == Boolean.TRUE) scopes.add("profile");
//                    if (user.getKakaoAccount().getAgeRangeNeedsAgreement() == Boolean.TRUE) scopes.add("age_range");
//
//                    if (!scopes.isEmpty()) {
//                        Log.d(TAG, "사용자에게 추가 동의를 받아야 합니다.");
//
//                        UserApiClient.getInstance().loginWithNewScopes(context, scopes, null, new Function2<OAuthToken, Throwable, Unit>() {
//                            @Override
//                            public Unit invoke(OAuthToken token, Throwable error) {
//                                if (error != null) {
//                                    Log.e(TAG, "사용자 추가 동의 실패", error);
//                                } else {
//                                    Log.d(TAG, "allowed scopes: " + token.getScopes());
//                                    // 재요청
//                                    UserApiClient.getInstance().me(new Function2<com.kakao.sdk.user.model.User, Throwable, Unit>() {
//                                        @Override
//                                        public Unit invoke(com.kakao.sdk.user.model.User user, Throwable error) {
//                                            if (error != null) {
//                                                Log.e(TAG, "사용자 정보 재요청 실패", error);
//                                            } else if (user != null) {
//                                                Log.i(TAG, "사용자 정보 재요청 성공");
//                                            }
//                                            return Unit.INSTANCE;
//                                        }
//                                    });
//                                }
//                                return Unit.INSTANCE;
//                            }
//                        });
//                    }
//                }
//                return Unit.INSTANCE;
//            }
//        });
//    }
    private void checkInputAndSetButton() {
        String id = email.getText().toString().trim();
        String pw = password.getText().toString().trim();

        if(pw.isEmpty()){
            password.setTypeface(font);
        }else{
            password.setTypeface(Typeface.DEFAULT);
        }

        if (!id.isEmpty() && !pw.isEmpty() && validEmail(id)) {
//            loginBtn.setImageResource(R.drawable.login_button_activate);
            loginBtn.setCardBackgroundColor(ContextCompat.getColor(this,R.color.login_button));
            loginBtn.setEnabled(true);
        } else {
//            loginBtn.setImageResource(R.drawable.login_button_deactivate);
            loginBtn.setCardBackgroundColor(ContextCompat.getColor(this,R.color.gray));
            loginBtn.setEnabled(false);
        }
    }
    private boolean validEmail(String email){
        String emailPatern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+$";
        return email.matches(emailPatern);
    }
}
