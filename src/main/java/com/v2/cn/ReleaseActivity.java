package com.v2.cn;

import android.app.Activity;
import android.os.Bundle;


public class ReleaseActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node);
		//沉浸导航栏
        getWindow().setNavigationBarColor(0);
		setTitle("放行设置");
    }
}
