package com.example.travelplus;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.travelplus.Login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {
    ImageView back, dupliacteCheck, registerBtn;
    TextView email, password, passwordCheck, name, checkId, checkPw;
    Typeface font;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        back = findViewById(R.id.back_btn);
        dupliacteCheck = findViewById(R.id.duplicate_check);
        registerBtn = findViewById(R.id.register_button);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        passwordCheck = findViewById(R.id.register_check_password);
        name = findViewById(R.id.register_name);
        checkId = findViewById(R.id.check_text);
        checkPw = findViewById(R.id.check_pw);
        font = ResourcesCompat.getFont(this,R.font.bmeuljirottf);
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
        dupliacteCheck.setOnClickListener(view -> {
            // backend에 중복하는지 보내기
            checkId.setVisibility(TextView.VISIBLE);
        });
        registerBtn.setOnClickListener(view -> {
            // backend에 정보 보내고 로그인 화면으로 가기
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
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
        if (same && !id.isEmpty() && !pw.isEmpty() && !pwc.isEmpty() && !user_name.isEmpty() && validEmail(id)) {
            registerBtn.setImageResource(R.drawable.register_button_activate);
            registerBtn.setClickable(true);
            registerBtn.setEnabled(true);
        } else {
            registerBtn.setImageResource(R.drawable.register_button_deactivate);
            registerBtn.setClickable(false);
            registerBtn.setEnabled(false);
        }
    }
    private boolean validEmail(String email){
        String emailPatern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+$";
        return email.matches(emailPatern);
    }
}
