package com.luosenen.huel.core;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.luosenen.huel.R;
import com.luosenen.huel.user.MyUser;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends Activity {

    private EditText userName,password;
    private ProgressBar bar;
    Button but_login,but_to_register;
    private TextView welcome_txt;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        look();
        if (MyUser.isLogin()) {
            MyUser user = BmobUser.getCurrentUser(MyUser.class);
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        but_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                String ids = userName.getText().toString().trim();
                String passwords = password.getText().toString().trim();
                if(ids.equals("")||passwords.equals("")){
                    bar.setVisibility(View.INVISIBLE);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout),
                            "必选项不能为空",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                    return;
                }
                MyUser user = new MyUser();
                user.setUsername(ids);
                user.setPassword(passwords);
                user.login(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {

                        if (e == null){
                            bar.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout),
                                    "登陆成功，请稍后订阅座位",
                                    Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(Color.WHITE);
                            View mView = snackbar.getView();
                            mView.setBackgroundColor(Color.GRAY);
                            snackbar.show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else {
                            bar.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout),
                                    "登陆失败，请检查网络设置",
                                    Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(Color.WHITE);
                            View mView = snackbar.getView();
                            mView.setBackgroundColor(Color.GRAY);
                            snackbar.show();
                        }
                    }
                });
            }
        });

        but_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistryActivity.class);
                startActivity(intent);

            }
        });

    }

    public void init(){
        userName = findViewById(R.id.login_user_name);
        password = findViewById(R.id.login_password);
        bar = findViewById(R.id.login_bar);
        but_login = findViewById(R.id.login);
        but_to_register = findViewById(R.id.to_register);
        welcome_txt = findViewById(R.id.login_text);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.font);
        welcome_txt.setTypeface(typeface);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void look(){
        int i = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (i!= PackageManager.PERMISSION_GRANTED){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout),
                    "不开启权限会导致程序无法运行",
                    Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            View mView = snackbar.getView();
            mView.setBackgroundColor(Color.GRAY);
            snackbar.show();
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE
                    ,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SET_TIME},1);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if (grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout),
                        "手机权限获取成功",
                        Snackbar.LENGTH_SHORT);
                snackbar.setActionTextColor(Color.WHITE);
                View mView = snackbar.getView();
                mView.setBackgroundColor(Color.GRAY);
                snackbar.show();
            }else {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout),
                        "手机权限获取失败",
                        Snackbar.LENGTH_SHORT);
                snackbar.setActionTextColor(Color.WHITE);
                View mView = snackbar.getView();
                mView.setBackgroundColor(Color.GRAY);
                snackbar.show();
            }
        }
    }

}
