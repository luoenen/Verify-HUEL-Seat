package com.luosenen.huel.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.luosenen.huel.R;
import com.luosenen.huel.user.MyUser;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class PasswordActivity extends Activity {
    private EditText passwd;
    private Button change;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        passwd = findViewById(R.id.newPassword);
        change = findViewById(R.id.knowChange);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = passwd.getText().toString().trim();
                if(s.equals("")){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.passwordLayout),
                            "新密码不能为空",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                }
                updateUser(s);
            }
        });
    }
    /**
     * 更新用户操作并同步更新本地的用户信息
     */
    private void updateUser(final String view) {
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);
        user.setPassword(view);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.passwordLayout),
                            "更新密码秘文信息成功",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.passwordLayout),
                            "更新密码秘文信息失败：" + e.getMessage(),
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                }
            }
        });
    }

}
