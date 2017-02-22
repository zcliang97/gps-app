package lab_203_13.uwaterloo.ca.GPS_Navigator;

import android.graphics.PointF;
import android.util.Log;

import mapper.MapView;
import mapper.NavigationalMap;

/**
 * Created by Frank Liang on 2016-06-23.
 */
public class DirectionalStep {

    public static float angle = 0;

    private final float LENGTH_OF_NORMAL_STEP = 0.6f;
    private final float DESTINATION_RANGE = 1f;

    private float angleInDegrees;
    private float stepNorthSouth = 0;
    private float stepEastWest = 0;
    private float totalNorthSouth = 0;
    private float totalEastWest = 0;

    private String error = "";

    private Orientation orientation;
    private MapView mapView;
    private String text;
    private NavigationalMap nm = null;

    public DirectionalStep(Orientation orientation, MapView mapView, NavigationalMap nm){
        angleInDegrees = 0;
        stepNorthSouth = 0;
        stepEastWest = 0;

        this.orientation = orientation;
        this.mapView = mapView;
        this.nm = nm;
    }

    public void directionalStep(float inputAngle){
        takeStep(inputAngle);
    }

    //Changes angle into heading
    public void takeStep(float inputAngle){

        stepNorthSouth = (float) Math.cos(inputAngle);
        stepEastWest = (float) Math.sin(inputAngle);

        if(nm.calculateIntersections(mapView.getUserPoint(),new PointF(mapView.getUserPoint().x + stepNorthSouth,mapView.getUserPoint().y + stepEastWest)).size() == 0) {
            mapView.getUserPoint().x += (stepNorthSouth * LENGTH_OF_NORMAL_STEP);
            mapView.getUserPoint().y += (stepEastWest * LENGTH_OF_NORMAL_STEP);
            mapView.invalidate();
        }

        String output = "Please continue walking in the indicated path.";
        if(Math.abs(mapView.getUserPoint().x - mapView.getDestinationPoint().x)<=DESTINATION_RANGE && Math.abs(mapView.getUserPoint().y - mapView.getDestinationPoint().y) <= DESTINATION_RANGE){
            output = "You have reached your destination.";
        }
        text = output;

        Log.v("X - dif: ", "" + Math.abs(mapView.getUserPoint().x - mapView.getDestinationPoint().x));
        Log.v("Y - dif: ", "" + Math.abs(mapView.getUserPoint().y - mapView.getDestinationPoint().y));

        Log.v("Change in Point: ", "X: " + stepEastWest + "Y: " + stepNorthSouth);
        Log.v("New Point: ", "X: " + mapView.getUserPoint().x + "Y: " + mapView.getUserPoint().y);

        float xUser = mapView.getUserPoint().x;
        float yUser = mapView.getUserPoint().y;

        float xDest = mapView.getDestinationPoint().x;
        float yDest = mapView.getDestinationPoint().y;

        float xTotal = xDest - xUser;
        float yTotal = yDest - yUser;

        angle = (float)Math.atan((double)yTotal/(double)xTotal);

        if(mapView.getUserPoint().x>mapView.getDestinationPoint().x){
            MainActivity.direction.setRotation((float)Math.toDegrees(angle)+270);
        }
        else{
            MainActivity.direction.setRotation((float)Math.toDegrees(angle)+90);
        }
    }

    public String getText(){
        return text;
    }

    //Clear Coordinates
    public void clear(){
        totalNorthSouth = 0;
        totalEastWest = 0;
        mapView.getUserPoint().x = mapView.getOriginPoint().x;
        mapView.getUserPoint().y = mapView.getOriginPoint().y;
        mapView.invalidate();
        text = "Please continue walking in the direction indicated.";
    }

    //For testing purposes
    public String getMessage(){
        return error;
    }

    public float getTotalNorth(){
        return totalNorthSouth;
    }

    public float getTotalWest(){
        return totalEastWest;
    }
}
