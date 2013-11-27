package org.jostein.testassistant.workers;

import org.jostein.testassistant.HomeActivity;
import org.jostein.testassistant.resources.PhoneNumbersFactory;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.os.RemoteException;
import android.provider.CallLog.Calls;
import android.util.Log;




public class AddCallLogsWorker extends BaseTaskWorker {

    private static final String TAG = "AddCallLogsWorker";
    private static final int[] CALLS_TYPES = new int[]{Calls.INCOMING_TYPE, Calls.MISSED_TYPE, Calls.OUTGOING_TYPE};

    public AddCallLogsWorker(HomeActivity context, int count) {
        super(context, count);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return insertIntoCallLogs();
    }

    /**
     * Inserts a number of entries in the call log based on the given templates.
     *
     * @return the number of inserted entries
     */
    private Integer insertIntoCallLogs() {
        int inserted = 0;

        for (int index = 0; index < mCount; ++index) {
            ContentValues values = createContentValuesForCallLog();
            // Insert into the call log the newly generated entry.
            ContentProviderClient contentProviderClient =
                    mContext.getContentResolver().acquireContentProviderClient(Calls.CONTENT_URI);
            try {
                Log.d(TAG, "adding entry to call log");
                contentProviderClient.insert(Calls.CONTENT_URI, values);
                ++inserted;
                publishProgress(inserted);
            } catch (RemoteException e) {
                Log.d(TAG, "insert failed", e);
            }
        }
        return inserted;
    }
    
    private ContentValues createContentValuesForCallLog(){
        ContentValues values = new ContentValues();
        // These should not be set.
        values.putNull(Calls._ID);
        // Add some randomness to the date. For each new entry being added, add an extra
        // day to the maximum possible offset from the original.
        
        values.put(Calls.DATE,
                System.currentTimeMillis()
                - mRandom.nextInt( 7 * 24 * 60 * 60) * 1000L );
        values.put(Calls.NUMBER, 
                PhoneNumbersFactory.getMobilePhoneNumbersFromContactsOrCreateOne(mContext));
        
        int type = CALLS_TYPES[mRandom.nextInt(CALLS_TYPES.length)];
        if (type != Calls.MISSED_TYPE) {
            values.put(Calls.DURATION, mRandom.nextInt(30 * 60 * 60 * 1000));
        }
        values.put(Calls.TYPE, type);
        
        return values;
    }
}

