package exp1.sensor.oda114.sensorapp6.kmeans;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oda on 24.3.2016.
 */
public class KMeans {
    //Number of Clusters. This metric should be related to the number of points
    private int NUM_CLUSTERS = 3;
    //Number of Points
    private int NUM_POINTS = 15;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 15;

    private List<Point> points;
    private List<Cluster> clusters;
    ArrayList<Double> distList ;

    public KMeans() {
        this.points = new ArrayList();
        this.clusters = new ArrayList();
    }

    public static void main(String[] args) {

        KMeans kmeans = new KMeans();
        kmeans.init();
        kmeans.calculate();
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    //Initializes the process
    public void init() {

       /* BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader("kmeans.txt"));

            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
                String[] parts = sCurrentLine.split("\t");
                Point p = new Point(Double.parseDouble( parts[0]), Double.parseDouble( parts[1]));
                points.add(p);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }*/
        //Create Points
      //  points = Point.createRandomPoints(MIN_COORDINATE,MAX_COORDINATE,NUM_POINTS);

        //Create Clusters
        //Set Random Centroids
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster cluster = new Cluster(i);
            //Point centroid = Point.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
            Point centroid = points.get(i);
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }

        //Print Initial state
        plotClusters();
    }

    private void plotClusters() {
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster c = clusters.get(i);
            c.plotCluster();
        }
    }

    //The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while(!finish) {
            //Clear cluster state
            clearClusters();

            List<Point> lastCentroids = getCentroids();

            //Assign points to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            List<Point> currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for(int i = 0; i < lastCentroids.size(); i++) {
                distance += Point.distance(lastCentroids.get(i),currentCentroids.get(i));
            }
            System.out.println("#################");
            System.out.println("Iteration: " + iteration);
            System.out.println("Centroid distances: " + distance);
            plotClusters();

            if(distance == 0) {
                finish = true;
            }
        }
    }

    private void clearClusters() {
        for(Cluster cluster : clusters) {
            cluster.clear();
        }
    }
    void getIndex(KMeans kMeans){
        int i = 1 ;
        for (int j = 0; j < kMeans.getClusters().get(i).getPoints().size(); j ++){
            int index = this.points.indexOf( kMeans.getClusters().get(i).getPoints().get(j));
            System.out.println(index);
        }
    }

     public void clusterQuality(KMeans kMeans){
        // küme merkezi ve o kümenin en son bir birlerine olan uzaklıkları hesaplanıyor. (centroid, points)

        this.distList = new ArrayList();
        double d1 = 0;
        for (int i = 0 ; i < NUM_CLUSTERS ; i ++ ){
            int kümeElemanSayısı= kMeans.getClusters().get(i).getPoints().size();
            for (int j = 0 ; j < kMeans.getClusters().get(i).getPoints().size(); j ++)
            {
                Point p1 = kMeans.getClusters().get(i).getCentroid();
                Point p2 = (Point) kMeans.getClusters().get(i).getPoints().get(j);
                // double a = distList.get(i) + Point.distance2(p1, p2);
                d1 = d1 + Point.distance(p1, p2);
                // distList.add(i, a);
                //System.out.println(distList.get(i));
            }
            System.out.println(d1);
//            distList.add(d1);
            double d2 = d1 / kümeElemanSayısı;
            distList.add(d2);


        }
        System.out.println(distList);
    }


    private List getCentroids() {
        List<Point> centroids = new ArrayList(NUM_CLUSTERS);
        for(Cluster cluster : clusters) {
            Point aux = cluster.getCentroid();
            Point point = new Point(aux.getX(),aux.getY());
            centroids.add(point);
        }
        return centroids;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        double distance = 0.0;

        for(Point point : points) {
            min = max;
            for(int i = 0; i < NUM_CLUSTERS; i++) {
                Cluster c = clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }

    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Point> list = cluster.getPoints();
            int n_points = list.size();

            for(Point point : list) {
                sumX += point.getX();
                sumY += point.getY();
            }

            Point centroid = cluster.getCentroid();
            if(n_points < 0) {
                double newX = sumX / n_points;
                double newY = sumY / n_points;
                centroid.setX(newX);
                centroid.setY(newY);
            }
        }
    }

    public List<Cluster> getClusters() {
        return clusters;
    }
}
