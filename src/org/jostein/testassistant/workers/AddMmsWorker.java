package org.jostein.testassistant.workers;


import org.jostein.testassistant.HomeActivity;
import org.jostein.testassistant.resources.MmsFactory;
import org.jostein.testassistant.workers.BaseTaskWorker;

public class AddMmsWorker extends BaseTaskWorker {

    public AddMmsWorker(HomeActivity context, int count) {
        super(context, count);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int inserted = 0;
        for (; inserted<mCount && !this.isCancelled(); inserted++) {
            MmsFactory mms = new MmsFactory(mContext);
            mms.insertMmsToDB();
            publishProgress(inserted);
        }
        return inserted;
    }

}
