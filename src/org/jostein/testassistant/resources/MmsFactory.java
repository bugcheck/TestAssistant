package org.jostein.testassistant.resources;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

import org.jostein.testassistant.workers.Utils;

public class MmsFactory {
    
    private static ContentValues audioMms = new ContentValues();
    private static ContentValues vCardMms = new ContentValues();
    private static ContentValues videoMms = new ContentValues();
    private static ContentValues pictureMms = new ContentValues();
    
    private static ContentValues[] MMS_TYPES = {
        audioMms, vCardMms, videoMms, pictureMms
    };
    
    private int mms_type = -1;
    private String attachmentFileName = null;
    /** copy and rename the attachment to app_part, so every mms can really own their attachment,
     * it would avoid some bugs when we delete part of faked mms.
    */
    private String attachmentAbsolutePath = null;

    private static final int INBOX = 1;
    private static final int SENT = 2;
    
    private int threadId = -1;
    private int pduId = -1;
    private long date = -1;
    private int msg_box = -1;
    private String address = null;
    private String subject = null;
    private long size = 0;
    private Context mContext = null;
    ContentResolver mCR = null;
    
    private static final Uri PDU_URI = Uri.parse("content://mms");
    private Uri ADDR_URI = null;
    private Uri PART_URI = null;
    
    public static final String ATTACHMENT_PATH = "/data/data/com.android.providers.telephony/app_parts/";
    
    private Random mRandom = new Random();
    public ContentValues pduValues = new ContentValues();
    public ContentValues partSmilValues = new ContentValues();
    private ContentValues partAttachmentValues = new ContentValues();
    private ContentValues addrFromValues = new ContentValues();
    private ContentValues addrToValues = new ContentValues();
    private Uri smsUri = null;
    private String contentType = null;
    private String smilText = null;
    
    public MmsFactory(Context mContext) {
        
        this.mContext = mContext;
        mCR = mContext.getContentResolver();

        definitionMmsType();
        address = PhoneNumbersFactory.getMobilePhoneNumbersFromContactsOrCreateOne(mContext);
        subject = "sent mms subject";
        date = System.currentTimeMillis()-mRandom.nextInt(7*24*60*60*1000);
        msg_box = mRandom.nextBoolean()? INBOX : SENT;
        mms_type = mRandom.nextInt(MMS_TYPES.length);
        attachmentAbsolutePath = ATTACHMENT_PATH + "PART_" + date;
        
        attachmentFileName = MMS_TYPES[mms_type].getAsString("file_name");
        size = MMS_TYPES[mms_type].getAsLong("size");
        contentType  = MMS_TYPES[mms_type].getAsString("content_type");
        smilText  = MMS_TYPES[mms_type].getAsString("smil_text");
        
        this.pduValues = createPduValues();
        
        this.partSmilValues = createPartSmilValues();
        
        this.partAttachmentValues = createPartAttachmentValues();
        
        this.addrFromValues = createAddrFromValues();
        
        this.addrToValues = createAddrToValues();
        
    }
    
    private void definitionMmsType() {
        pictureMms.put("content_type", "image/jpeg");
        pictureMms.put("file_name", "bluesky.jpg");
        pictureMms.put("size", 104832);
        pictureMms.put("smil_text", 
                "<smil>" +
                    "<head>" +
                        "<layout><root-layout width=\"320px\" height=\"480px\"/>" +
                            "<region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/>" +
                        "</layout>" +
                    "</head>" +
                    "<body>" +
                        "<par dur=\"5000ms\">" +
                            "<img src=\"bluesky.jpg\" region=\"Image\"/>" +
                        "</par>" +
                    "</body>" +
                "</smil>"
        );
        
        audioMms.put("content_type", "application/ogg");
        audioMms.put("file_name", "audio.ogg");
        audioMms.put("size", 112987);
        audioMms.put("smil_text", 
            "<smil>" +
                "<head>" +
                    "<layout>" +
                        "<root-layout width=\"320px\" height=\"480px\"/>" +
                    "</layout>" +
                "</head>" +
                "<body>" +
                    "<par dur=\"10214ms\"><audio src=\"audio.ogg\" dur=\"10214ms\"/>" +
                    "</par>" +
                "</body>" +
            "</smil>"
        );
        
        vCardMms.put("content_type", "text/x-vCard");
        vCardMms.put("file_name", "209k.vcf");
        vCardMms.put("size", 209*1024);
        vCardMms.put("smil_text",
            "<smil>" +
                "<head>" +
                    "<layout>" +
                        "<root-layout width=\"320px\" height=\"480px\"/>" +
                    "</layout>" +
                "</head>" +
                "<body>" +
                    "<par dur=\"5000ms\"/>" +
                "</body>" +
            "</smil>"
        );
        
        videoMms.put("content_type", "video/3gp");
        videoMms.put("file_name", "268k.3gp");
        videoMms.put("size", 274766);
        videoMms.put("smil_text", 
                "<smil>" +
                    "<head>" +
                        "<layout>" +
                            "<root-layout width=\"320px\" height=\"480px\"/>" +
                            "<region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/>" +
                        "</layout>" +
                    "</head>" +
                    "<body>" +
                        "<par dur=\"3320ms\">" +
                            "<video src=\"268k.3gp\" region=\"Image\" dur=\"3320ms\"/>" +
                        "</par>" +
                    "</body>" +
                "</smil>"
        );
    }

