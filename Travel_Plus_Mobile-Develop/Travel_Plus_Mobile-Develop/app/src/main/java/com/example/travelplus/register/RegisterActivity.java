package com.example.travelplus.register;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.travelplus.login.LoginActivity;
import com.example.travelplus.R;
import com.example.travelplus.network.ApiService;
import com.example.travelplus.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    ImageView back, duplicateCheck, registerBtn;
    TextView email, password, passwordCheck, name, checkId, checkPw;
    Typeface font;
    boolean duplicate;
    ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        back = findViewById(R.id.back_btn);
        duplicateCheck = findViewById(R.id.duplicate_check);
        registerBtn = findViewById(R.id.register_button);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        passwordCheck = findViewById(R.id.register_check_password);
        name = findViewById(R.id.register_name);
        checkId = findViewById(R.id.check_text);
        checkPw = findViewById(R.id.check_pw);

        font = ResourcesCompat.getFont(this, R.font.bmeuljirottf);
        duplicate = false;

        back.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        email.setOnEditorActionListener((v, actionId, event) ->
                handleEditorAction(actionId, event, password));
        password.setOnEditorActionListener((v, actionId, event) ->
                handleEditorAction(actionId, event, passwordCheck));
        passwordCheck.setOnEditorActionListener((v, actionId, event) ->
                handleEditorAction(actionId, event, name));

        name.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                }
                name.clearFocus();
                return true;
            }
            return false;
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                duplicate = false;checkId.setVisibility(View.INVISIBLE);
            }
            @Override public void afterTextChanged(Editable s) {
                checkInputAndSetButton();
            }
        };
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        passwordCheck.addTextChangedListener(textWatcher);
        name.addTextChangedListener(textWatcher);

        duplicateCheck.setOnClickListener(view -> {
            String id = email.getText().toString().trim();
            DuplicateCheckRequest duplicateCheckRequest = new DuplicateCheckRequest(id);
            Call<DuplicateCheckResponse> call = apiService.duplicateCheck(duplicateCheckRequest);
            call.enqueue(new Callback<DuplicateCheckResponse>() {
                @Override
                public void onResponse(Call<DuplicateCheckResponse> call, Response<DuplicateCheckResponse> response) {
                    if (response.isSuccessful() && response.body() != null){
                        DuplicateCheckResponse res = response.body();

                        // ✅ ✅ 여기에 duplication 값을 출력하는 코드 삽입
                        checkId.setVisibility(TextView.VISIBLE);

                        if (res.resultCode == 200 && res.data != null) {
                            Boolean isDuplicated = res.data.duplication;

                            // ✅ 1번: 화면에 duplication 값 출력
                            checkId.setText("duplication = " + isDuplicated);

                            if (Boolean.TRUE.equals(isDuplicated)) {
                                checkId.append("\n중복된 이메일입니다. 다시 시도해 주십시오.");
                                duplicate = false;
                            } else {
                                checkId.append("\n사용가능한 이메일입니다.");
                                duplicate = true;
                            }
                        } else {
                            checkId.setText("응답 오류 또는 data 없음");
                            duplicate = false;
                        }
                    } else {
                        checkId.setVisibility(TextView.VISIBLE);
                        checkId.setText("response 실패");
                        duplicate = false;
                    }
                    checkInputAndSetButton();
                }

                @Override
                public void onFailure(Call<DuplicateCheckResponse> call, Throwable t) {
                    checkId.setVisibility(TextView.VISIBLE);
                    checkId.setText("네트워크 연결 실패");
                    duplicate = false;
                    checkInputAndSetButton();
                }
            });
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
                        Log.d("Register", res.resultMessage);
                        if (res.resultCode == 200){
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                            showToast("회원가입에 성공하였습니다!");
                        } else {
                            showToast("회원가입 실패");
                        }
                    } else {
                        showToast("회원가입 실패");
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    showToast("회원가입 실패");
                    Log.d("Register", "서버 연결 실패");
                }
            });
        });
    }

    private boolean handleEditorAction(int actionId, KeyEvent event, TextView nextField) {
        if (actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            nextField.requestFocus();
            return true;
        }
        return false;
    }

    private void checkInputAndSetButton() {
        String id = email.getText().toString().trim();
        String pw = password.getText().toString().trim();
        String pwc = passwordCheck.getText().toString().trim();
        String user_name = name.getText().toString().trim();
        boolean same;

        password.setTypeface(pw.isEmpty() ? font : Typeface.DEFAULT);
        passwordCheck.setTypeface(pwc.isEmpty() ? font : Typeface.DEFAULT);

        if (pw.isEmpty() || pwc.isEmpty() || pw.equals(pwc)) {
            checkPw.setVisibility(TextView.INVISIBLE);
            same = true;
        } else {
            checkPw.setVisibility(TextView.VISIBLE);
            same = false;
        }

        if (same && !id.isEmpty() && !pw.isEmpty() && !pwc.isEmpty() && !user_name.isEmpty()
                && validEmail(id) && duplicate) {
            registerBtn.setImageResource(R.drawable.register_button_activate);
            registerBtn.setEnabled(true);
        } else {
            registerBtn.setImageResource(R.drawable.register_button_deactivate);
            registerBtn.setEnabled(false);
        }
    }

    private boolean validEmail(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+$";
        return email.matches(emailPattern);
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
