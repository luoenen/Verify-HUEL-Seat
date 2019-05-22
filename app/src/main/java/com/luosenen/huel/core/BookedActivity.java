package com.luosenen.huel.core;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.igexin.sdk.PushManager;
import com.instacart.library.truetime.TrueTime;
import com.instacart.library.truetime.TrueTimeRx;
import com.luosenen.huel.R;
import com.luosenen.huel.push.Push;
import com.luosenen.huel.push.PushService;
import com.luosenen.huel.utils.MyUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BookedActivity extends Activity {

    private EditText cookie;
    private Button but_start;
    private TextView showResult;
    private TextView showTimer;
    public static String js, jsCode;
    public static String result;
    public static String floor, seat_X, seat_Y;
    private double count = 0;
    private TextView web, software,teach,google;
    private TextView toKnow;
    private static Date goal;
    private TextView say;
    private ImageView  mImageView;
    private static String childThreadCookie;

    public static String booked_url = "http://wechat.laixuanzuo.com/index.php/prereserve/save/libid=";
    public static String index_url = "";
    public static String times = String.valueOf(System.currentTimeMillis());
    public static Map<String,String> header;
    public static Map<String,String> cv;
    public static long currtime;



    Timer timer = new Timer();

    private String imgPath="https://wechat.laixuanzuo.com/index.php/misc/verify?"+System.currentTimeMillis();
    private File cache;//缓存路径
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //显示
            Bitmap b=(Bitmap)msg.obj;
            mImageView.setImageBitmap(b);
            //保存至本地
            File imgFile=new File(cache,"验证码.jpg");
            try {
                BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(imgFile));
                b.compress(Bitmap.CompressFormat.JPEG,80,bos);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);
        PushManager.getInstance().initialize(getApplicationContext(), Push.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), PushService.class);
        getInfo();
        init();

        //创建缓存路径
        //Environment.getExternalStorageDirectory()获取手机内存卡根路径
        cache=new File(Environment.getExternalStorageDirectory(),"来选座验证码");
        if(!cache.exists()){
            cache.mkdirs();
        }
        //耗时操作都要放在子线程操作
        //开启子线程获取输入流
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn=null;
                InputStream is=null;
                try {
                    URL url=new URL(imgPath);
                    //开启连接
                    conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Host","wechat.laixuanzuo.com");
                    conn.setRequestProperty("Accept","image/png,image/svg+xml,image/*;q=0.8,video/*;q=0.8,*/*;q=0.5");
                    conn.setRequestProperty("Connection","keep-alive");
                    conn.setRequestProperty("Cookie","");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) MicroMessenger/2.3.24(0x12031810) MacWechat NetType/WIFI WindowsWechat MicroMessenger/2.3.24(0x12031810) MacWechat NetType/WIFI WindowsWechat");
                    conn.setRequestProperty("Accept-Language","zh-cn");
                    conn.setRequestProperty("Referer","");
                    conn.setRequestProperty("Accept-Encoding","br, gzip, deflate");

                    //设置连接超时
                    conn.setConnectTimeout(5000);
                    //设置请求方式
                    conn.setRequestMethod("GET");
                    //conn.connect();
                    if(conn.getResponseCode()==200){
                        is=conn.getInputStream();
                        Bitmap b= BitmapFactory.decodeStream(is);
                        //把输入流转化成bitmap格式，以msg形式发送至主线程
                        Message msg=new Message();
                        msg.obj=b;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    try {
                        //用完记得关闭
                        is.close();
                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),WebActivity.class));
            }
        });

        //开启
        but_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (floor.equals("0") || seat_Y.equals("0") || seat_X.equals("0")) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.bookedLayout),
                            "您暂未订阅秒选服务，请线联系微信：19939374645订阅服务",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                    return;
                }
                String cookies = cookie.getText().toString().trim();
                if(cookies.equals("")){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.bookedLayout),
                            "Cookie 抓包上方输入框信息必填",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                    return;
                }

                Date getTime = new Date();
                Date judgement = new Date();
                judgement.setMinutes(48);
                if(getTime.getMinutes()<judgement.getMinutes()){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.bookedLayout),
                            "为保证秒选有效，软件判定选座为7:48开放秒选!,请等待到7:48",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                    return;
                }
                but_start.setEnabled(false);
                cookie.setFocusable(false);
                cookie.setFocusableInTouchMode(false);
                try {
                    childThreadCookie = cookies;
                    ChildThread child = new ChildThread();
                    Thread thread = new Thread(child);
                    thread.start();
                    thread.join(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showResult.setText("等待系统获取验证码...");
                if (js == null) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.bookedLayout),
                            "验证系统失败，请返回重试",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    View mView = snackbar.getView();
                    mView.setBackgroundColor(Color.GRAY);
                    snackbar.show();
                    but_start.setEnabled(true);
                    cookie.setFocusable(true);
                    cookie.setFocusableInTouchMode(true);
                    showResult.setText("验证系统失败，请返回重试");
                    return;
                }
                Log.i("myjs",js);
                showResult.setText("暂未开始抢座");
                Date now = new Date();
                goal = new Date();
                goal.setHours(now.getHours());
                goal.setMinutes(50);
                goal.setSeconds(0);
                count = (((goal.getTime()-now.getTime()))/1000);
                timer.schedule(task, 100, 100);
            }
        });
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bilibili.com/video/av44994621/")));
            }
        });
        software.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://raw.githubusercontent.com/luoenen/HUELibraryCourse/master/package.apk")));
            }
        });
        teach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.bilibili.com/video/av50764673/")));
            }
        });
        toKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(BookedActivity.this);
                localBuilder.setTitle("⚠️选座操作必要须知：");
                localBuilder.setMessage("\t\t\t\t1.在选座开始前1分钟务必完成本操作。\n\t\t\t\t2.选座抓包教程和抓包工具在下方，通过点击查看自主学习，本程序" +
                        "只保障同学们能够按操作选到位置，并非替同学们选座，而是提供计秒选速度，一定要自主学习。\n\t\t\t\t3.抓包后输入上方输入框等待提示" +
                        "信息，提示成功则点上方选座按钮，提示失败则返回重试。\n\t\t\t\t4.切记：在完成上述操作后，请务必等待下方计时结束再启动'微信来选座'，在计时期间，" +
                        "一定不要进入微信来选座公众号，不要刷新页面，否则一定会导致选座失败，没有例外，切记切记。\n\t\t\t\t5.请联系19939374645订阅选座服务，一旦订阅成功，开发者持有最终解释权，如无明显Bug概不退换任何费用，放弃支持则退回客户对应订阅费用。");
                localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                    {
                        /**
                         * 确定操作
                         * */
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.bookedLayout),
                                "已知晓",
                                Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.WHITE);
                        View mView = snackbar.getView();
                        mView.setBackgroundColor(Color.GRAY);
                        snackbar.show();
                    }
                });
                localBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                    {
                        /**
                         * 确定操作
                         * */
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.bookedLayout),
                                "详细反复阅读",
                                Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.WHITE);
                        View mView = snackbar.getView();
                        mView.setBackgroundColor(Color.GRAY);
                        snackbar.show();
                    }
                });

                /***
                 * 设置点击返回键不会消失
                 * */
                localBuilder.setCancelable(false).create();

                localBuilder.show();
            }
        });

    }
    public void init() {
        cookie = findViewById(R.id.weChat_cookie);
        but_start = findViewById(R.id.start);
        showResult = findViewById(R.id.result);
        showTimer = findViewById(R.id.timer);
        web = findViewById(R.id.get_cookie);
        web.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        software = findViewById(R.id.get_software);
        software.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        teach = findViewById(R.id.teach);
        teach.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        toKnow = findViewById(R.id.know);
        toKnow.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        say = findViewById(R.id.mySay);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.font);
        say.setTypeface(typeface);
        google = findViewById(R.id.webLayout);
        mImageView = findViewById(R.id.verify);
    }

    public void getJsCode(final String cookie) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    js = MyUtils.getJs(index_url, cookie);
                    Log.i("js",js);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void booked(final String cookie) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = booked_url + floor + "&" + jsCode + "=" + seat_X + "," + seat_Y + "&yzm=";
                    result = MyUtils.netWork(url, cookie);
                    final String results = new String(result.getBytes());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showResult.setText(results);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    count =(count-0.1);
                    String cookies = cookie.getText().toString().trim();
                    if (count <= 0) {
                        try {
                            childThreadCookie = cookies;
                            Begin begin = new Begin();
                            Thread thread = new Thread(begin);
                            thread.start();
                            thread.join(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        header = new HashMap<>();
                        header.put("Host","wechat.laixuanzuo.com");
                        header.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                        header.put("Connection","keep-alive");
                        header.put("Cookie","Hm_lpvt_7838cef374eb966ae9ff502c68d6f098=+"+currtime+"; Hm_lvt_7838cef374eb966ae9ff502c68d6f098="+currtime+"; FROM_TYPE=weixin; wechatSESS_ID="+childThreadCookie);
                        header.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/605.1.15 (KHTML, like Gecko) MicroMessenger/2.3.24(0x12031810) MacWechat NetType/WIFI WindowsWechat");
                        header.put("Accept-Language","zh-cn");
                        header.put("Referer","https://wechat.laixuanzuo.com/index.php/prereserve/index.html");
                        header.put("Accept=Encoding","br, gzip, deflate");
                        jsCode = MyUtils.cv(js.replace(".js", ""));
                        Log.i("mytime", String.valueOf(new Date()));
                        booked(cookies);
                        Date d = new Date();
                        if (d.getMinutes()>=50){
                            booked(cookies);
                        }else {
                            try {
                                Thread.sleep(500);
                                booked(cookies);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        showResult.setText(result);
                        timer.cancel();
                        showTimer.setText("请稍后进入来选座结果查看");
                    }else {
                        showTimer.setText(""+count + "秒");
                    }
                }
            });
        }
    };

    public void getInfo() {
        Intent intent = getIntent();
        floor = intent.getStringExtra("floor");
        seat_X = intent.getStringExtra("seat_X");
        seat_Y = intent.getStringExtra("seat_Y");
        index_url = "http://wechat.laixuanzuo.com/index.php/reserve/layoutApi/action=prereserve_event&libid="+floor;
    }

    class ChildThread implements Runnable {

        @Override
        public void run() {
            try {
                header = new HashMap<>();
                header.put("Host","wechat.laixuanzuo.com");
                header.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                header.put("Connection","keep-alive");
                header.put("Cookie","Hm_lpvt_7838cef374eb966ae9ff502c68d6f098=+"+System.currentTimeMillis()+"; Hm_lvt_7838cef374eb966ae9ff502c68d6f098="+System.currentTimeMillis()+"; FROM_TYPE=weixin; wechatSESS_ID="+childThreadCookie);
                header.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/605.1.15 (KHTML, like Gecko) MicroMessenger/2.3.24(0x12031810) MacWechat NetType/WIFI WindowsWechat");
                header.put("Accept-Language","zh-cn");
                header.put("Referer","https://wechat.laixuanzuo.com/index.php/prereserve/index.html");
                header.put("Accept=Encoding","br, gzip, deflate");
                js = MyUtils.getJs("https://wechat.laixuanzuo.com/index.php/reserve/index.html", (HashMap<String, String>) header);
                Log.i("js",js);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Begin implements Runnable {

        @Override
        public void run() {
            currtime = System.currentTimeMillis();
            try {
                header = new HashMap<>();
                header.put("Host","wechat.laixuanzuo.com");
                header.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                header.put("Connection","keep-alive");
                header.put("Cookie","Hm_lpvt_7838cef374eb966ae9ff502c68d6f098=+"+currtime+"; Hm_lvt_7838cef374eb966ae9ff502c68d6f098="+currtime+"; FROM_TYPE=weixin; wechatSESS_ID="+childThreadCookie);
                header.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/605.1.15 (KHTML, like Gecko) MicroMessenger/2.3.24(0x12031810) MacWechat NetType/WIFI WindowsWechat");
                header.put("Accept-Language","zh-cn");
                header.put("Referer","https://wechat.laixuanzuo.com/index.php/prereserve/index.html");
                header.put("Accept=Encoding","br, gzip, deflate");
                js = MyUtils.getJs(index_url, (HashMap<String, String>) header);
                Log.i("js",js);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}