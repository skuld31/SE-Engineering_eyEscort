package com.example.gunhee.eyescort_guidance;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;


public class MapActivity extends NMapActivity implements NMapView.OnMapStateChangeListener {

    NGeoPoint koreaUniversity = new NGeoPoint(127.0318122, 37.5899103);
    NMapViewerResourceProvider mMapViewerResourceProvider;
    NMapOverlayManager mOverlayManager;
    NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener;

    public static final String API_KEY = "b95de12d5821eca3369d1bf599a28f8b";
    NMapView mMapView = null;
    NMapController mMapController = null;
    FrameLayout MapContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapContainer = (FrameLayout) findViewById(R.id.layOut);
        mMapView = new NMapView(this);
        mMapController = mMapView.getMapController();
        mMapView.setApiKey(API_KEY);
        MapContainer.addView(mMapView);
        mMapView.setClickable(true);
        mMapView.setBuiltInZoomControls(true, null);
        mMapView.setOnMapStateChangeListener(this);

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        testOverlayMaker();
        testOverlayPath();

    }

    private void testOverlayMaker() {
        int markerId = NMapPOIflagType.PIN;
        NMapPOIdata poiData = new NMapPOIdata(3, mMapViewerResourceProvider);
        poiData.beginPOIdata(3);
        poiData.addPOIitem(127.0312861, 37.5843480, "우현종합광고 앞 횡단보도", markerId, 0);
        poiData.addPOIitem(127.0294927, 37.5862300, "렌즈스토리고려대점 앞 횡단보도", markerId, 0);
        poiData.addPOIitem(127.02924, 37.5863814, "대학약국 앞 횡단보도", markerId, 0);
        poiData.endPOIdata();
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.showAllPOIdata(0);
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

    }

    private void testOverlayPath() {
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(2);
        poiData.addPOIitem(127.0323364, 37.5835299,"안암골벽산아파트",NMapPOIflagType.FROM, 0);
        poiData.addPOIitem(127.028772, 37.5863814,"안암역1번출구",NMapPOIflagType.TO,0);
        poiData.endPOIdata();
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.showAllPOIdata(0);
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

        NMapPathData pathData = new NMapPathData(12);
        pathData.initPathData();
        pathData.addPathPoint(127.0323364, 37.5835299, NMapPathLineStyle.TYPE_DASH);

        pathData.addPathPoint(127.0329164, 37.5835299, NMapPathLineStyle.TYPE_SOLID);

        pathData.addPathPoint(127.0328000, 37.5838700, 0);
        pathData.addPathPoint(127.0318549, 37.5838701, 0);

        pathData.addPathPoint(127.0311021, 37.5842074, 0);
        pathData.addPathPoint(127.0312861, 37.5843480, 0);

        pathData.addPathPoint(127.0310178, 37.5845631, 0);
        pathData.addPathPoint(127.0314261,37.5850059, 0);
        pathData.addPathPoint(127.0303205, 37.5862300, 0);

        pathData.addPathPoint(127.0294927, 37.5862300, 0);
        pathData.addPathPoint(127.02924, 37.5863814, 0);
        pathData.addPathPoint(127.028772, 37.5863814, 0);
        pathData.endPathData();

        NMapPathDataOverlay pathDataOverlay = mOverlayManager.createPathDataOverlay(pathData);
    }

    public void backClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton3:
                Intent i = new Intent(MapActivity.this, MainActivity.class);
                startActivity(i);
                break;
        }
    }

    public void mapClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                Intent i = new Intent(MapActivity.this, FinalActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        if (nMapError == null) {
            mMapController.setMapCenter(127.0304972, 37.5849396,13);

        }else {
            android.util.Log.e("NMAP","onMapInitHandler: error="+nMapError.toString());
        }

    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i2) {

    }
}
