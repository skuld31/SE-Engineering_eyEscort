package nudge8.com.eyescort;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nudge8.com.eyescort.R;

public class MainActivity extends Activity {

    Intent ScreamIntent;

    Intent SpeechIntent;
    SpeechRecognizer mRecognizer;

    String str = "";

    MediaPlayer mp1;
    MediaPlayer mp2;
//    MediaPlayer mp3;
//    MediaPlayer mp4;

    Timer timer;
    TimerTask myTask;
//    SmsManager smsManager = SmsManager.getDefault();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        mp1 = MediaPlayer.create(this, R.raw.check);
//        mp2 = MediaPlayer.create(this, R.raw.iamgoodtoo);

        timer = new Timer();

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stt:
//              Log.e("요기", "여기3");
                myTask = new TimerTask() {
                    @Override
                    public void run() {
                        myTask.cancel();
                    }
                };
                mRecognizer.startListening(SpeechIntent);
//                Log.e("요기","여기4");
                timer.schedule(myTask, 8 * 1000);
                break;
        }
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
            if ((str.contains("hello") && str.contains(""))) {
                mp1.start();
                Toast.makeText(MainActivity.this, " Hey, how are you?", Toast.LENGTH_LONG).show();
            } else if (str.contains("thank you") || str.contains("and you?") || str.contains("what about you?")) {
                mp2.start();
                Toast.makeText(MainActivity.this, " I am good, too. Thanks", Toast.LENGTH_LONG).show();
            }
            /*
            else if(str.contains("먹고") || str.contains("사갈까"))
                mp3.start();
            else
                mp4.start();
                */
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
}