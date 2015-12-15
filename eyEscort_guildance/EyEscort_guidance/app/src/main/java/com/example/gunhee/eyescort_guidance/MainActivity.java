package com.example.gunhee.eyescort_guidance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.EditText;

import java.util.ArrayList;


public class MainActivity extends Activity implements TextToSpeech.OnInitListener, TextWatcher{

    //TTS by sangho
    private TextToSpeech tts;

    private AutoCompleteTextView source;
    private AutoCompleteTextView destination;

    private EditText starting_loc;
    private EditText destination_loc;


    private AutoCompleteTextView autoComplete;
    private String item[] = {"안암역6호선","안암역","안암역2번출구","안암역1번출구","안암역 스타벅스","안암역 민영 주차장","안암역 사거리","안암역 맛집","안암역 찜질방","안암골 벽산아파트"};

    private AutoCompleteTextView autoComplete2;

    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    int requestCode;
    int REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TTS
        tts = new TextToSpeech(this, this);

        source = (AutoCompleteTextView) findViewById(R.id.myautocomplete);
        destination = (AutoCompleteTextView) findViewById(R.id.myautocomplete2);

        autoComplete = (AutoCompleteTextView) findViewById(R.id.myautocomplete);
        autoComplete.addTextChangedListener(this);
        autoComplete.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                item));

        autoComplete2 = (AutoCompleteTextView) findViewById(R.id.myautocomplete2);
        autoComplete2.addTextChangedListener(this);
        autoComplete2.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                item));

        // button on click event
        autoComplete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                speakOut();
            }

        });

        // button on click event
        autoComplete2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                speakOut2();
            }

        });

//        starting_loc = (EditText) findViewById(R.id.imageView);
//        destination_loc = (EditText) findViewById(R.id.imageView2);



    }

    private void speakOut() {
        String text = "출발지를 검색합니다";
        if (!tts.isSpeaking()) {

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    private void speakOut2() {
        String text = "도착지를 검색합니다";
        if (!tts.isSpeaking()) {

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    private void speakOut3() {
        String text = "출발지를 말해주세요";
        if (!tts.isSpeaking()) {

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    private void speakOut4() {
        String text = "도착지를 말해주세요";
        if (!tts.isSpeaking()) {

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        }
    }


    // TTS
    @Override
    protected void onDestroy() {
        if (tts!=null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:

                mHandler = new Handler();


                String source_str = source.getText().toString();
                String destination_str = destination.getText().toString();


                String text = source_str + "에서 " + destination_str + "까지의 경로를 검색합니다";
                if (!tts.isSpeaking()) {

                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                }


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


            case R.id.imageView:

//                String start = "출발지를 말씀해주세요";
//                if (!tts.isSpeaking()) {
//
//                    tts.speak(start, TextToSpeech.QUEUE_FLUSH, null);
//
//                }
                speakOut3();
                int REQUEST_CODE = 1;
                String DIALOG_TEXT = "출발지를 말해주세요";
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, DIALOG_TEXT);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, REQUEST_CODE);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

                startActivityForResult(intent, requestCode);

                break;

            case R.id.imageView2:

//                String destination = "도착지를 말씀해주세요";
//                if (!tts.isSpeaking()) {
//
//                    tts.speak(destination, TextToSpeech.QUEUE_FLUSH, null);
//
//                }
                speakOut4();
                int REQUEST_CODE2 = 2;
                String DIALOG_TEXT2 = "도착지를 말해주세요";
                Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent2.putExtra(RecognizerIntent.EXTRA_PROMPT, DIALOG_TEXT2);
                intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent2.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, REQUEST_CODE2);
                intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                startActivityForResult(intent2, requestCode);


                break;


//            case R.id.main_activity_stt:
//
//                String greeting = "무엇을 도와드릴까요?";
//                if (!tts.isSpeaking() ) {
//
//                    tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, null);
//                }

        }
    }



    String resultSpeech = "";
    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent intent) {
        super.onActivityResult(requestCode, resultcode, intent);
        ArrayList<String> speech;
        if (resultcode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                speech = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                resultSpeech = speech.get(0);
                //you can set resultSpeech to your EditText or TextView
//                starting_loc.setText(resultSpeech);
            }
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

    @Override
    public void onInit(int i) {

        welcomeMessage();

    }

    // 안내음성
    private void welcomeMessage() {

        String text = "경로검색 페이지로 이동합니다";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


}
