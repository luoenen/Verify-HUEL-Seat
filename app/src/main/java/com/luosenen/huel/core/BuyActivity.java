package com.luosenen.huel.core;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.luosenen.huel.R;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class BuyActivity extends Activity {
    private ImageView but_share;
    private Tencent tencent;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        but_share = findViewById(R.id.share);
        tencent = Tencent.createInstance("101552552",getApplicationContext());
        but_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToQQ();
            }
        });
    }
    private void shareToQQ() {
        bundle = new Bundle();
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "//shang.qq.com/wpa/qunwpa?idkey=ab5e4afee9fbb237e26b5c97379361709caa49e8d52064bed55d24486c1addb9");
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, "图书馆秒选座");
        bundle.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "http://m.laixuanzuo.com/Public/images/logo-pic1.png");
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "河南财经政法大学图书馆秒选外挂，快人一步，上帝之手！");
        bundle.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "图书馆秒选座");
        tencent.shareToQQ(this, bundle , new BaseUiListener());
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.buyLayout),
                    "分享成功",
                    Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            View mView = snackbar.getView();
            mView.setBackgroundColor(Color.GRAY);
            snackbar.show();
        }

        @Override
        public void onError(UiError uiError) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.buyLayout),
                    "分享失败",
                    Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            View mView = snackbar.getView();
            mView.setBackgroundColor(Color.GRAY);
            snackbar.show();
        }

        @Override
        public void onCancel() {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.buyLayout),
                    "取消分享",
                    Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            View mView = snackbar.getView();
            mView.setBackgroundColor(Color.GRAY);
            snackbar.show();
        }
    }
}


