package com.luosenen.huel.core;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.igexin.sdk.PushManager;
import com.luosenen.huel.R;
import com.luosenen.huel.push.Push;
import com.luosenen.huel.push.PushService;
import com.luosenen.huel.user.MyUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobDialogButtonListener;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

public class MainActivity extends AppCompatActivity {
    public static String floor;
    public static String seat_X;
    public static String seat_Y;
    static int index = 0;
    private static String uid,time,isWork,seat,enemy;
    private TextView t1,t2,t3,t4,t5,out,update;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.index:
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.containLayout),
                            "已经在首页了！",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                    return true;
                case R.id.book:
                    startActivity(new Intent(getApplicationContext(),BookedActivity.class)
                            .putExtra("floor",floor)
                            .putExtra("seat_X",seat_X)
                            .putExtra("seat_Y",seat_Y));
                    return true;
                case R.id.buy:
                    startActivity(new Intent(getApplicationContext(),BuyActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PushManager.getInstance().initialize(getApplicationContext(), Push.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), PushService.class);
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                // TODO Auto-generated method stub
                //根据updateStatus来判断更新是否成功
                if (updateStatus == UpdateStatus.Yes) {//版本有更新

                }else if(updateStatus == UpdateStatus.No){
                }else if(updateStatus==UpdateStatus.EmptyField){//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                    //Toast.makeText(MainActivity.this, "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                }else if(updateStatus==UpdateStatus.IGNORED){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.containLayout),
                            "请尽快更新到最新版本",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
                    //Toast.makeText(MainActivity.this, "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                }else if(updateStatus==UpdateStatus.TimeOut){

                }
            }
        });
        if(index==0) {
            BmobUpdateAgent.update(this);
        }
        index++;
        BmobUpdateAgent.setDialogListener(new BmobDialogButtonListener() {

            @Override
            public void onClick(int status) {
                // TODO Auto-generated method stub
                switch (status) {
                    case UpdateStatus.Update:
                        //Toast.makeText(MainActivity.this, "点击了立即更新按钮" , Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.NotNow:
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.containLayout),
                                "请尽快更新到最新版本",
                                Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.WHITE);
                        View mView = snackbar.getView();
                        mView.setBackgroundColor(Color.GRAY);
                        snackbar.show();
                        break;
                    case UpdateStatus.Close://只有在强制更新状态下才会在更新对话框的右上方出现close按钮,如果用户不点击”立即更新“按钮，这时候开发者可做些操作，比如直接退出应用等
                        Snackbar snackbars = Snackbar.make(findViewById(R.id.containLayout),
                                "请尽快更新到最新版本",
                                Snackbar.LENGTH_SHORT);
                        snackbars.setActionTextColor(Color.WHITE);
                        View mViews = snackbars.getView();
                        mViews.setBackgroundColor(Color.GRAY);
                        snackbars.show();
                        break;
                }
            }
        });

        t1 = findViewById(R.id.get_uid);
        t2 = findViewById(R.id.get_isWork);
        t3 = findViewById(R.id.get_seat);
        t4 = findViewById(R.id.get_time);
        t5 = findViewById(R.id.get_enemy);
        update = findViewById(R.id.update_password);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PasswordActivity.class));
            }
        });
        out = findViewById(R.id.login_out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUser.logOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        String id = user.getObjectId();
        queryUser(id);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private void queryUser(final String id) {
        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> object, BmobException e) {
                if (e == null) {
                    for (MyUser user :
                            object) {
                        if (user.getObjectId().equals(id)){
                            floor = user.getFloor();
                            seat_X = user.getSeat_X();
                            seat_Y = user.getSeat_Y();
                            uid = user.getUsername();
                            time = user.getBookedTime();
                            seat = user.getSeat();
                            enemy = "暂无座位竞争对手";
                            isWork = user.getIsBooked();
                            if(isWork.equals("0")){
                                time = "0个月(暂未订阅任何服务)";
                                isWork = "暂未激活服务";
                                floor = "0";
                                seat_X = "0";
                                seat_Y = "0";
                            }else {
                                time = time + "个月";
                                isWork = "订阅中(到期: " + isWork + " )";
                            }
                            t1.setText(uid);
                            t2.setText(isWork);
                            t3.setText(seat);
                            t4.setText(time);
                            t5.setText(enemy);
                        }
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.containLayout),
                            "服务器用户信息获取失败",
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
