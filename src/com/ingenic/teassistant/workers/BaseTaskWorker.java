package com.ingenic.teassistant.workers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ingenic.teassistant.HomeActivity;

import java.util.Random;

public abstract class BaseTaskWorker extends AsyncTask<Void, Integer, Integer> 
    implements DialogInterface.OnClickListener {

    private static final String TAG = "TEAssistant";
    public static final int ZERO = 0;
    public static final String DEFAULT_SIM_CONTENT_URI = "content://icc/adn";
    
    public static final String DEFAULT_DUALCARD_SIM_CONTENT_URI = "content://icc/adn";
    public static final String DEFAULT_DUALCARD_UIM_CONTENT_URI = "content://icc/adn";
    
    public static final Uri ICC_SMS_URI = Uri.parse("content://sms/icc");
    
    protected HomeActivity mContext;
    protected SharedPreferences mPrefs = null;
    protected int mCount = ZERO;
    protected int buttonId;//which button pressed to execute this AsyncTask

    protected Random mRandom = new Random();
    protected ProgressDialog pd;
    
    public BaseTaskWorker(HomeActivity context, int count) {
        this.mContext = context;
        this.mCount = count;
        buttonId = mContext.getLatestPressedButtonId();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        initializeProgressDialog();
    }
    
    @Override
    protected void onPreExecute() {
        if(mCount==ZERO){
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
        }
        pd.show();
    }
    
    private void initializeProgressDialog() {
        pd = new ProgressDialog(mContext);
        pd.setTitle("title");
        pd.setMessage("In Progress...");
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, "Hide", this);
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(mCount);
    }
    
    @Override
    protected void onProgressUpdate(Integer... progress){
        pd.setProgress(progress[0]);
    }
    
    @Override
    protected void onPostExecute(Integer result) {
        pd.dismiss();
    }
    
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_NEUTRAL:
                pd.hide();
                Toast.makeText(mContext, "The task will run in background.", Toast.LENGTH_LONG).show();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                pd.dismiss();
                this.cancel(true);
                Toast.makeText(mContext, "The task is canceled.", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
    
    protected void log(String message){
        Log.d(TAG, message);
    }
}
