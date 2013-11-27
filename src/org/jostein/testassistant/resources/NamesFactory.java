package org.jostein.testassistant.resources;

import android.content.Context;


import java.util.Locale;
import java.util.Random;

import org.jostein.testassistant.R;


public class NamesFactory {
    private Context mContext;
    private static Random mRandom = new Random();
    private String[] firstNames;
    private String[] lastNames;

    public NamesFactory(Context context) {
        this.mContext = context;
        firstNames = mContext.getResources().getStringArray(R.array.given_name);
        lastNames = mContext.getResources().getStringArray(R.array.family_name);
    }
    
    private String getNameFrom( String[] names ) {
        int index = mRandom.nextInt(names.length);
        return names[index];
    }
    
    public String getFakeName() {
        String currentLanguage =  Locale.getDefault().getDisplayLanguage();
        String name = "empty";
        if( currentLanguage.equalsIgnoreCase("English")) {
            name = getNameInEnglish();
        } else {
            name = getNameInChinese();
        }
        return name;
    }
    
    public String getNameInChinese() {
        String familyName = getNameFrom(lastNames);
        String givenName = "";
        givenName = getNameFrom(firstNames);
        if (mRandom.nextBoolean() &&  mRandom.nextBoolean() ) {
            //a 25 percent probability
        } else {
            givenName += getNameFrom(firstNames);
        }
        return familyName + givenName;
    }
    
    public String getNameInEnglish() {
        String firstName = getNameFrom(firstNames);
        String lastName = getNameFrom(lastNames);
        return firstName +" "+ lastName;
    }
    
}
