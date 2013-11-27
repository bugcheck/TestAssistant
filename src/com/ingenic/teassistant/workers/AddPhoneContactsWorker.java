package com.ingenic.teassistant.workers;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import com.ingenic.teassistant.HomeActivity;
import com.ingenic.teassistant.resources.*;

import java.util.ArrayList;

public class AddPhoneContactsWorker extends BaseTaskWorker {
    
    public AddPhoneContactsWorker(HomeActivity context, int count) {
        super(context, count);
    }
    
    public void onPreExecute(){
        super.onPreExecute();
    }
    
    @Override
    protected Integer doInBackground(Void... params) {
        int inserted = 0;
        for (; inserted<mCount && !this.isCancelled(); inserted++) {
        	try {
				addContact();
			} catch (Exception e) {
				e.printStackTrace();
			}
            publishProgress(inserted);
        }
        return inserted;
    }

    public void addContact() throws RemoteException, OperationApplicationException {
    	ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
    	ContentProviderOperation.Builder builder = 
    			ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
    	builder.withValue(RawContacts.ACCOUNT_TYPE, null);
    	builder.withValue(RawContacts.ACCOUNT_NAME, null);
    	int backReferenceValue = operations.size();
    	operations.add(builder.build());

    	builder = constructPhoneDataOperation(backReferenceValue, getNameValues());
    	operations.add(builder.build());
    	if (mPrefs.getBoolean("mobile_phone", true)) {
    		Builder phoneBuilder = constructPhoneDataOperation(backReferenceValue, getMobilePhoneNumberValues());
    		operations.add(phoneBuilder.build());
    	}
    	if (mPrefs.getBoolean("home_phone", false)) {
    		Builder mhBuilder = constructPhoneDataOperation(backReferenceValue, getFixedPhoneNumberValues());
    		operations.add(mhBuilder.build());
    	}
    	if (mPrefs.getBoolean("email_address", false)) {
    		Builder emailBuilder = constructPhoneDataOperation(backReferenceValue, getEmailValues());
    		operations.add(emailBuilder.build());
    	}
    	mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
    }
    
    private ContentProviderOperation.Builder constructPhoneDataOperation(int backReferenceValue, ContentValues cv) {
    	return constructInsertOperation(ContactsContract.Data.CONTENT_URI, cv, true,
    			Data.RAW_CONTACT_ID, backReferenceValue);
    }
    
    private ContentProviderOperation.Builder constructInsertOperation(Uri uri, ContentValues cv,
    		boolean withBackReferenceValue, String key, int backReferenceValue) {
    	ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
    	builder.withValues(cv);
    	if (withBackReferenceValue) {
    		withValueBackReference(builder, key, backReferenceValue);
    	}
    	return builder;
    }
    
    private ContentProviderOperation.Builder withValueBackReference(
    		ContentProviderOperation.Builder builder, String key, int backReferenceValue) {
    	return builder.withValueBackReference(key, backReferenceValue);
    }
    
    private ContentValues getNameValues() {
        ContentValues cv = new ContentValues();
        cv.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        String name = new NamesFactory(mContext).getFakeName();
        cv.put(StructuredName.DISPLAY_NAME, name);
        return cv;
    }
    
    private ContentValues getMobilePhoneNumberValues() {
        ContentValues cv = new ContentValues();
        cv.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        cv.put(Phone.NUMBER, PhoneNumbersFactory.getMobilePhoneNumber());
        cv.put(Phone.TYPE,Phone.TYPE_MOBILE);
        return cv;
    }
    
    private ContentValues getFixedPhoneNumberValues() {
        ContentValues cv = new ContentValues();
        cv.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        cv.put(Phone.NUMBER, PhoneNumbersFactory.getFixedPhoneNumber());
        cv.put(Phone.TYPE,Phone.TYPE_HOME);
        return cv;
    }
    
    private ContentValues getEmailValues() {
        ContentValues cv = new ContentValues();
        cv.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
        cv.put(Data.DATA1, EmailsFactory.getEmail());
        cv.put(Email.TYPE, Email.TYPE_MOBILE);
        return cv;
    }
}
