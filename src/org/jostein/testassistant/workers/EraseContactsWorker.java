package org.jostein.testassistant.workers;

import org.jostein.testassistant.HomeActivity;
import org.jostein.testassistant.R;

import android.net.Uri;
import android.provider.ContactsContract.RawContacts;

public class EraseContactsWorker extends BaseTaskWorker {

    
    public EraseContactsWorker(HomeActivity context, int count) {
        super(context, ZERO);
    }
    
    @Override
    protected Integer doInBackground(Void... params) {
        clearContacts();
        return null;
    }
    
    private void clearContacts() {
        Uri uri = RawContacts.CONTENT_URI;
        String where = null;
        String[] selectionArgs = null;
        switch(buttonId) {
            case R.id.clearPhoneContacts:
                where = "raw_contact_type=0 AND " + RawContacts.ACCOUNT_TYPE + " is null";
                mContext.getContentResolver().delete(uri, where, selectionArgs);//delete the data save in contacts2.db
                break;
            case R.id.clearAccountContacts:
                where = RawContacts.ACCOUNT_TYPE + "=?";
                selectionArgs = new String[]{"com.android.exchange"};
                mContext.getContentResolver().delete(uri, where, selectionArgs);
                break;
            case R.id.clearSimContacts:
                where = "raw_contact_type=11";
                String sim_uri = mPrefs.getString("sim_contacts_uri", DEFAULT_DUALCARD_SIM_CONTENT_URI);
                mContext.getContentResolver().delete(Uri.parse(sim_uri), null, null);
                mContext.getContentResolver().delete(uri, where, selectionArgs);
                break;
            case R.id.clearUimContacts:
                where = "raw_contact_type=21";
                String uim_uri = mPrefs.getString("uim_contacts_uri", DEFAULT_DUALCARD_UIM_CONTENT_URI);
                mContext.getContentResolver().delete(Uri.parse(uim_uri), null, null);
                mContext.getContentResolver().delete(uri, where, selectionArgs);
                break;
            default:
                break;
        }
    }
}
