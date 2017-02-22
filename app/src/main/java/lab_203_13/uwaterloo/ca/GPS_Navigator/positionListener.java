package lab_203_13.uwaterloo.ca.GPS_Navigator;

import android.graphics.PointF;
import android.util.Log;

import mapper.MapView;
import mapper.NavigationalMap;
import mapper.PathFinder;
import mapper.PositionListener;

/**
 * Created by Frank Liang on 2016-05-29.
 */

public class positionListener implements PositionListener {
    private PointF start = null;
    private PointF end = null;
    private NavigationalMap nm = null;
    private PathFinder pf;
    private MapView mv;
    private PointF origin = null;

    public positionListener(MapView mv, NavigationalMap nm){
        System.out.println("TestingClass Constructor");
        this.nm = nm;
        this.pf = new PathFinder(mv,nm);
        this.mv = mv;
    }

    @Override
    public void originChanged(MapView source, PointF loc) {
        this.start = loc;
        System.out.println("Location X: "+loc.x + ", Location Y: "+loc.y);
        if(this.end != null){
            pf.drawPath(this.start, this.end);
        }
        Log.v("Change in Origin: ", "X: " + loc.x + "Y: " + loc.y);

        mv.setUserPoint(loc);

        Log.v("Origin Position: ", "X: " + loc.x + "Y: " + loc.y);

        origin = loc;
    }

    public PointF getOriginPoint(){
        if(origin != null){
            return origin;
        }
        else{
            System.out.println("Error occurred in getting origin point.");
            return new PointF(0f,0f);
        }
    }

    @Override
    public void destinationChanged(MapView source, PointF dest) {
        this.end = dest;
        if(this.start != null){
            pf.drawPath(this.start, this.end);
        }
    }
}
