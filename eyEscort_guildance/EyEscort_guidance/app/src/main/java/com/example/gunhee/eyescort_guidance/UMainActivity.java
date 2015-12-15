package com.example.gunhee.eyescort_guidance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by STEP on 15. 12. 9..
 */
public class UMainActivity extends Activity implements TextToSpeech.OnInitListener {

    //TTS by sangho
    private TextToSpeech tts;

    //    Create objects for each clsss
    Intent SpeechIntent;
    SpeechRecognizer mRecognizer;

    Timer timer;
    TimerTask myTask;

    Beacon beacon;

    int requestCode;
    int REQUEST_CODE;

    String str = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umain);


        //TTS
        tts = new TextToSpeech(this, this);


        // The following parts are for voice recognition
        SpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        timer = new Timer();





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


    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            str = rs[0];



            if ((str.contains("안내")) || str.contains("시작")) {

                String text = "안암골 벽산아파트에서 안암 사거리까지 아이에스코트 안내를 시작합니다";
                if (!tts.isSpeaking() ) {

                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }

                Intent i;
                i = new Intent(UMainActivity.this, FinalActivity.class);
                startActivity(i);

            }

            if ((str.contains("안암")) || str.contains("벽산") || str.contains("아파트") || str.contains("아산") || str.contains("이학관")) {

                String text = "선택하신 출발지는 안암골 벽산 아파트입니다";
                if (!tts.isSpeaking() ) {

                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }

            }

            if ((str.contains("사거리"))) {

                String text = "선택하신 도착지는 안암 사거리입니다";
                if (!tts.isSpeaking() ) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }

            }






        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            // TODO Auto-generated method stub
        }

        @Override

        public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub

        }
    };


    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.back_btn:

                Intent i;
                i = new Intent(UMainActivity.this, FinalActivity.class);
                startActivity(i);
                break;

            case R.id.select_source:

                speakOut();

                Log.e("요기", "음성듣기 시작");
                myTask = new TimerTask() {
                    @Override
                    public void run() {
                        myTask.cancel();
                    }
                };
                mRecognizer.startListening(SpeechIntent);

                Log.e("요기", "음성듣기 종료");
                timer.schedule(myTask, 8 * 1000);  // 1000 is in ms, so 8 * 1000 ms = 8 * 1 sec = 8 sec
                // it means, wait for 8 seconds for voice input

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





//                String text = "선택하신 출발지는 " + str + "입니다";
//                if (!tts.isSpeaking() ) {
//
//                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
//                }


                break;



            case R.id.select_dest:

                speakOut2();

                Log.e("요기", "음성듣기 시작");
                myTask = new TimerTask() {
                    @Override
                    public void run() {
                        myTask.cancel();
                    }
                };
                mRecognizer.startListening(SpeechIntent);

                Log.e("요기", "음성듣기 종료");
                timer.schedule(myTask, 8 * 1000);  // 1000 is in ms, so 8 * 1000 ms = 8 * 1 sec = 8 sec
                // it means, wait for 8 seconds for voice input


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

//
//                String text2 = "선택하신 도착지는 " + str + "입니다";
//                if (!tts.isSpeaking() ) {
//
//                    tts.speak(text2, TextToSpeech.QUEUE_FLUSH, null);
//                }

                break;


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
    public void onInit(int i) {

            String greeting = "출발지 선택은 화면상단을, 도착지 선택은 화면하단을 눌러주세요";
           if (!tts.isSpeaking() ) {

                tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, null);
            }

        }


    // 안내음성
    private void speakOut() {

        String text = "출발지를 말해주시기 바랍니다";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void speakOut2() {

        String text = "도착지를 말해주시기 바랍니다";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


}
