package com.example.gunhee.eyescort_guidance;

import com.parse.Parse;

/**
 * Created by STEP on 15. 12. 8..
 */
public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //This will only be called once in your app's entire lifecycle.
        Parse.initialize(this, "9DEuwzU9jxQRuA7FghaslDVT72WysCYvLRrm1Cxo","LWYAzhDfKf11TrgVwLIup4tkg1cgbPo5uIjqcvMT");

    }

}
