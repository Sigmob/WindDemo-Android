package com.sigmob.android.demo.natives;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;

import com.sigmob.android.demo.Constants;
import com.sigmob.android.demo.R;

public class NativeAdActivity extends Activity {

    private String placementId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        placementId = Constants.native_unified_placement_id;

        bindButton(R.id.unified_native_ad_button, NativeAdUnifiedActivity.class);
        bindButton(R.id.unified_native_ad_list_button, NativeAdUnifiedListActivity.class);
        bindButton(R.id.unified_native_ad_recycle_button, NativeAdUnifiedRecycleActivity.class);
    }

    private void bindButton(@IdRes int id, Class<?> clz) {
        findViewById(id).setOnClickListener(v -> {
            Intent intent = new Intent(NativeAdActivity.this, clz)
                    .putExtra(Constants.CONF_PLACEMENT_ID, placementId);
            startActivity(intent);
        });
    }
}