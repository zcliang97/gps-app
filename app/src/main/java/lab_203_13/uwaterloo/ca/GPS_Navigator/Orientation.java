package lab_203_13.uwaterloo.ca.GPS_Navigator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mapper.MapView;
import mapper.NavigationalMap;

/**
 * Created by Frank Liang on 2016-06-07.
 */

public class Orientation implements SensorEventListener{
    private float[] rotationMatrix, inclineMatrix ,orientationMatrix, newOrientationMatrix, accelerationValues, magneticValues, gyroValues;
    private float[] linAccelerationValues;

    private float startTime = System.nanoTime();
    private float endTime;
    private float samplingTime = 0;
    private float alpha;
    private float timeConstant = 0.18f;
    private float averageAngle = 0;
    private List<Float> angleArrayList = new ArrayList<>();

    private boolean rotating = false;

    private int count = 1;

    private TextView outputView = null;
    private MapView map;
    private LinearAccelSensorListener accelSensorListener = new LinearAccelSensorListener();
    private DirectionalStep directionalStep;
    private NavigationalMap nm;

    private String output = null;
    private String errorM = "";

    public Orientation(TextView currentView, LinearAccelSensorListener accelSensorListener, MapView map, NavigationalMap nm){
        this.outputView = currentView;
        this.accelSensorListener = accelSensorListener;
        this.map = map;
        this.nm = nm;
        this.directionalStep = new DirectionalStep(this, map, nm);

        this.rotationMatrix = new float[9];
        this.inclineMatrix = new float[9];
        this.orientationMatrix = new float[3];
        this.newOrientationMatrix = new float[3];
        this.accelerationValues = new float[3];
        this.magneticValues = new float[3];
        this.linAccelerationValues = new float[3];
        this.gyroValues = new float[3];
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Setup for alpha calculation
        endTime = System.nanoTime();
        samplingTime = 1 / (count / ((endTime - startTime) / 1000000000.0f));
        alpha = samplingTime/(samplingTime + timeConstant);
        count++;

        //Update sensor values respectively
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerationValues = event.values;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magneticValues = event.values;
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyroValues = event.values;
        }

        //Grabbing rotationMatrix and feeding it into getOrientation to fill orientationMatrix
        SensorManager.getRotationMatrix(rotationMatrix, inclineMatrix, accelerationValues, magneticValues);
        SensorManager.getOrientation(rotationMatrix, newOrientationMatrix);

        //Filter each orientation value, if the change is almost instantaneous, do not filter, set it as the new value.
        for(int counter = 0; counter <3; counter ++){
            if(Math.abs((newOrientationMatrix[counter] - orientationMatrix[counter])) > 6 || rotating){
                orientationMatrix[counter] = newOrientationMatrix[counter];
                newOrientationMatrix[counter] = 0;
            }else{
                orientationMatrix[counter] = orientationMatrix[counter] + alpha * (newOrientationMatrix[counter] - orientationMatrix[counter]);
            }
        }

        double trueVal = 0.0;

        if(Math.toDegrees(orientationMatrix[0]) - 9 < -180){
            trueVal =  Math.toDegrees(orientationMatrix[0])-9-180;
        }
        else{
            trueVal = Math.toDegrees(orientationMatrix[0]) -9;
        }

        MainActivity.compass.setRotation(-(float) trueVal);

        //Averaging Values during the taken step then inform directionStep that a step has been taken and the averaged angle is ready to be evaluated.
        if(accelSensorListener.getStateMachine().getIsStep() != true){
            if(angleArrayList.size() >1){
                if((orientationMatrix[0] - angleArrayList.get(angleArrayList.size()-1) <= 6.28f)) {
                    angleArrayList.add(orientationMatrix[0]);
                }
            }else{
                angleArrayList.add(orientationMatrix[0]);
            }
        }else if(accelSensorListener.getStateMachine().getIsStep() == true){
            averageAngle = 0;
            for(int counter = 0; counter < angleArrayList.size(); counter++) {
                averageAngle += angleArrayList.get(counter);
            }

            averageAngle = averageAngle /angleArrayList.size();
            directionalStep.directionalStep(averageAngle);

            angleArrayList.clear();
            accelSensorListener.getStateMachine().setIsStepFalse();
        }

        //Output
        output = "------------------Orientation Values-------------------" + "\n" + "AZIMUTH: "+ radToDeg(orientationMatrix[0]) + "\nROLL: "+ radToDeg(orientationMatrix[1]) + "\nPITCH: " + radToDeg(orientationMatrix[2])
                  + "\nNorthSouth (Y): " + directionalStep.getTotalNorth() + "\nEastWest(X): " + directionalStep.getTotalWest() + "\nSteps: " + accelSensorListener.getStepCounter() + "\nStepCounter Changed?: " + accelSensorListener.getStateMachine().getIsStep()
                  +"\nDiretionalStepError: " + directionalStep.getMessage() + "\nOrientationError: " + errorM + "\n" + directionalStep.getText()
                  +"\nAverage Angle: "+ radToDeg(averageAngle);
        outputView.setText(output);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private float radToDeg (float radians){
        return (float) (180f/Math.PI)*radians;
    }

    public void clear(){
        angleArrayList.clear();
        averageAngle = 0;
        directionalStep.clear();
    }
}
