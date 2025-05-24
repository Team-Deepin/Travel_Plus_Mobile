package com.example.travelplus.login;


import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.KeyEvent;
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

import com.example.travelplus.BaseResponse;
import com.example.travelplus.MainActivity;
import com.example.travelplus.R;
import com.example.travelplus.network.RetrofitClient;
import com.example.travelplus.register.RegisterActivity;
import com.example.travelplus.network.ApiService;
import com.kakao.sdk.auth.model.OAuthToken;

import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



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
            // 콜백 메서드
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                Log.e(TAG,"CallBack Method");
                //oAuthToken != null 이라면 로그인 성공
                if(oAuthToken!=null){
                    // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                    String accessToken = oAuthToken.getAccessToken();
                    UserApiClient.getInstance().me((user, error)->{
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error);
                        } else if (user != null) {
                            String kakaoEmail = user.getKakaoAccount().getEmail();
                            String nickname = user.getKakaoAccount().getProfile().getNickname();
                            Log.d("kakao",kakaoEmail+" 이름: "+nickname);

                            if (kakaoEmail != null && nickname != null){
                                KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest(kakaoEmail, nickname);
                                Call<BaseResponse> call = apiService.kakao(kakaoLoginRequest);
                                call.enqueue(new Callback<BaseResponse>() {
                                    @Override
                                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            BaseResponse res = response.body();
                                            Log.d(TAG, res.resultMessage);
                                            if (res.resultCode == 200) {
                                                String authorization = response.headers().get("Authorization");
                                                if (authorization != null) {
                                                    SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.putString("authorization", authorization)
                                                            .putString("loginType", "kakao");
                                                    editor.apply();
                                                }
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
                                    public void onFailure(Call<BaseResponse> call, Throwable t) {
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

            String emailStr = email.getText().toString().trim();
            String pwStr = password.getText().toString().trim();

            LoginRequest request = new LoginRequest(emailStr, pwStr);

            Call<BaseResponse> call = apiService.login(request);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String authorization = response.headers().get("Authorization");
                        if (authorization != null) {
                            SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("authorization", authorization)
                                    .putString("loginType", "normal");
                            editor.apply();
                        }
                        BaseResponse res = response.body();
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
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show());
                    t.printStackTrace();
                }
            });
        });

        // 카카오 로그인 클릭
        kakaoLogin.setOnClickListener(view -> {
            Function2<OAuthToken, Throwable, Unit> loginCallback = (token, error) -> {
                if (error != null) {
                    Log.e(TAG, "카카오 로그인 실패", error);
                    Toast.makeText(LoginActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show();
                } else if (token != null) {
                    Log.d(TAG, "카카오 로그인 성공: " + token.getAccessToken());

                    // 사용자 정보 요청
                    UserApiClient.getInstance().me((user, meError) -> {
                        if (meError != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", meError);
                        } else if (user != null) {
                            String kakaoEmail = user.getKakaoAccount().getEmail();
                            String nickname = user.getKakaoAccount().getProfile().getNickname();
                            Log.d("kakao", kakaoEmail + " 이름: " + nickname);

                            // 서버에 전달
                            if (kakaoEmail != null && nickname != null){
                                KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest(kakaoEmail, nickname);
                                Call<BaseResponse> call = apiService.kakao(kakaoLoginRequest);
                                call.enqueue(new Callback<BaseResponse>() {
                                    @Override
                                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            BaseResponse res = response.body();
                                            Log.d(TAG, res.resultMessage);
                                            if (res.resultCode == 200) {
                                                String authorization = response.headers().get("Authorization");
                                                if (authorization != null) {
                                                    SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.putString("authorization", authorization)
                                                            .putString("loginType", "kakao");
                                                    editor.apply();
                                                }
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                                        "환영합니다! " + nickname + "님!", Toast.LENGTH_SHORT).show());
                                            } else {
                                                Toast.makeText(LoginActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                                        Toast.makeText(LoginActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        return null;
                    });
                }
                return Unit.INSTANCE;
            };

            if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, loginCallback);
            } else {
                UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, loginCallback);
            }
        });


    }
    private void checkInputAndSetButton() {
        String id = email.getText().toString().trim();
        String pw = password.getText().toString().trim();

        if(pw.isEmpty()){
            password.setTypeface(font);
        }else{
            password.setTypeface(Typeface.DEFAULT);
        }

        if (!id.isEmpty() && !pw.isEmpty() && validEmail(id)) {
            loginBtn.setCardBackgroundColor(ContextCompat.getColor(this,R.color.login_button));
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setCardBackgroundColor(ContextCompat.getColor(this,R.color.gray));
            loginBtn.setEnabled(false);
        }
    }
    private boolean validEmail(String email){
        String emailPatern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+$";
        return email.matches(emailPatern);
    }

}
