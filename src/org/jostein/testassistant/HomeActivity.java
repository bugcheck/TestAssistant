package org.jostein.testassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.jostein.testassistant.workers.*;



public class HomeActivity extends BaseActivity implements View.OnClickListener {
    
    private HashMap<Integer, Class> map = new HashMap<Integer, Class>();   //HashMap<Button_ID, AsyncTask_Class_Name>
    private EditText editText = null;
    private TextView textView = null;
    private SharedPreferences prefs;
    private int buttonId;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        initializeControls();

        //combine button id with the background AsyncTask class name
        map.put(R.id.addCallLogs,           AddCallLogsWorker.class);
        map.put(R.id.addPhoneContacts,      AddPhoneContactsWorker.class);
        map.put(R.id.addSmsToPhone,         AddSmsToPhoneWorker.class);
        map.put(R.id.addMms,                AddMmsWorker.class);
        map.put(R.id.addSimCardContacts,    AddSimCardContactsWorker.class);
        map.put(R.id.clearAccountContacts,  EraseContactsWorker.class);
        map.put(R.id.clearPhoneContacts,    EraseContactsWorker.class);
        map.put(R.id.clearSimContacts,      EraseContactsWorker.class);
        map.put(R.id.clearUimContacts,      EraseContactsWorker.class);
        map.put(R.id.clearSmsOnPhone,       EraseSmsMmsWorker.class);
        map.put(R.id.clearSmsOnSim,         EraseSmsMmsWorker.class);
        map.put(R.id.clearMms,              EraseSmsMmsWorker.class);
    }
    
    private void initializeControls() {
        textView = (TextView) findViewById(R.id.infoTextView);
        editText = (EditText) findViewById(R.id.editText);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        switch( menuItem.getItemId() ) {
            case R.id.menu_clearInfo:
                clearInformation();
                break;
            case R.id.menu_preferences:
                Intent intent = new Intent(this, org.jostein.testassistant.MainPreferenceActivity.class);
                this.startActivityForResult(intent, 0);
                break;
            default:
                    break;
        }
        return true;
    }
    
    public void clearInformation() {
        textView.setText("");
    }
    
    public void goToContacts(View v){
        Intent intent = new Intent();
        intent.setAction("com.android.contacts.action.LIST_DEFAULT");
//        intent.setClassName("com.android.contacts", "com.android.contacts.activities.PeopleActivity");
        startActivity(intent);
    }
    

    
    @SuppressWarnings("rawtypes")
    public void onClick(View view) {
        buttonId = view.getId();
        String text = editText.getText().toString();
        int count = 0;
        if ( !TextUtils.isEmpty(text) )
            count = Integer.valueOf(text);
        try {
            Class cls = map.get(view.getId());
            Constructor[] constructors = cls.getDeclaredConstructors();
            BaseTaskWorker task = (BaseTaskWorker)constructors[0].newInstance(this, count);
            task.execute(new Void[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void slideToNextActivity() {
        Intent intent = new Intent();
        intent.setClassName("org.jostein.testassistant", "org.jostein.testassistant.TestActivity");
        startActivity(intent);
    }

    @Override
    protected void slideToPreviousActivity() {
    }

    public int getLatestPressedButtonId() {
        return buttonId;
    }
}