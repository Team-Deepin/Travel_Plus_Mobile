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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.travelplus.MainActivity;
import com.example.travelplus.R;
import com.example.travelplus.network.RetrofitClient;
import com.example.travelplus.register.RegisterActivity;
import com.example.travelplus.network.ApiService;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.SharedPreferences;


public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    ImageView loginBtn;
    TextView register;
    ImageView kakaoLogin;
    Typeface font;
    ApiService apiService;
    private MockWebServer mockServer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = RetrofitClient.getInstance().create(ApiService.class);

//        apiService = new Retrofit.Builder()
//                .baseUrl("http://your-server.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(ApiService.class);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_button);
        register = findViewById(R.id.register);
        kakaoLogin = findViewById(R.id.kakao_login);
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
            finish();
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
                        LoginResponse res = response.body();
                        if (res.getResultCode() == 200) {

                            Long userId = res.getUserId();
                            if (userId == null) {
                                Toast.makeText(LoginActivity.this, "로그인 실패: userId 없음", Toast.LENGTH_SHORT).show();
                                Log.e("Login", "userId is null. 응답: " + res.getResultMessage());
                                return;
                            }
                            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            prefs.edit().putLong("userId", userId).apply();

                            // ✅ 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(LoginActivity.this,
                                    "환영합니다! " + email.getText().toString().trim() + "님!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            Log.d("Login", String.valueOf(res.getResultCode()));
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        Log.d("Login", "로그인 실패");
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
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "환영합니다! [사용자]님", Toast.LENGTH_SHORT).show();
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
            loginBtn.setImageResource(R.drawable.login_button_activate);
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setImageResource(R.drawable.login_button_deactivate);
            loginBtn.setEnabled(false);
        }
    }
    private boolean validEmail(String email){
        String emailPatern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+$";
        return email.matches(emailPatern);
    }
}
