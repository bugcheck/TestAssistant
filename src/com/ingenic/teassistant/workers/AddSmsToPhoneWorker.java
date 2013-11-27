package com.ingenic.teassistant.workers;

import android.content.ContentValues;
import android.net.Uri;

import com.ingenic.teassistant.HomeActivity;
import com.ingenic.teassistant.resources.PhoneNumbersFactory;
import com.ingenic.teassistant.resources.SmsMmsFactory;
import com.ingenic.teassistant.resources.SmsMmsFactory.Sms;



public class AddSmsToPhoneWorker extends BaseTaskWorker {
    
    public AddSmsToPhoneWorker(HomeActivity context, int count) {
        super(context, count);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int inserted = 0;
        for (; inserted<mCount && !this.isCancelled(); inserted++) {
            insertSmsToPhone();
            publishProgress(inserted);
        }
        return inserted;
    }
    
    private void insertSmsToPhone() {
        ContentValues values = new ContentValues();
        
        long date = System.currentTimeMillis()-mRandom.nextInt(7*24*60*60*1000);
        //read status 1-read 0-unread
        int read = mRandom.nextInt(1);
        //1-sent 2-inbox
        int type = mRandom.nextInt(2)+1;
        String address = PhoneNumbersFactory.getMobilePhoneNumbersFromContactsOrCreateOne(mContext);
        String body = new SmsMmsFactory(mContext).getMessageBody();
        
        values.put(Sms.DATE, date);
        values.put(Sms.READ, read);
        values.put(Sms.TYPE, type);
        values.put(Sms.ADDRESS, address);
        values.put(Sms.BODY, body);
        mContext.getContentResolver().insert(Sms.CONTENT_URI, values);
    }

    
}
