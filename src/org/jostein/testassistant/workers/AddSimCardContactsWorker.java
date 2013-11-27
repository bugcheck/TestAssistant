package org.jostein.testassistant.workers;

import org.jostein.testassistant.HomeActivity;
import org.jostein.testassistant.resources.NamesFactory;
import org.jostein.testassistant.resources.PhoneNumbersFactory;

import android.content.ContentValues;
import android.net.Uri;


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
