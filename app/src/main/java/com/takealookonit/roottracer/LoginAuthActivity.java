package com.takealookonit.roottracer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginAuthActivity extends AppCompatActivity {

    private EditText mLogin;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth);

        mLogin = findViewById(R.id.login_edit_text);
        mPassword = findViewById(R.id.password_edit_text);
    }

    //Sign in clicked
    public void signIn(View view) {
    }

    //Sign up clicked
    public void signUp(View view) {
    }
}