    public void insertMmsToDB(){
        //get threadId
        this.threadId = insertSmsToGetThreadId();
        pduValues.put("thread_id", threadId);
        
        //insert pdu row to get pduId
        Uri uri = mCR.insert(PDU_URI, pduValues);
        pduId = Integer.valueOf(getValueByUriAndColumnName(uri, "_id"));
        partSmilValues.put("mid", pduId);
        partAttachmentValues.put("mid", pduId);
        Utils.log("pduId is " + pduId);
        
        //insert smil information to part table
        PART_URI = Uri.parse("content://mms/" + pduId + "/part");
        mCR.insert(PART_URI, partSmilValues);
        
        //insert attachment information to part table attachment
        Uri uri_part = mCR.insert(PART_URI, partAttachmentValues);
        attachmentAbsolutePath = getValueByUriAndColumnName(uri_part, "_data");
        
        //copy the attachment file to /data/data/com.android.providers.telephony/app_parts
        copyAndRenameAttachmentToTelephony();
        
        //insert information to addr table
        ADDR_URI = Uri.parse("content://mms/" + pduId + "/addr");
        mCR.insert(ADDR_URI, addrFromValues);
        mCR.insert(ADDR_URI, addrToValues);
        
        
        mCR.delete(smsUri, null, null);
    }
    
    private void copyAndRenameAttachmentToTelephony(){
        try {   
            int byteread = 0;   
            InputStream in = mContext.getAssets().open(attachmentFileName);
            FileOutputStream fs = new FileOutputStream(attachmentAbsolutePath);   
            byte[] buffer = new byte[1444];   
            while ( (byteread = in.read(buffer)) != -1) {   
                fs.write(buffer, 0, byteread);   
            }   
            in.close();
        }   
        catch (Exception e) {   
            e.printStackTrace();   
        }
    }
    
    private int insertSmsToGetThreadId() {
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("read", 0);
        values.put("type", mRandom.nextInt(2)+1);
        values.put("address", address);
        values.put("body", "it doesn't matter");
        smsUri = mContext.getContentResolver().insert(Uri.parse("content://sms"), values);
        int thread_id = Integer.valueOf(getValueByUriAndColumnName(smsUri, "thread_id"));
        Utils.log("thread id is "+thread_id);
        return thread_id;
    }
    
    private String getValueByUriAndColumnName(Uri uri, String columnName) {
        Cursor cursor = mContext.getContentResolver()
                .query(uri, new String[]{columnName}, null, null, null);
        if(cursor == null || cursor.getCount() == 0) {
            return "-1";
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(columnName);
        String value = cursor.getString(columnIndex);
        cursor.close();
        return value;
    }

    private ContentValues createAddrToValues() {
        ContentValues cv = new ContentValues();
        cv.put("msg_id", pduId);
        cv.put("address", address);
        cv.put("type", 151);
        cv.put("charset", 106);
        return cv;
    }

    private ContentValues createAddrFromValues() {
        ContentValues cv = new ContentValues();
        cv.put("msg_id", pduId);
        cv.put("address", "insert-address-token");
        cv.put("type", 137);
        cv.put("charset", 106);
        return cv;
    }

    private ContentValues createPartAttachmentValues() {
        ContentValues cv = new ContentValues();
        cv.put("mid", pduId);
        cv.put("seq", 0);
        //update "ct" field  to attachment's type after, so we could avoid the following situation:
        //  when insert a new row to the part table, if "ct" != "application/smil", the provider would
        //  update the "_data" field with the current time.
        cv.put("ct", contentType);
        switch(msg_box) {
            case INBOX:
                cv.put("name", attachmentFileName);
        }
        cv.put("cid", "<" + attachmentFileName + ">");
        cv.put("cl", attachmentFileName);
        cv.put("_data", attachmentAbsolutePath);//here this values don't matter
        return cv;

    }

    private ContentValues createPartSmilValues() {
        ContentValues cv = new ContentValues();
        cv.put("mid", pduId);
        cv.put("seq", -1);
        cv.put("ct", "application/smil");
        switch(msg_box){
            case INBOX:
                cv.put("name",  "smil.xml");
                break;
        }
        cv.put("cid", "<smil>");
        cv.put("cl", "smil.xml");
        
        cv.put("text", smilText);
        return cv;
    }

    //pdu table don't care what kind of attachment:)
    private ContentValues createPduValues() {
        ContentValues cv = new ContentValues();
        cv.put("thread_id", threadId);
        cv.put("date", date/1000);
        cv.put("date_sent", 0);
        cv.put("msg_box", msg_box);
        cv.put("read", mRandom.nextInt(2));
        cv.put("m_id", "110114394391000136374");
        cv.put("sub", subject);
        if(subject != null)
            cv.put("sub_cs", 106);
        cv.put("ct_t", "application/vnd.wap.multipart.related");
        cv.put("m_cls", "personal");
        cv.put("pri", 129);
        cv.put("m_size", size);
        cv.put("d_rpt", 128);
        cv.put("seen", mRandom.nextInt(2));
        switch (msg_box) {
            case SENT:
                cv.put("exp",           604800);
                cv.put("m_type",        128);
                cv.put("v",             18);
                cv.put("rr",            128);
                cv.put("resp_st",       128);
                cv.put("st",            134);
                cv.put("tr_id",         "T13abab2123d");
                break;
            case INBOX:
                cv.put("ct_l",          "http://221.179.185.231/BOlmhX1RaCmH");//it doesn't matter
                cv.put("m_type",        132);
                cv.put("v",             16);
                cv.put("rr",            129);
                cv.put("tr_id",         "3340000254060121101143738001");//it doesn't matter
        }
        return cv;
    }
    
}
