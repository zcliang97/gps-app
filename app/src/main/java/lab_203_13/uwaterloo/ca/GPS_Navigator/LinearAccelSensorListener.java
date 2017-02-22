package lab_203_13.uwaterloo.ca.GPS_Navigator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


/**
 * Created by Frank Liang on 2016-05-27.
 *
 * Accelerometer listener. Gets max and current values of the acceleration to the graphView
 */

public class LinearAccelSensorListener implements SensorEventListener{

    private float startTime = System.nanoTime();
    private float endTime = System.nanoTime();
    private float samplingTime = 0;
    private float alpha;
    private float timeConstant = 0.18f;

    private float[] eventValues = new float[3];

    private int count = 1;

    private pedoStateMachine SM = new pedoStateMachine();

    public LinearAccelSensorListener( ){}

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

            //A lowpass filter to get rid of noise.
            endTime = System.nanoTime();
            samplingTime = 1 / (count / ((endTime - startTime) / 1000000000.0f));
            alpha = samplingTime/(samplingTime + timeConstant);
            count++;

            //filter on the accelerometer values
            eventValues[0] = eventValues[0] + alpha * (event.values[0] - eventValues[0]);
            eventValues[1] = eventValues[1] + alpha * (event.values[1] - eventValues[1]);
            eventValues[2] = eventValues[2] + alpha * (event.values[2] - eventValues[2]);

            //Pedometer finite state machine
            SM.checkState(eventValues[1], eventValues[2],samplingTime);
        }
    }

    public int getStepCounter(){
        return SM.getStepCount();
    }

    public pedoStateMachine getStateMachine(){
        return this.SM;
    }

    public boolean getIsMoving(){
        if(Math.abs(eventValues[2]) >= 0.2f ){
            return true;
        }else{
            return false;
        }
    }
}