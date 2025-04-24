package com.example.travelplus.register;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.travelplus.Login.LoginActivity;
import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    ImageView back, duplicateCheck, registerBtn;
    TextView email, password, passwordCheck, name, checkId, checkPw;
    Typeface font;
    Boolean duplicate;
    ApiService apiService;
    private MockWebServer mockServer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupMockServer();
        back = findViewById(R.id.back_btn);
        duplicateCheck = findViewById(R.id.duplicate_check);
        registerBtn = findViewById(R.id.register_button);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        passwordCheck = findViewById(R.id.register_check_password);
        name = findViewById(R.id.register_name);
        checkId = findViewById(R.id.check_text);
        checkPw = findViewById(R.id.check_pw);
        font = ResourcesCompat.getFont(this,R.font.bmeuljirottf);
        duplicate=false;
        back.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
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
                passwordCheck.requestFocus();
                return true;
            }
            return false;
        });
        passwordCheck.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                name.requestFocus();
                return true;
            }
            return false;
        });
        name.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                registerBtn.requestFocus();
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
        passwordCheck.addTextChangedListener(textWatcher);
        name.addTextChangedListener(textWatcher);
        duplicateCheck.setOnClickListener(view -> {
            // backend에 중복하는지 보내기
            duplicate=true;
            checkId.setVisibility(TextView.VISIBLE);
            if(duplicate){
                checkId.setText("사용가능한 이메일입니다.");
            }else {
                checkId.setText("중복된 이메일입니다. 다시 시도해 주십시오.");
            }
            checkInputAndSetButton();
        });
        registerBtn.setOnClickListener(view -> {
            String emailStr = email.getText().toString().trim();
            String pwStr = password.getText().toString().trim();
            String nameStr = name.getText().toString().trim();

            RegisterRequest request = new RegisterRequest(emailStr, pwStr, nameStr);
            Call<RegisterResponse> call = apiService.register(request);
            call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    if (response.isSuccessful() && response.body() != null){
                        RegisterResponse res = response.body();
                        Log.d("Register",res.resultMessage);
                        if(res.resultCode == 200){
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            runOnUiThread(() -> Toast.makeText(RegisterActivity.this,
                                    "회원가입에 성공하였습니다!", Toast.LENGTH_SHORT).show());
                        }else {
                            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show());
                            Log.d("Register","회원가입 실패\n"+"ResultCode : "+res.resultCode+" ResultMessage : "+res.resultMessage);
                        }
                    }else {
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show());
                        Log.d("Register","회원가입 실패");
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show());
                    Log.d("Login","서버 연결 실패");
                }
            });
        });
    }
    private void checkInputAndSetButton() {
        String id = email.getText().toString().trim();
        String pw = password.getText().toString().trim();
        String pwc = passwordCheck.getText().toString().trim();
        String user_name = name.getText().toString().trim();
        Boolean same;

        if(pw.isEmpty()){
            password.setTypeface(font);
        }else{
            password.setTypeface(Typeface.DEFAULT);
        }
        if(pwc.isEmpty()){
            passwordCheck.setTypeface(font);
        }else{
            passwordCheck.setTypeface(Typeface.DEFAULT);
        }
        if(pw.isEmpty() || pwc.isEmpty() || pw.equals(pwc)){
            checkPw.setVisibility(TextView.INVISIBLE);
            same=true;
        }else{
            checkPw.setVisibility(TextView.VISIBLE);
            same=false;
        }
        if (same && !id.isEmpty() && !pw.isEmpty() && !pwc.isEmpty() && !user_name.isEmpty() && validEmail(id) && duplicate) {
            registerBtn.setImageResource(R.drawable.register_button_activate);
            registerBtn.setEnabled(true);
        } else {
            registerBtn.setImageResource(R.drawable.register_button_deactivate);
            registerBtn.setEnabled(false);
        }
    }
    private boolean validEmail(String email){
        String emailPatern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+$";
        return email.matches(emailPatern);
    }
    private void setupMockServer() {
        new Thread(() -> {
            try {
                mockServer = new MockWebServer();
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(200)
                        .setBody("{\"resultCode\":200,\"resultMessage\":\"Success\"}"));
                mockServer.start();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mockServer.url("/"))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                apiService = retrofit.create(ApiService.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
