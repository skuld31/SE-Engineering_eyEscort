package com.example.gunhee.eyescort_guidance;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

// by sangho

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


// till here


public class FinalActivity extends Activity implements TextToSpeech.OnInitListener, BeaconConsumer {

    public static int num_of_start = 0;

    //TTS by sangho
    private TextToSpeech tts;

    //    Create objects for each clsss
    Intent SpeechIntent;
    SpeechRecognizer mRecognizer;

    Timer timer;
    TimerTask myTask;

    int cnt=0;
    double check1=10000;
    double check2=10000;
    double closer_beacon_dist = 10000;
    double check_difference=10000;
    double checkVibe = 0;
    int major;
    int minor;


//    ArrayList<String> check = new ArrayList<String>();


    Beacon beacon;

    String str = "";

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final int REQUEST_ENABLE_BT = 3;
    private ArrayList<Beacon> beacons;


    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager;
//    private ListView listView;
//    private TextView statusView;
    private BeaconCollectionAdapter beaconAdapter;

    int check = 1;

    // number of times voice guidance tells user of the distance to traffic light
    private int voice_guidance_20steps = 0;
    private int voice_guidance_10steps = 0;
    private int voice_guidance_0steps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        this.beaconAdapter = new BeaconCollectionAdapter(this);

        // keep track of how many times the activity was loaded
        num_of_start++;

        //TTS
        tts = new TextToSpeech(this, this);


//        String greeting = "아이에스코트 안내를 시작합니다";
//        RelativeLayout start_stt = (RelativeLayout) findViewById(R.id.stt);
//        if (!tts.isSpeaking()) {
//            tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, null);
//        }

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // The following parts are for voice recognition
        SpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        // The following is a way to use the created object, mp1, to connect with voice recording file, 'check.m4a'
        // 'check.m4a' file is in raw folder in res folder and FYI, by default, 'raw' folder does not exist in res folder
        // but is usually created for storing sound recording files
        timer = new Timer(); // this is later used as the waiting time after voice recognition button has been pressed

