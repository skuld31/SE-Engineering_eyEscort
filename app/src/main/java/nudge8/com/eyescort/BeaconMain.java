package nudge8.com.eyescort;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;


public class BeaconMain extends ActionBarActivity implements BeaconConsumer {

//    Create objects for each clss
    Intent SpeechIntent;
    SpeechRecognizer mRecognizer;
    MediaPlayer mp1;
    MediaPlayer mp2;
    MediaPlayer mp3;


    Timer timer;
    TimerTask myTask;
    int cnt;

    String str = "";

    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // load activity_main in layout folder for the first activity

        // The following parts are for voice recognition
        SpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        // The following is a way to use the created object, mp1, to connect with voice recording file, 'check.m4a'
        // 'check.m4a' file is in raw folder in res folder and FYI, by default, 'raw' folder does not exist in res folder
        // but is usually created for storing sound recording files
        mp1 = MediaPlayer.create(this, R.raw.twenty);
        mp2 = MediaPlayer.create(this, R.raw.ten);
        mp3 = MediaPlayer.create(this, R.raw.arrived);

        timer = new Timer(); // this is later used as the waiting time after voice recognition button has been pressed

        // The following codes are for creating instance for BeaconManager class
        // This is not part of Android Studio API
        // So, this source is imported from 'android-beacon-library-2.6.1' as external library
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

    }




    // Part I. Beacon Functionalities


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

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
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon oneBeacon : beacons) {
                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                    Log.e(TAG, "distance:" + oneBeacon.getDistance());
                    Log.e(TAG, "count:" + cnt);


                    if (oneBeacon.getDistance() < 20 && oneBeacon.getDistance() > 15) { // if the distance to beacon is less than 20 m ...
                        if(cnt == 0) {
                            mp1.start();                            // play sound recording
                            cnt++;
                        }
                        Log.e("요기", "traffic light 20m nearby");

                        // and print the Log to inform us that it was activated
                    }

                    else if(oneBeacon.getDistance() < 9 && oneBeacon.getDistance() > 4) {
                        if(cnt == 0 || cnt == 1) {
                            mp2.start();
                            cnt++;
                        }
                        Log.e("요기", "traffic light 10m nearby");

                    }

                    if(oneBeacon.getDistance() < 1) {
                        if(cnt == 0 || cnt == 1) {
                            mp3.start();
                            cnt++;
                        }
                        Log.e("요기", "traffic light nearby");

                    }
                }
            }


        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
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





    // The below codes are all for Voice Recognition, while the above are for Beacon functionalities




    // Part II. Voice Recognition Functionalities

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stt:                  // stt is an id for ImageView in activity_main.xml in res -> layout folder


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

            // if voice input, after being translated to text contains "hello"
            // then play mp1 and Toast "Hey, how are you?"
            if ((str.contains("hello") && str.contains(""))) {

                mp1.start();
                Toast.makeText(BeaconMain.this, " Hey, how are you?", Toast.LENGTH_LONG).show();
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




}