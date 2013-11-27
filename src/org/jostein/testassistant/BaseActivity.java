package org.jostein.testassistant;

import org.jostein.testassistant.workers.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.Window;


public abstract class BaseActivity extends Activity {
    
    private final static int SPEED = 1400;
    /**
     * my phone number
     */
    public String MY_NUMBER = null;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        MY_NUMBER  = tm.getLine1Number();
    }
    private VelocityTracker vTracker = null;
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onUserInteraction();
        }
        if (getWindow().superDispatchTouchEvent(event)) {
            return false;
        }
        return true;
    }
    //must return false
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if(vTracker == null) {
                    vTracker = VelocityTracker.obtain();
                }
                else {
                    vTracker.clear();
                }
                vTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                vTracker.addMovement(event);
                vTracker.computeCurrentVelocity(1000);

                if ( vTracker.getXVelocity()>SPEED ) {
                    slideToPreviousActivity();
                }
                if ( vTracker.getXVelocity()<-SPEED ) {
                    slideToNextActivity();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                vTracker.recycle();
                break;
        }
        return super.onTouchEvent(event);
    }
    protected abstract void slideToNextActivity();
    protected abstract void slideToPreviousActivity();
}
