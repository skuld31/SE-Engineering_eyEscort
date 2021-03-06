package com.example.gunhee.eyescort_guidance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.parse.Parse;

/**
 * Created by gunhee on 2015-11-13.
 */


public class LoadingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

//        // Parse
//        Parse.initialize(this, "9DEuwzU9jxQRuA7FghaslDVT72WysCYvLRrm1Cxo", "LWYAzhDfKf11TrgVwLIup4tkg1cgbPo5uIjqcvMT");

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler() , 2500); // 3초 후에 hd Handler 실행
    }

    private class splashhandler implements Runnable{
        public void run() {
            startActivity(new Intent(getApplication(), FinalActivity.class)); // 로딩이 끝난후 이동할 Activity
            LoadingActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
        }
    }
}
