package exp1.sensor.oda114.sensorapp6.dbscan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

import exp1.sensor.oda114.sensorapp6.kmeans.Point;

/**
 * Created by Oda114 on 5.4.2016.
 */
public class DBScanTest {
    public static int minpoints = 5;
    public static double tdistance = 30;
    public  double x1;
    public  double y1;
    public  Vector<Point> hset = new Vector<Point>();
    public  Vector<List> trl = new Vector<List>();

    public static void main(String[] args) {
        minpoints = 3;
        tdistance = 5;
        DBScanTest dbScanTest = new DBScanTest();
        //dbScanTest.addpoints();
        dbScanTest.applyDbscan();
    }

public  void applyDbscan(){

    this.trl.addAll(dbscan.applyDbscan(hset));

    int index1 = 0;

    for(List l : trl){



        Iterator<Point> j = l.iterator();
        while (j.hasNext()) {
            Point w = j.next();
            System.out.println(w);
        }
        index1++;

    }

}
    public  void addpoints(Point np) {

            hset.add(np);
            System.out.println(hset);
        }


    public Vector<List> getTrl() {
        return trl;
    }

    public void setTrl(Vector<List> trl) {
        this.trl = trl;
    }

    public Vector<Point> getHset() {
        return hset;
    }

    public void setHset(Vector<Point> hset) {
        this.hset = hset;
    }
}
