package com.ingenic.teassistant.resources;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;

import java.util.ArrayList;
import java.util.Random;

public class PhoneNumbersFactory {
    private static final Random mRandom = new Random();
    public static String[]  AREA_CODE_LIST = {
//        直辖市  
		"010", "022", "021", "023",
//        河北省
		"0311", "0310", "0319", "0312", "0313", "0314", "0315", "0335", "0317", "0316", "0318",
//        江西省
		"0791", "0798", "0790", "0792", "0701", "0793", "0795", "0794", "0796", "0797",
//        山东省
		"0531", "0532", "0533", "0536", "0535", "0631", "0537", "0633", "0534", "0530", "0538",
//        山西省
		"0351", "0352", "0353", "0355", "0349", "0354", "0358", "0357", "0359",
//        内蒙古自治区
		"0471", "0472", "0476", "0479", "0470", "0475", "0477", "0482",
//        河南省
		"0371", "0378", "0379", "0373", "0393", "0370", "0377", "0394", "0396",
//        辽宁省
		"024", "0411", "0412", "0413", "0414", "0415", "0416", "0417", "0418", "0419", "0410",
//        湖北省
		"027", "0714", "0710", "0719", "0717", "0714", "0712", "0713", "0718", "0716",
//        吉林省
		"0431", "0432", "0434", "0437", "0435", "0439", "0436", "0433", "0440",
//        湖南省
		"0731", "0733", "0732", "0734", "0730", "0736", "0735", "0737", "0746", "0745", "0744",
//        黑龙江
		"0451", "0452", "0459", "0458", "0453", "0454", "0455", "0457", "0456",
//        广东省
		"020", "0755", "0756", "0754", "0751", "0752", "0769", "0760", "0757", "0759",
//        江苏省
		"025", "0516", "0518", "0517", "0527", "0515", "0514", "0513", "0511", "0519", "0510", "0512", "0520",
//        广西壮族自治区
		"0771", "0772", "0773", "0774", "0779", "0777",
//        海南省
		"0898", "0899", "0890",
//        四川省
		"028", "0812", "0838", "0816", "0813", "0832", "0833", "0830", "0831",
//        浙江省
		"0571", "0574", "0573", "0572", "0575", "0579", "0570", "0580", "0577", "0576",
//        贵州省
		"0851", "0852", "0853", "0858",
//        安徽省
		"0551", "0554", "0552", "0555", "0556", "0559", "0550", "0557", "0565", "0563",
//        云南省
		"0871", "0870", "0874", "0877", "0879", "0888", "0873", "0878",
//        福建省
		"0591", "0592", "0598", "0594", "0595", "0596", "0599", "0593", "0597",
//        陕西省
		"029", "0919", "0917", "0913", "0914",
//        西藏自治区
		"0891", "0892", "08018", "08059", "0897",
//        甘肃省
		"0931", "0935", "0938", "0933", "0937", "0937",
//        青海省
		"0971", "0972", "0979", "0975",
//        宁夏回族自治区
		"0951", "0952", "0953", "0954",
//        新疆维吾尔自治区
		"0991", "0990", "0995", "0998", "0908", "0996",
    };
    public static String[] MOBILE_PREFIX = {
//        130, 131, ..., 188
    };
    public static String getFixedPhoneNumber(){
        int randomIndex = mRandom.nextInt(AREA_CODE_LIST.length);
        String areaCode = AREA_CODE_LIST[randomIndex];
        String lastEightNumbers = getLastEightNumbersInString();
        return areaCode + lastEightNumbers;
    }
    private static String getLastEightNumbersInString() {
        int i = mRandom.nextInt(100000000);
        String lastEightNumbers = String.valueOf(i);
        while (lastEightNumbers.length()<8) {
            lastEightNumbers = "0" + lastEightNumbers;
        }
        return lastEightNumbers;
    }
    public static String getMobilePhoneNumber() {
        int i = 130 + mRandom.nextInt(200-130);  //the first three numbers of mobile phone
        String prefix = String.valueOf(i);
        String lastEightNumbers = getLastEightNumbersInString();
        return prefix + lastEightNumbers;
    }
    
    public static String getRandomPhoneNumber() {
        if (mRandom.nextBoolean())
            return getMobilePhoneNumber();
        else
            return getFixedPhoneNumber();
    }
    
    private static String[] getMobileNumberFromContacts(Context context){
        Cursor cursor = context.getContentResolver().query(
                Data.CONTENT_URI, 
                new String[]{Phone.NUMBER}, 
                Phone.TYPE + "=" + Phone.TYPE_MOBILE 
                + " AND " + Phone.MIMETYPE + "=?", 
                new String[]{Phone.CONTENT_ITEM_TYPE}, 
                null );
        if(cursor==null || cursor.getCount()==0) {
            return null;
        } else{
            String[] numbers = new String[cursor.getCount()];
            for(int i=0; cursor.moveToNext(); i++){
                int columnIndex = cursor.getColumnIndex(Phone.NUMBER);
                String number = cursor.getString(columnIndex);
                numbers[i] = number;
            }   
            return numbers;
        }
    }
    public static String getMobilePhoneNumbersFromContactsOrCreateOne(Context mContext) {
        String address = "";
        String[] numbers = getMobileNumberFromContacts(mContext);
        if(numbers!=null) {
            int randomIndex = mRandom.nextInt(numbers.length);
            address = numbers[randomIndex]; 
        } else {
            address = getMobilePhoneNumber();
        }
        return address;
    }
}
