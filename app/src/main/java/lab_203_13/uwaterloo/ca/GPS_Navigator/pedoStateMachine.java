package lab_203_13.uwaterloo.ca.GPS_Navigator;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Frank Liang on 2016-05-17.
 */

public class pedoStateMachine {
    private String pedoState = null;

    private float oldY,currentY, yJerk, oldZ, currentZ, samplingTime;
    private float safetyThreshold = 3.5f;
    private float yINITIAL_THRESHOLD = 0.45f;
    private float yFINAL_THRESHOLD = -0.15f;

    private Timer refreshTimer = new Timer();
    private TimerTask refreshTask = new refreshTask();

    private boolean firstTime = true;
    public boolean isStep = false;
    private int stepCounter = 0;

    public pedoStateMachine(){
        this.pedoState = "INITIAL";
    }

    public void checkState(float currentY, float currentZ, float samplingTime){
        if(firstTime){
            firstTime = false;
            oldY = currentY;
            oldZ = currentZ;
        }else{
            oldY = this.currentY;
            oldZ = this.currentZ;

            this.currentY = currentY;
            this.currentZ = currentZ;
            this.samplingTime = samplingTime;

            checkTransition();
        }
    }

    private void checkTransition(){
        yJerk = getJerk(oldY, currentY, samplingTime);
        if(checkSafety(currentY) || checkSafety(currentZ)|| Math.abs(yJerk) > 150){
            pedoState = "INITIAL";
            return;
        }

        switch(pedoState){
            case "INITIAL":
                if((yJerk > 0)  && (currentY > yINITIAL_THRESHOLD)){
                    refreshTimer.cancel();
                    refreshTimer = new Timer();
                    refreshTask = new refreshTask();
                    refreshTimer.schedule(refreshTask, 500);

                    pedoState = "tPEAK";
                }
                break;
            case "tPEAK":
                if((yJerk < 0) && (currentY > yINITIAL_THRESHOLD)){
                    pedoState = "FALL";
                }
                break;
            case "FALL":
                if((yJerk < 0) && (currentY < yINITIAL_THRESHOLD && currentY > yFINAL_THRESHOLD )){
                    pedoState = "bPEAK";
                }
                break;
            case "bPEAK":
                if((yJerk < 0) && (currentY < yFINAL_THRESHOLD)){
                    stepCounter++;
                    isStep = true;
                    pedoState = "INITIAL";
                }
                break;
            default:
                System.err.println("ERROR: Cannot find a state for switch, check the State initialization");
        }
    }

    public boolean getIsStep(){
        return isStep;
    }

    public void setIsStepFalse(){
        isStep = false;
    }

    private float getJerk(float iAccel, float fAccel, float samplingTIme){
        float deltaAccel = (fAccel - iAccel)/samplingTIme;
        return deltaAccel;
    }

    public int getStepCount(){
        return this.stepCounter;
    }
    public String getState(){
        return this.pedoState;
    }

    public void resetStateMachine(){
        pedoState = "INITIAL";
        stepCounter = 0;
    }

    public boolean checkSafety(float value){
        if(Math.abs(value) > safetyThreshold){
            return true;
        }
        return false;
    }

    private class refreshTask extends TimerTask{
        @Override
        public void run() {
            if(pedoState != "INITIAL"){
                pedoState = "INITIAL";
            }
        }
    }

}


