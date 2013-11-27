package com.ingenic.teassistant.workers;

import android.content.ContentValues;
import android.net.Uri;

import com.ingenic.teassistant.HomeActivity;
import com.ingenic.teassistant.resources.NamesFactory;
import com.ingenic.teassistant.resources.PhoneNumbersFactory;

public class AddSimCardContactsWorker extends BaseTaskWorker {
    
    public AddSimCardContactsWorker(HomeActivity context, int count) {
        super(context, count);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int inserted = 0;
        for (; inserted<mCount && !this.isCancelled(); inserted++) {
            addContactsToSim();
            publishProgress(inserted);
        }
        return inserted;
    }
    
    private void addContactsToSim() {
        ContentValues values = new ContentValues();
        values.put("tag", new NamesFactory(mContext).getFakeName());
        values.put("number", PhoneNumbersFactory.getRandomPhoneNumber());
        String uri = null;
        uri = mPrefs.getString("sim_contacts_uri", DEFAULT_DUALCARD_SIM_CONTENT_URI);
        log("current sim uri is " + uri);
        mContext.getContentResolver().insert(Uri.parse(uri), values);
    }
    

}
