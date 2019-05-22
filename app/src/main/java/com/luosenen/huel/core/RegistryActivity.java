package com.luosenen.huel.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.luosenen.huel.R;
import com.luosenen.huel.user.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegistryActivity extends Activity {

    private EditText userName,password;
    private ProgressBar bar;
    public Button but_registry;
    private TextView registry_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        init();

        but_registry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                String ids = userName.getText().toString().trim();
                String passwords = password.getText().toString().trim();
                if(ids.equals("")||passwords.equals("")){
                    bar.setVisibility(View.INVISIBLE);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.registryLayout),
                            "必选项不能为空",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                    return;
                }
                final MyUser user = new MyUser();
                user.setUsername(ids);
                user.setBookedTime("0");
                user.setFloor("0");
                user.setSeat_X("0");
                user.setSeat_Y("0");
                user.setIsBooked("0");
                user.setPassword(passwords);
                user.signUp(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        if (e == null){
                            bar.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.registryLayout),
                                    "注册成功，请稍后订阅座位",
                                    Snackbar.LENGTH_SHORT);
                            snackbar.setActionTextColor(Color.WHITE);
                            View mView = snackbar.getView();
                            mView.setBackgroundColor(Color.GRAY);
                            snackbar.show();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }else {
                            bar.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.registryLayout),
                                    "注册失败，请检查网络设置",
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
    }

    public void init() {
        userName = findViewById(R.id.registry_user_name);
        password = findViewById(R.id.registry_password);
        bar = findViewById(R.id.registry_bar);
        but_registry = findViewById(R.id.registry);
        registry_text = findViewById(R.id.registry_text);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.font);
        registry_text.setTypeface(typeface);
    }
}
