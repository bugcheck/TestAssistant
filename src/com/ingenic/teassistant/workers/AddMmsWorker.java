package com.ingenic.teassistant.workers;


import com.ingenic.teassistant.HomeActivity;
import com.ingenic.teassistant.resources.MmsFactory;
import com.ingenic.teassistant.workers.BaseTaskWorker;

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
