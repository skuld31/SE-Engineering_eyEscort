package com.example.gunhee.eyescort_guidance;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class FinalActivity extends Activity {

    int check = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_final, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void smsClick(View v) {
        switch (v.getId()) {
            case R.id.button3:

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("01099028306", null, "eyEscort안내 링크(보호자용)- https://eyescort.parseapp.com", null, null);
                    Toast.makeText(getApplicationContext(), "보호자에게 SMS가 발송되었습니다",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS 발송에 실패하였습니다",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
        }
    }
}
