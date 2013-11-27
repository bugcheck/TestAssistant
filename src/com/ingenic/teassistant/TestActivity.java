package com.ingenic.teassistant;

import android.content.Intent;
import android.os.Bundle;

public class TestActivity extends BaseActivity {
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
    }

    @Override
    protected void slideToNextActivity() {
    }

    @Override
    protected void slideToPreviousActivity() {
        Intent intent = new Intent();
        intent.setClassName("com.ingenic.teassistant", "com.ingenic.teassistant.HomeActivity");
        startActivity(intent);
    }
    
}
