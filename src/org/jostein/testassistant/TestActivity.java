package org.jostein.testassistant;

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
        intent.setClassName("org.jostein.testassistant", "org.jostein.testassistant.HomeActivity");
        startActivity(intent);
    }
    
}