        // The following codes are for creating instance for BeaconManager class
        // This is not part of Android Studio API
        // So, this source is imported from 'android-beacon-library-2.6.1' as external library
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

    }

    // TTS
    @Override
    protected void onDestroy() {
        if (tts!=null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
        beaconManager.unbind(this); //
    }


    // by sangho
    @Override
    public void onBeaconServiceConnect() {

        // final Region region = new Region("myBeaons", Identifier.parse("<replaceBySomeUIID>"), null, null);
        // The below code is a modified version of the above code
        // modification - replaced "replacedBySomeUIID" with the below code for RECOS
        final Region region = new Region("myBeaons", Identifier.parse("24ddf411-8cf1-440c-87cd-e368daf9c93e"), null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });


        beaconManager.setRangeNotifier(new RangeNotifier() {



            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> rangedBeacons, Region region) {

                final ArrayList<Beacon> myNodeList = new ArrayList<Beacon>(rangedBeacons);
                //  ArrayAdapter<ArrayList> apdater = new ArrayAdapter<ArrayList>(RangeNotifier,R.id.list_item,R.id.idd1,R.id.idd2,R.id.idd3,R.id.idd4, myNodeList);
                //  setListAdapter(apdater);

                if (rangedBeacons.size() == 0) {
                    return;
                }

                for (int j = 0; j < myNodeList.size(); j++) {
                    beacon = myNodeList.get(j);


                    if (beacon.getId3().toString().equals("30")) {
                        check1 = beacon.getDistance(); // centimeter(cm) as unit
                        Log.e(TAG, "30's distance: " + check1);
                        Log.e(TAG, "30's distance: " + check1);
                        Log.e(TAG, "30's distance: " + check1);
                        Log.e(TAG, "30's distance: " + check1);

                    }
                    if (beacon.getId3().toString().equals("32")) {
                        check2 = beacon.getDistance(); // centimeter(cm) as unit
                        Log.e(TAG, "32's distance:" + check2);
                        Log.e(TAG, "32's distance:" + check2);
                        Log.e(TAG, "32's distance:" + check2);
                        Log.e(TAG, "32's distance:" + check2);
                    }

                    if(check1>check2)
                        closer_beacon_dist=check2;
                    else
                        closer_beacon_dist=check1;

                    Log.e(TAG, "Closer beacon: " + closer_beacon_dist);
                    Log.e(TAG, "Closer beacon: " + closer_beacon_dist);
                    Log.e(TAG, "Closer beacon: " + closer_beacon_dist);
                    Log.e(TAG, "Closer beacon: " + closer_beacon_dist);
                    Log.e(TAG, "Closer beacon: " + closer_beacon_dist);
                    Log.e(TAG, "Closer beacon: " + closer_beacon_dist);

                    checkVibe = check2 - check1;
                }

                Log.e(TAG, "Difference: " + (checkVibe));
                Log.e(TAG, "Difference: " + (checkVibe));
                Log.e(TAG, "Difference: " + (checkVibe));
                Log.e(TAG, "Difference: " + (checkVibe));
                Log.e(TAG, "Difference: " + (checkVibe));
                Log.e(TAG, "Difference: " + (checkVibe));
                if(checkVibe > 0.8)
                    Log.e(TAG, "길을 벗어나셨습니다");

                //check1 is beacon with ID=30
                //check2 is beacon with ID=32

                // 30 cm is approximately 1 step
                // 20 steps = 30 cm x 20
                // 20 steps = 600 cm

                // check1 is beacon with ID = 30

                if(checkVibe > 1.7 || checkVibe < -1.7) {
                    if (closer_beacon_dist < 3.8 && closer_beacon_dist > 2.6) {
                        voice_guidance_20steps++;

                        if (voice_guidance_20steps < 4) {
                            String greeting = "스무 걸음 이내에 신호등이 있습니다";

                            if (!tts.isSpeaking()) {

                                tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, null);

                            }
                        }


                    } else if (closer_beacon_dist < 2.6 & closer_beacon_dist > 1.5) {

                        voice_guidance_10steps++;
//                    if (voice_guidance_10steps < 4) {

                        String greeting = "열 걸음 이내입니다";
                        if (!tts.isSpeaking()) {

                            tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, null);

                        }
//                    }


                        //mp2.start();


                    } else if (closer_beacon_dist < 1.3) {

                        voice_guidance_0steps++;
//                    if (voice_guidance_0steps < 4) {
                        String greeting = "신호등 앞에 도착하였습니다";
                        if (!tts.isSpeaking()) {

                            tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, null);

//                        }
                        }


                    }

                }
                if (checkVibe > 0.3 || checkVibe < -0.3) {
                    Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                    vibe.vibrate(500);
//                    String greeting = "길을 벗어나셨습니다";
//                    if (!tts.isSpeaking()) {
//
//                        tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, null);
//
//                    }


                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        statusView.setText("Beacons found: " + rangedBeacons.size());
                        beaconAdapter.replaceWith(myNodeList);
                    }
                });


            }
        });


                /* for (Beacon oneBeacon : rangedBeacons) {
                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                    Log.e(TAG, "distance:" + oneBeacon.getDistance());
                    Log.e(TAG, "count:" + cnt);

                    if (oneBeacon.getDistance() < 20 && oneBeacon.getDistance() > 15) { // if the distance to beacon is less than
                       if(cnt==0) {
                           mp1.start();  // play sound recording
                           cnt++;
                       }
                        Log.e("요기", "traffic light nearby");   // and print the Log to inform us that it was activated
                    }
                    else if(oneBeacon.getDistance() < 9 && oneBeacon.getDistance() > 4) {
                        if(cnt==0 || cnt ==1) {
                            mp2.start();
                            cnt++;
                        }
                        Log.e("요기", "traffic light 10m nearby");

                        }
                    if(oneBeacon.getDistance() < 1) {
                        if(cnt==0 || cnt ==1) {
                            mp3.start();
                            cnt++;
                        }
                        Log.e("요기", "traffic light nearby");
                    }
                } */
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
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

            if ((str.contains("지금")) || str.contains("어디야") || str.contains("가까운")) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("TrafficLight");
                query.whereEqualTo("Major", 501);
                query.whereEqualTo("Minor", 4390);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {

//                            int id = object.getInt("ID");
                            String loc = "지금 가장 가까운 신호등은 " + object.getString("Location") + "입니다";

//                            Toast.makeText(BeaconMain.this, "you said, "+ str, Toast.LENGTH_SHORT).show();
                            Toast.makeText(FinalActivity.this, loc, Toast.LENGTH_LONG).show();
//                    Toast.makeText(BeaconMain.this, "20m 이내에 신호등이 있습니다", Toast.LENGTH_LONG).show();
                            if (!tts.isSpeaking()) {

                                tts.speak(loc, TextToSpeech.QUEUE_FLUSH, null);

                            }

                        } else {
                            // something went wrong

                            Toast.makeText(FinalActivity.this, "error occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }

            if ((str.contains("신호등")) || str.contains("어느") || str.contains("방향")) {



                ParseQuery<ParseObject> query = ParseQuery.getQuery("TrafficLight");
                query.whereEqualTo("Major", 501);
                query.whereEqualTo("Minor", 4390);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {

//                            int id = object.getInt("ID");
                            String loc = object.getString("Direction") + "입니다";


                            Toast.makeText(FinalActivity.this, loc, Toast.LENGTH_LONG).show();
//                    Toast.makeText(BeaconMain.this, "20m 이내에 신호등이 있습니다", Toast.LENGTH_LONG).show();
                            if (!tts.isSpeaking()) {

                                tts.speak(loc, TextToSpeech.QUEUE_FLUSH, null);

                            }

                        } else {
                            // something went wrong

                            Toast.makeText(FinalActivity.this, "error occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }


            if ((str.contains("반대편")) || str.contains("거리") || str.contains("어떻게")) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("TrafficLight");
                query.whereEqualTo("Major", 501);
                query.whereEqualTo("Minor", 4390);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {

//                            int id = object.getInt("ID");
                            String loc = "반대편까지의 거리는 약 " + object.getInt("Distance_to_cross") + "걸음 입니다";

//                            Toast.makeText(BeaconMain.this, "you said, "+ str, Toast.LENGTH_SHORT).show();
                            Toast.makeText(FinalActivity.this, loc, Toast.LENGTH_LONG).show();
//                    Toast.makeText(BeaconMain.this, "20m 이내에 신호등이 있습니다", Toast.LENGTH_LONG).show();
                            if (!tts.isSpeaking()) {

                                tts.speak(loc, TextToSpeech.QUEUE_FLUSH, null);

                            }

                        } else {
                            // something went wrong

                            Toast.makeText(FinalActivity.this, "error occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }

            if ((str.contains("경로")) || str.contains("검색")) {

                Intent i;
                i = new Intent(FinalActivity.this, UMainActivity.class);
                startActivity(i);

            }



            if ((str.contains("명령어")) || str.contains("도와줘") || str.contains("알려줘")) {

                if (!tts.isSpeaking() ) {
                    String text = "경로검색을 원하시면 '경로검색', 보호자에게 실시간 위치를 알리시려면, '실시간 위치정보 문자', 보호자에게 전화를 원하시면 '보호자에게 전화'라고 말씀해주세요";
                   tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }


            if (str.contains("전화")) {


                // make a phone call
                if (!tts.isSpeaking() ) {
                    String text = "보호자에게 전화합니다";
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "01099028306"));
                startActivity(intent);

            }


            if ((str.contains("실시간") || str.contains("위치") || str.contains("정보") || str.contains("지도"))) {

                // send SMS code

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("01099028306", null, "eyEscort안내 링크(보호자용)- https://eyescort.parseapp.com", null, null);
                    Toast.makeText(getApplicationContext(), "보호자에게 SMS가 발송되었습니다",
                            Toast.LENGTH_SHORT).show();
                    String text = "보호자에게 실시간 위치 지도링크가 발송되었습니다";
                    if (!tts.isSpeaking() ) {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS 발송에 실패하였습니다",
                            Toast.LENGTH_SHORT).show();
                    String text = "SMS 발송에 실패하였습니다";
                    if (!tts.isSpeaking() ) {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    e.printStackTrace();

                }


                // till here
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





    // till here


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
                    String text = "보호자에게 실시간 이동경로 표시링크가 발송되었습니다";
                    if (!tts.isSpeaking() ) {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS 발송에 실패하였습니다",
                            Toast.LENGTH_SHORT).show();
                    String text = "SMS 발송에 실패하였습니다";
                    if (!tts.isSpeaking() ) {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    e.printStackTrace();

                }
        }
    }

    @Override
    public void onInit(int i) {
        // start "아이에스코트 안내를 시작합니다" 안내 message as soon as the activity is loaded
        if(num_of_start==1)
        {
            speakOut();
        }
        else {
            speakOut2();
        }
    }

    // 안내음성
    private void speakOut() {

        String text = "아이에스코트 안내를 시작합니다";
        if (!tts.isSpeaking() ) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void speakOut2() {

        String text = "아이에스코트 안내를 시작합니다";
        if (!tts.isSpeaking() ) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_search_btn:                  // stt is an id for ImageView in activity_main.xml in res -> layout folder

//                String text = "경로검색 페이지로 이동합니다";
//                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;

            case R.id.stt:




                // if button is pressed, execute the following codes
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


                String greeting = "무엇을 도와드릴까요?";
                if (!tts.isSpeaking() ) {

                    tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, null);
                }


                break;


        }
    }




}
