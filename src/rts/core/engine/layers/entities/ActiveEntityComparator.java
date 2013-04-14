package rts.core.engine.layers.entities;

import rts.Launch;

import java.awt.Point;
import java.util.Comparator;
import java.lang.Math;

/**
 * Created with IntelliJ IDEA.
 * User: Trung
 * Date: 4/4/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 *
 * Compares two active entities based on their distance away from a point
 */
public class ActiveEntityComparator implements Comparator<ActiveEntity>{
    private Point point;

    public ActiveEntityComparator(Point point){
        this.point = point;
    }

    /**
     * Compares two entities based on a distance,
     * @param o1
     * @param o2
     * @return 1 if o1 is farther, 0 if equal, -1 if o1 is shorter
     */
    @Override
    public int compare(ActiveEntity o1, ActiveEntity o2) {
        double distance1, distance2;
        // note this compares actual distances, not tile distances
        // don't think it should matter though
        distance1 = Math.pow(o1.getX() / Launch.g.getEngine().getTileW() - point.getX(), 2) + Math.pow(o1.getY() / Launch.g.getEngine().getTileH() - point.getY(), 2);
        distance2 = Math.pow(o2.getX() / Launch.g.getEngine().getTileW() - point.getX(), 2) + Math.pow(o2.getY() / Launch.g.getEngine().getTileH() - point.getY(), 2);
        if(distance1 > distance2)
            return 1;
        else if(distance1 < distance2)
            return -1;
        else
            return 0;
    }

    /**
     * NOTE I DID NOT IMPLEMENT THIS
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
