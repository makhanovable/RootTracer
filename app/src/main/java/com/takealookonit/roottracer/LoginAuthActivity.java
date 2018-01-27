package com.takealookonit.roottracer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.takealookonit.roottracer.backend.GetUsers;
import com.takealookonit.roottracer.backend.HttpAddUser;
import com.takealookonit.roottracer.backend.models.User;
import com.takealookonit.roottracer.backend.models.UserWrapper;

import java.util.List;

public class LoginAuthActivity extends AppCompatActivity implements GetUsers.GetUserInterface {

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
        GetUsers getUsers = new GetUsers(this);
        getUsers.getUserWrapper();
    }

    //Sign up clicked
    public void signUp(View view) {
        String email = mLogin.getText().toString();
        String password = mPassword.getText().toString();
        HttpAddUser addUserDatabase = new HttpAddUser();
        addUserDatabase.execute(email, password);
    }

    @Override
    public void getUserWrapper(UserWrapper userWrapper) {
        String email = mLogin.getText().toString();
        String password = mPassword.getText().toString();
        if (userWrapper != null) {
            List<User> users = userWrapper.getUsers();
            User user = null;
            for (int i = 0; i < users.size(); i++) {
                user = users.get(i);
                if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                    Log.d("mylog", "logged in");
                    break;
                }
            }
        }
    }
}
