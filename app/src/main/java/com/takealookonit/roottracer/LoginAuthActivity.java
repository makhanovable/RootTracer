package com.takealookonit.roottracer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.takealookonit.roottracer.backend.GetUsers;
import com.takealookonit.roottracer.backend.HttpAddUser;
import com.takealookonit.roottracer.backend.models.User;
import com.takealookonit.roottracer.backend.models.UserWrapper;
import com.takealookonit.roottracer.database.EmailDB;

import java.util.List;

public class LoginAuthActivity extends AppCompatActivity implements GetUsers.GetUserInterface {

    private SharedPreferences sharedPreferences;
    public static final int IS_AUTH_KEY = 121232;
    public static final int EMAIL = 121;

    private EditText mLogin;
    private EditText mPassword;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getPreferences(IS_AUTH_KEY);
        boolean isAuth = sharedPreferences.getBoolean("isAuth", false);
        if (isAuth) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent); // TODO anim
            overridePendingTransition(R.anim.in, R.anim.out);
        } else {
            setContentView(R.layout.activity_login_auth);
            ImageView imageView= findViewById(R.id.image);
            Picasso.with(this)
                    .load(R.drawable.back)
                    .fit()
                    .centerCrop()
                    .into(imageView);
            mLogin = findViewById(R.id.login_edit_text);
            mPassword = findViewById(R.id.password_edit_text);
        }
    }

    ProgressDialog progressDialog;
    //Sign in clicked
    public void signIn(View view) {
        GetUsers getUsers = new GetUsers(this);
        getUsers.getUserWrapper();
        progressDialog = ProgressDialog.show(this,"Loading",
                "Please wait...", true);

    }

    //Sign up
    String email;

    public void signUp(View view) {
        email = mLogin.getText().toString();
        String password = mPassword.getText().toString();
        HttpAddUser addUserDatabase = new HttpAddUser(this);
        addUserDatabase.execute(email, password);
        progressDialog = ProgressDialog.show(this,"Loading",
                "Please wait...", true);
    }

    @SuppressLint("WrongConstant")
    public void signUpResponse(int res) {
        if (res == 1) {
            sharedPreferences = getPreferences(IS_AUTH_KEY);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAuth", true);
            editor.apply();
            EmailDB database = new EmailDB(this);
            SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("email", email);
            sqLiteDatabase.insert("ema", null, values);
            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.in, R.anim.out);//TODO anim
        } else {
            Log.d("mylog", "sign up error");
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void getUserWrapper(UserWrapper userWrapper) {
        String email = mLogin.getText().toString();
        String password = mPassword.getText().toString();
        boolean no = true;
        if (userWrapper != null) {
            List<User> users = userWrapper.getUsers();
            User user = null;
            for (int i = 0; i < users.size(); i++) {
                user = users.get(i);
                if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                    Log.d("mylog", "logged in");
                    sharedPreferences = getPreferences(IS_AUTH_KEY);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isAuth", true);
                    editor.apply();
                    no = false;
                    EmailDB database = new EmailDB(this);
                    SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("email", email);
                    sqLiteDatabase.insert("ema", null, values);
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in, R.anim.out);// TODO anim
                    break;
                }
            }
            if(no)
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    }

}
