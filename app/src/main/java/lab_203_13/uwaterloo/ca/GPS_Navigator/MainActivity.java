package lab_203_13.uwaterloo.ca.GPS_Navigator;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

import ca.uwaterloo.sensortoy.LineGraphView;
import mapper.MapLoader;
import mapper.MapView;
import mapper.NavigationalMap;

/**
 * Created by Frank Liang on 2016-05-09.
 */

public class MainActivity extends AppCompatActivity {
    LineGraphView graph;

    //Create the MapView
    MapView mv;
    positionListener pl;

    public static ImageView compass;
    public static ImageView direction;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        mv.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        return super.onContextItemSelected(item) || mv.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_203_13);

        mv = new  MapView(getApplicationContext(), 1080, 750, 60, 60);

        // Creates context for MapView
        registerForContextMenu(mv);

        // Chooses which SVG file to use as map
        NavigationalMap map = MapLoader.loadMap(getExternalFilesDir(null),"Lab-room-peninsula.svg");
        mv.setMap(map);

        LinearLayout layout = (LinearLayout) findViewById(R.id.main);

        compass = (ImageView) findViewById(R.id.compass);
        direction = (ImageView) findViewById(R.id.direction);

        mv.setVisibility(View.VISIBLE);
        layout.addView(mv);

        SensorManager senManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Declaring sensors
        Sensor accelSensor = senManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor linearAccelSensor = senManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor MFSensor = senManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor gyroSensor = senManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //Compass Declaration and registering needed sensors
        graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));

        //Linear Acceleration listener for step counter and registering needed sensors
        final LinearAccelSensorListener linAccelListener = new LinearAccelSensorListener();
        senManager.registerListener(linAccelListener, linearAccelSensor, SensorManager.SENSOR_DELAY_GAME);

        //Adding Position Listener Class Class
        pl = new positionListener(mv, map);
        mv.addListener(pl);

        //Compass Listener
        TextView tv = new TextView(getApplicationContext());
        final Orientation compass = new Orientation(tv, linAccelListener, mv, map);
        senManager.registerListener(compass, accelSensor, SensorManager.SENSOR_DELAY_GAME);
        senManager.registerListener(compass, MFSensor, SensorManager.SENSOR_DELAY_GAME);

        //Gyroscope listener
        senManager.registerListener(compass, gyroSensor, SensorManager.SENSOR_DELAY_GAME);

        tv.setTextColor(Color.BLACK);
        layout.addView(tv);


        //Clear All Button
        Button clearButton = new Button(getApplicationContext());
        clearButton.setText("Clear");
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linAccelListener.getStateMachine().resetStateMachine();
                compass.clear();

            }
        };
        clearButton.setOnClickListener(onClick);

        layout.addView(clearButton);
    }
}
