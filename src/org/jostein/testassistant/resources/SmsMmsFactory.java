package org.jostein.testassistant.resources;

import android.content.Context;
import android.net.Uri;


import java.util.Random;

import org.jostein.testassistant.R;

public class SmsMmsFactory {
    private String messages;
    
    public SmsMmsFactory(Context context) {
        messages = context.getResources().getString(R.string.messages);
    }
    public String getMessageBody() {
        Random random = new Random();
         int length = random.nextInt(70) + 30;
         int start = random.nextInt(messages.length()-length);
         return  messages.substring(start, start+length);
    }
    
    public static class Sms {
    	public static final Uri CONTENT_URI = Uri.parse("content://sms");
    	
    	public static final String DATE = "date";
    	public static final String READ = "read";
    	public static final String TYPE = "type";
    	public static final String ADDRESS = "address";
    	public static final String BODY = "body";
    }
    
    public static class Mms {
    	public static final Uri CONTENT_URI = Uri.parse("content://mms");
    }
}
