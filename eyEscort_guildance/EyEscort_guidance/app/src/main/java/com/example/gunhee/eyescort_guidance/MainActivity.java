package com.example.gunhee.eyescort_guidance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;


public class MainActivity extends Activity implements TextWatcher{


    private AutoCompleteTextView autoComplete;
    private String item[] = {"안암역6호선","안암역","안암역2번출구","안암역1번출구","안암역 스타벅스","안암역 민영 주차장","안암역 사거리","안암역 맛집","안암역 찜질방","안암골 벽산아파트"};

    private AutoCompleteTextView autoComplete2;

    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        autoComplete = (AutoCompleteTextView) findViewById(R.id.myautocomplete);
        autoComplete.addTextChangedListener(this);
        autoComplete.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                item));

        autoComplete2 = (AutoCompleteTextView) findViewById(R.id.myautocomplet2);
        autoComplete2.addTextChangedListener(this);
        autoComplete2.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                item));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:

                mHandler = new Handler();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mProgressDialog = ProgressDialog.show(MainActivity.this,"",
                                "잠시만 기다려 주세요.",true);
                        mHandler.postDelayed( new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    if (mProgressDialog!=null&&mProgressDialog.isShowing()){
                                        mProgressDialog.dismiss();
                                    }
                                }
                                catch ( Exception e )
                                {
                                    e.printStackTrace();
                                }
                            }
                        }, 2000);
                    }
                } );

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(i);
                    }
                }, 2000);
            break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
