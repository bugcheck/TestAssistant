package com.ingenic.teassistant.workers;

import android.net.Uri;

import com.ingenic.teassistant.HomeActivity;
import com.ingenic.teassistant.R;
import com.ingenic.teassistant.resources.SmsMmsFactory.Mms;
import com.ingenic.teassistant.resources.SmsMmsFactory.Sms;

public class EraseSmsMmsWorker extends BaseTaskWorker {

    //if count is useless for worker, use ZERO to replace it, so we don't have to
    //override the onPreExecute() method to redefine ProgressDialog
    public EraseSmsMmsWorker(HomeActivity context, int count) {
        super(context, ZERO);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        switch(buttonId){
            case R.id.clearMms:
                eraseAllMms();
                break;
            case R.id.clearSmsOnPhone:
                eraseAllSmsOnPhone();
                break;
            case R.id.clearSmsOnSim:
                eraseAllSmsOnSim();
                break;
        }
        return null;
    }
    
    private void eraseAllSmsOnSim() {
        Uri sim_sms_uri = Uri.parse("content://sms/icc");
        mContext.getContentResolver().delete(sim_sms_uri, null, null);
    }

    private void eraseAllSmsOnPhone(){
        mContext.getContentResolver().delete(Sms.CONTENT_URI, 
            //"locked=0", 
            null,
            null);
    }
    private void eraseAllMms() {
        mContext.getContentResolver().delete(Mms.CONTENT_URI, 
            //"locked=0", 
            null,
            null);
    }
}
