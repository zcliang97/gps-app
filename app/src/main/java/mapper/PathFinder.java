package mapper;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;


/*For path finding:
    1. Initialize PathFinder Object passing in current MapView and its NavigationalMap object
    2. Call Draw Path with the Initial start point and end point
    Note: DO NOT CALL DRAW PATH ON EVERY STEP OF THE PERSON, this shit will break your phone cuz of how shit the code is.
 */
public class PathFinder {
    private NavigationalMap nm;
    private MapView mv;
    private PointF startPoint = null;
    private PointF endPoint = null;
    //This can be optimized, but w.e.
    private boolean[][] visitedArray = new boolean[200][200];

    private List<PointF> path = new ArrayList();

    public PathFinder(MapView mv, NavigationalMap mapNav){
        this.nm = mapNav;
        this.mv = mv;
    }

    public void drawPath(PointF start, PointF end){
        startPoint = start;
        endPoint = end;

        findPath(start, start, Math.round(start.x*10), Math.round(start.y*10));
        path.add(0,startPoint);
        mv.setUserPath(path);

        //Reset Visited Array for next path calculations
        path.clear();
        for(int x = 0; x < 200; x++){
            for(int y = 0; y < 200; y ++){
                visitedArray[x][y] = false;
            }
        }
    }

    //Recursively explore and find a path
    public boolean findPath(PointF start, PointF end, int xMove, int yMove){
        //If "Node" is visisted, or if we encounter a wall, return.
        if(visitedArray[xMove][yMove] || nm.calculateIntersections(start,end).size() != 0) {
            return false;
        }
        visitedArray[xMove][yMove] = true;

        //If no objects in between, end recursion.
        if(nm.calculateIntersections(start, this.endPoint).size() == 0){
            path.add(0,this.endPoint);
            return true;
        }

        //Based on position of destination relative to the start, use only certain movements.
        if(displacementBetween(start,endPoint) == 1){
            if(findPath(end, new PointF(end.x+0.1f, end.y), xMove+1, yMove)){
                path.add(0,new PointF(end.x+0.1f, end.y));
                return true;
            }
            if(findPath(end, new PointF(end.x, end.y+0.1f), xMove, yMove+1)){
                path.add(0,new PointF(end.x, end.y+0.1f));
                return true;
            }
            if(findPath(end, new PointF(end.x, end.y-0.1f),xMove, yMove-1)){
                path.add(0,new PointF(end.x, end.y-0.1f));
                return true;
            }
        }else{
            if(findPath(end, new PointF(end.x-0.1f, end.y), xMove-1, yMove)){
                path.add(0,new PointF(end.x-0.1f, end.y));
                return true;
            }
            if(findPath(end, new PointF(end.x, end.y+0.1f), xMove, yMove+1)){
                path.add(0,new PointF(end.x, end.y+0.1f));
                return true;
            }
            if(findPath(end, new PointF(end.x, end.y-0.1f),xMove, yMove-1)){
                path.add(0,new PointF(end.x, end.y-0.1f));
                return true;
            }
        }
        return false;
    }

    //Calculates Where the destination (end) is relative to start
    private int displacementBetween(PointF start, PointF end){
        if(start.x >= end.x){
            return 0;
        }else if(start.x < end.x){
            return 1;
        }
        return -1;
    }
}
