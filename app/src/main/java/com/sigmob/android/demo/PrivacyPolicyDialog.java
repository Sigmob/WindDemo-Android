package com.sigmob.android.demo;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PrivacyPolicyDialog extends Dialog {
    private static final String PRIVACY_URL = "https://doc.sigmob.com/Sigmob%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97/%E5%90%88%E8%A7%84%E6%9D%A1%E6%AC%BE/%E9%9A%90%E7%A7%81%E6%94%BF%E7%AD%96/";
    private static final String PRIVACY_SDK_HEGUI_URL = "https://doc.sigmob.com/Sigmob%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97/%E5%90%88%E8%A7%84%E6%9D%A1%E6%AC%BE/Sigmob_SDK_%E5%90%88%E8%A7%84%E6%8C%87%E5%BC%95/";
    private OnPrivacyResultListener listener;

    public interface OnPrivacyResultListener {
        void onAccept();
        void onReject();
    }

    public PrivacyPolicyDialog( Context context) {
        super(context);
        init();
    }

    public PrivacyPolicyDialog( Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_privacy_policy);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        // 设置弹框宽度为屏幕宽度的85%
        Window window = getWindow();
        if (window != null) {
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.85);
            window.setAttributes(params);
        }

        // 初始化视图
        Button btnReject = findViewById(R.id.btn_reject);
        Button btnAccept = findViewById(R.id.btn_accept);

        TextView tvPrivacyLink = findViewById(R.id.tv_privacy_link);

        // 设置隐私政策链接点击事件
        tvPrivacyLink.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL));
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "无法打开链接", Toast.LENGTH_SHORT).show();
            }
        });

        TextView tvsdkheguiLink = findViewById(R.id.tv_sdk_hegui_link);

        // 设置隐私政策链接点击事件
        tvsdkheguiLink.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_SDK_HEGUI_URL));
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "无法打开链接", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置按钮点击事件
        btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject();
            }
            dismiss();
        });

        btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept();
            }
            dismiss();
        });
    }

    public void setOnPrivacyResultListener(OnPrivacyResultListener listener) {
        this.listener = listener;
    }
}