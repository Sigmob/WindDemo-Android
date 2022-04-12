package com.sigmob.android.demo.natives;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.IdRes;

import com.sigmob.android.demo.Constants;
import com.sigmob.android.demo.R;


public class NativeAdActivity extends Activity {

    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
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

    private void bindButton(@IdRes int id, final Class clz) {
        this.findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NativeAdActivity.this, clz);
                intent.putExtra("placementId", placementId);
                startActivity(intent);
            }
        });
    }

}