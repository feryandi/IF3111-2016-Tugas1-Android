package in.feryand.locationfinder;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    static final LatLng defaultLocation = new LatLng(-6.8899f, 107.6100f);
    private GoogleMap googleMap;

    private ImageView compass;
    private float compassDeg = 0f;
    private SensorManager mSensorManager;

    private float mDeviceOrientation;
    private float phoneOrientation = 0;

    private OrientationEventListener mOrientationEventListener;

    RotateAnimation ra = new RotateAnimation(
            compassDeg,
            0,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Map");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = (ImageView) findViewById(R.id.iv_compass);

        OrientationEventListener mOrientationEventListener =
                new OrientationEventListener(this, SensorManager.SENSOR_DELAY_GAME) {
                    @Override
                    public void onOrientationChanged(int orientation){
                        mDeviceOrientation = orientation;
                    }
                };

        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(i);
            }
        });

        FloatingActionButton fab_answer = (FloatingActionButton) findViewById(R.id.fab_answer);
        fab_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AnswerActivity.class);
                startActivity(i);
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap();
    }

    public void setUpMap(){
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 16.0f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if ( getResources().getConfiguration().orientation == 1 ) {
            googleMap.setPadding(20, 0, 20, 150);
        } else {
            googleMap.setPadding(0, 0, 150, 0);
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
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        float orientation = (90*Math.round(mDeviceOrientation/90))%360;

        boolean isOrientationEnabled;

        try {
            isOrientationEnabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION) == 1;
        } catch (Settings.SettingNotFoundException e) {
            isOrientationEnabled = false;
        }

        if ( ( getResources().getConfiguration().orientation != 1 ) && ( isOrientationEnabled ) ) {
            if ( (orientation == 270) || (phoneOrientation == 270) ) {
                if(phoneOrientation == 90) {
                    degree += 90;
                }
                phoneOrientation = 270;
                degree += 90;
            }
            if ( (orientation == 90) || (phoneOrientation == 90) ){
                if(phoneOrientation == 270) {
                    degree -= 90;
                }
                phoneOrientation = 90;
                degree -= 90;
            }
        } else {
            phoneOrientation = 0;
            degree += 0;
        }

        ra = new RotateAnimation(
                compassDeg,
                (-degree),
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(200);
        ra.setFillAfter(true);

        compass.startAnimation(ra);
        compassDeg = -degree;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            compass = (ImageView) findViewById(R.id.iv_compass);
            compass.startAnimation(ra);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
