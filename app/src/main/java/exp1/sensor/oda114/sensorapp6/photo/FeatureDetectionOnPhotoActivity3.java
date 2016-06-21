package exp1.sensor.oda114.sensorapp6.photo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import exp1.sensor.oda114.sensorapp6.R;
import exp1.sensor.oda114.sensorapp6.dbscan.DBScanTest;
import exp1.sensor.oda114.sensorapp6.kmeans.KMeans;
import exp1.sensor.oda114.sensorapp6.kmeans.Point;
import exp1.sensor.oda114.sensorapp6.show.ShowDistanceActivity;

public class FeatureDetectionOnPhotoActivity3 extends AppCompatActivity {


    //public static final String TAG = "Photo Activity ";
    MatOfKeyPoint keyPoints;
    MatOfKeyPoint logokeyPoints;
    public static final int WIDTH = 300;
    public static final int HEIGHT = 300;

    List<DMatch> matchesList;
    DescriptorMatcher matcher;
    MatOfDMatch matches;
    MatOfDMatch gm;
    LinkedList<DMatch> good_matches;
    LinkedList<DMatch> good_matches3;
    LinkedList<DMatch> good_matches4;
    private String imgPath1 = "", imgPath2 = "";
    ArrayList<Double> farkList ;
    ArrayList<Double> farkList3 ;
    ArrayList<Double> farkList4 ;
    boolean neTaraf = true;
    int X,Y;
    HashMap<Point, Double> distMap = new HashMap<>();
    private int KAC_TANE = 2;
    MatOfKeyPoint logokeyPoints3, logokeyPoints4;
    private int BASE_LINE = 5;
    // Opencv Kontrol ve Kod yazma

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    System.loadLibrary("opencv_java");
                    System.loadLibrary("nonfree");
////////////////////
                    ///////////////////
                    ////  3 ve 4 ün yolları gelmiyor. yanı takephotoActivity de ayarlanmaış.
                    // 3 ve 4 ün yolları 2 nin yoluna "3" ve "4" eklenerek atnacak


                    File file1 = new File(Environment.getExternalStorageDirectory(), "AutoExperiment2/" + imgPath1);
                    File file2 = new File(Environment.getExternalStorageDirectory(), "AutoExperiment2/" + imgPath2);
                    Mat image1, image2;
                    Mat image3, image4;
                    File file3 = null, file4 = null;
                    if (KAC_TANE > 2) {

                        file3 = new File(Environment.getExternalStorageDirectory(), "AutoExperiment2/" + imgPath2.substring(0, imgPath2.length() - 4)+"3.jpg");
                    }
                    if (KAC_TANE == 4){

                        file4 = new File(Environment.getExternalStorageDirectory(), "AutoExperiment2/" + imgPath2.substring(0, imgPath2.length() - 4)+"4.jpg");
                    }
                    try {
                        if (file1.exists() && file2.exists() && file3.exists()) {
                            // Resimleri Grayscale olarak okuma

                            /**
                             * 1. çekilen resim image2 içersine alınıyor.
                             * 2. çekilen resim image1 içersine alınıyor.
                             *
                             * */
                            image1 = Imgcodecs.imread(file1.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                            image2 = Imgcodecs.imread(file2.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                            image3 = Imgcodecs.imread(file2.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                            image4 = Imgcodecs.imread(file2.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

                            /**
                             * Eğer 2 den fazla resim çekilmiş ise
                             *
                             * */

                            if (KAC_TANE > 2) {
                                image3 = Imgcodecs.imread(file3.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                            }
                            if (KAC_TANE == 4){
                                image4 = Imgcodecs.imread(file4.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                            }



                            /**
                             * Gelen resimlere dokunulan koordinata göre kırpma işlemi uyulanıyor
                             * İlk resimde dokunulan koordinat çevresi kırpılırken ikinci resimde kameranın hareket yönüne göre
                             * dokunulan korrdinattan önceki yada sonraki kısım kesiliyor.
                             * Böylece gereksiz yerlere bakılmamış olunup işlemlerin daha hızlı yapılması dağlanıyor.
                             * */

                            // Birinci resmi crop etme


                            Rect roi = new Rect(X, Y, WIDTH, HEIGHT);
                            Mat cropped = new Mat(image1, roi);
                            Mat outputImage1 = cropped.clone();


                            // ikinci resmi crop etme
                            // Sağa çekince nesne ikinciresimdce daha sola düşmekte
                            // Bundan dolayı 0 dan dokunulan X değerine kadar olan kısıma bakılalı
                            // neTaraf değişkeni true eğer hareket sağa yapılmışsa
                            // neTaraf değişkeni fase eğer hareket sola yapılmışsa
                            int x = 0;

                            if (neTaraf) {
                                x = X - 400;
                                if (x < 0) x = 0;
                                roi = new Rect(x, Y, 500, HEIGHT); // WIDHT = X + 300
                            } else {
                                roi = new Rect(X, Y, 3264 - X, HEIGHT); // WIDHT = 3264 - X
                            }

                            cropped = new Mat(image2, roi);
                            Mat outputImage2 = cropped.clone();

                            /**
                             * Eğer ikiden fazla ise
                             *
                             * */
                            Mat outputImage3 = null, outputImage4 = null;
                            if (KAC_TANE > 2){

                                cropped = new Mat(image3, roi);
                                outputImage3 = cropped.clone();
                            }
                            if(KAC_TANE == 4){
                                cropped = new Mat(image4, roi);
                                outputImage4 = cropped.clone();
                            }
                            // Kesilen resimler

                            System.out.println(outputImage1);
                            System.out.println(outputImage2);
                            System.out.println(outputImage3);
                            System.out.println(outputImage4);


                            /**
                             * Keypoints ve bunlardan elde edilecek descriptorların hesaplanması
                             * Bu hesaplamalar için SURF algoritması kullanılıyor.
                             * Bu işlem her iki resim içinde aynı şekilde yapılıyor.
                             * Bundan somra iki elde edilen desriptorlar yardımıyla iki resiminkarşılaştırılması yapışlıyor.
                             **/

                            FeatureDetector SURF = FeatureDetector.create(FeatureDetector.FAST);

                            keyPoints = new MatOfKeyPoint();
                            logokeyPoints = new MatOfKeyPoint();

                            //  İki reim içinde keypoints hesabı
                            //  SURF.detect(image1, keyPoints);
                            //  SURF.detect(image2, logokeyPoints);


                            SURF.detect(outputImage2, keyPoints); // 2. çekilen resim için
                            SURF.detect(outputImage1, logokeyPoints); // 1. çekilen resim için

                            /**
                             * Eğer ikiden fazla ise
                             *
                             * */
                            logokeyPoints3 = new MatOfKeyPoint();
                            logokeyPoints4 = new MatOfKeyPoint();

                            if (KAC_TANE > 2){
                                SURF.detect(outputImage3, logokeyPoints3); // 3. çekilen resim için
                            }
                            if(KAC_TANE == 4){
                                SURF.detect(outputImage4, logokeyPoints4); // 4. çekilen resim için
                            }

                            // Ne kadar nokta bulunmuş

                            Size keyPoint = keyPoints.size();
                            Size keyPointLogo = logokeyPoints.size();
                            Size keyPointLogo3 = logokeyPoints3.size();
                            Size keyPointLogo4 = logokeyPoints4.size();

                            System.out.println(keyPoint);
                            System.out.println(keyPointLogo);
                            System.out.println(keyPointLogo3);
                            System.out.println(keyPointLogo4);

                            // Bulunan noktalara göre
                            // Descriptor Hesabının yapılması
                            // SURF algoritması kullanılıyor.

                            DescriptorExtractor SurfExtractor = DescriptorExtractor
                                    .create(DescriptorExtractor.SURF);



                            Mat descriptors = new Mat();
                            Mat logoDescriptors = new Mat();

                            // İki resim içinde desriptor hesabının yapılması
                            // Kesilme yapılmadan önceki kodlar
                            // SurfExtractor.compute(image1, keyPoints, descriptors);
                            // SurfExtractor.compute(image2, logokeyPoints, logoDescriptors);

                            // logoDescriptor 1. resim için
                            // descriptors 2. resim için

                            SurfExtractor.compute(outputImage2, keyPoints, descriptors);
                            SurfExtractor.compute(outputImage1, logokeyPoints, logoDescriptors);

                            Mat logoDescriptors3 = new Mat();
                            Mat logoDescriptors4 = new Mat();


                            /**
                             * Eğer ikiden fazla ise
                             *
                             * */
                            if (KAC_TANE > 2){

                                SurfExtractor.compute(outputImage3, logokeyPoints3, logoDescriptors3);

                            }
                            if(KAC_TANE == 4){

                                SurfExtractor.compute(outputImage4, logokeyPoints4, logoDescriptors4);

                            }


                            /**
                             * İki resimin karşılaştırma işlemi burada yapılıyor
                             *
                             * */

                            gm = new MatOfDMatch();
                            matches = new MatOfDMatch();
                            good_matches = new LinkedList<>();



                            double max_dist = 0;
                            double min_dist = 1000;

                            // FLANBASED matcher kullanılarak karşılaştırma işlemi gerçekleştiriliyor

                            matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
                            try {
                                matcher.match(descriptors, logoDescriptors, matches);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            matchesList = matches.toList();

                            // En uzak ve En yakın mesafeler hesaplanıyor.

                            for (int i = 0; i < descriptors.rows(); i++) {
                                Double dist = (double) matchesList.get(i).distance;
                                if (dist < min_dist) min_dist = dist;
                                if (dist > max_dist) max_dist = dist;
                            }

                            // En iyi eşleşen noktalar bulunuyor.
                            // En yakın mesafenin 2 katı büyüklüğünde olan bütün mesafeler alınıyor.

                            for (int i = 0; i < descriptors.rows(); i++) {
                                if (matchesList.get(i).distance < 2.5 * min_dist) {
                                    good_matches.addLast(matchesList.get(i));
                                }
                            }


                            /**
                             * Eğer ikiden fazla ise
                             *
                             * */

                            max_dist = 0;
                            min_dist = 1000;
                            good_matches3 = new LinkedList<>();
                            good_matches4 = new LinkedList<>();
                            if (KAC_TANE > 2){

                                try {
                                    matcher.match(logoDescriptors3, logoDescriptors, matches);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                matchesList = matches.toList();

                                // En uzak ve En yakın mesafeler hesaplanıyor.

                                for (int i = 0; i < logoDescriptors3.rows(); i++) {
                                    Double dist = (double) matchesList.get(i).distance;
                                    if (dist < min_dist) min_dist = dist;
                                    if (dist > max_dist) max_dist = dist;
                                }

                                // En iyi eşleşen noktalar bulunuyor.
                                // En yakın mesafenin 2 katı büyüklüğünde olan bütün mesafeler alınıyor.

                                for (int i = 0; i < logoDescriptors3.rows(); i++) {
                                    if (matchesList.get(i).distance < 2.5 * min_dist) {
                                        good_matches3.addLast(matchesList.get(i));
                                    }
                                }
                            }
                            max_dist = 0;
                            min_dist = 1000;
                            if(KAC_TANE == 4){

                                try {
                                    matcher.match(logoDescriptors, logoDescriptors4, matches);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                matchesList = matches.toList();

                                // En uzak ve En yakın mesafeler hesaplanıyor.

                                for (int i = 0; i < logoDescriptors4.rows(); i++) {
                                    Double dist = (double) matchesList.get(i).distance;
                                    if (dist < min_dist) min_dist = dist;
                                    if (dist > max_dist) max_dist = dist;
                                }

                                // En iyi eşleşen noktalar bulunuyor.
                                // En yakın mesafenin 2 katı büyüklüğünde olan bütün mesafeler alınıyor.

                                for (int i = 0; i < logoDescriptors4.rows(); i++) {
                                    if (matchesList.get(i).distance < 2.5 * min_dist) {
                                        good_matches4.addLast(matchesList.get(i));
                                    }
                                }
                            }

                            /**
                             * Eşleşen noktalar bulundu.
                             * Eşleşen noktaların pixel koordinatları bulunuyor.
                             *
                             * */


                            gm.fromList(good_matches);

                            // sceneList 1. resim için
                            // objectList 2. resim için

                            List<KeyPoint> keypoints_objectList = keyPoints.toList();
                            List<KeyPoint> keypoints_sceneList = logokeyPoints.toList();


                            /**
                             * Eğer ikiden fazla ise
                             *
                             * */
                            List<KeyPoint> keypoints_objectList3 = null ;
                            List<KeyPoint> keypoints_objectList4 = null ;
                            LinkedList<org.opencv.core.Point> sceneList3 =  new LinkedList<>();;
                            LinkedList<org.opencv.core.Point> sceneList4 =  new LinkedList<>();;
                            LinkedList<org.opencv.core.Point> objList3 =  new LinkedList<>();;
                            LinkedList<org.opencv.core.Point> objList4 =  new LinkedList<>();;
                            if (KAC_TANE > 2){

                                keypoints_objectList3 = logokeyPoints3.toList();
                                objList3 = new LinkedList<>();
                            }
                            if(KAC_TANE == 4){
                                keypoints_objectList4 = logokeyPoints4.toList();
                                objList4 = new LinkedList<>();
                            }
                            LinkedList<org.opencv.core.Point> objList = new LinkedList<>();
                            LinkedList<org.opencv.core.Point> sceneList = new LinkedList<>();

                            KMeans objKMeans = new KMeans();
                            KMeans sceneKMeans = new KMeans();

                            DBScanTest sceneDBScanTest = new DBScanTest();
                            DBScanTest objectDBScanTest = new DBScanTest();


                            double ort = 0;
                            int count = 0;


                            farkList = new ArrayList<>();

                            for (int i = 0; i < good_matches.size(); i++) {

                                // ilk resim kırpıldığı için bulunan noktalar kırpılmış resme göre bulunuyor
                                // bundan dolayı bukunan noktaların X değerlerine X (Gelen X koordinat değeri) eklenmeli

                                sceneList.addLast(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
                                objList.addLast(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);

                                // KMeans algoritmasını kullanabilmek için Point tipinde bir nesne oluşturduk.
                                exp1.sensor.oda114.sensorapp6.kmeans.Point point = null;

                                /**
                                 *
                                 *  Kmeans algoritmasını kullanabilmek için KMeans sınıfından iki nesne türetteik.
                                 *  objKMeans ve sceneKMeans
                                 *  bu iki nesnenin list elemanına eşeleşen en iyi noktaları ekledik.
                                 *  point nesnesinin yapıcı metoduna en iyi eşleşen noktaların x ve y değerlerini göndedik.
                                 *
                                 *  Eğer eşleşen noktalar arasındaki fark negatif ise bu dğerler yanlış eşleitiğinden almıyoruz.
                                 * */

                                double x1 = keypoints_sceneList.get(good_matches.get(i).trainIdx).pt.x;
                                double x2 = keypoints_objectList.get(good_matches.get(i).queryIdx).pt.x;

                                x1 += X;
                                x2 += x;
                                double fark = x1 - x2;
                                if (!neTaraf)
                                    fark = x2 - x1; // Eğer neTaraf değişkeni false ise sola hareket var, true ise sağa hareket var demektir.

                                if ((fark) >= 0) {
                                    ort += (fark);
                                    farkList.add(fark);
                                    count++;

                                    // objList değerleri ekleniyor

                                    point = new exp1.sensor.oda114.sensorapp6.kmeans.Point(keypoints_objectList.get(good_matches.get(i).queryIdx).pt.x + x, keypoints_objectList.get(good_matches.get(i).queryIdx).pt.y + Y);
                                    objKMeans.getPoints().add(point);
                                    objectDBScanTest.getHset().add(point);

                                    // sceneList değerleri ekleniyor

                                    point = new exp1.sensor.oda114.sensorapp6.kmeans.Point(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt.x + X, keypoints_sceneList.get(good_matches.get(i).trainIdx).pt.y + Y);
                                    sceneKMeans.getPoints().add(point);
                                    sceneDBScanTest.getHset().add(point);
                                }
                            }

                            /**
                             * Eğer ikiden fazla ise
                             *
                             * */
                            farkList3 = new ArrayList<>();
                            farkList4 = new ArrayList<>();
                            KMeans sceneKMeans3 = new KMeans();
                            KMeans sceneKMeans4 = new KMeans();
                            if (KAC_TANE > 2){

                                for (int i = 0; i < good_matches3.size(); i++) {

                                    // ilk resim kırpıldığı için bulunan noktalar kırpılmış resme göre bulunuyor
                                    // bundan dolayı bukunan noktaların X değerlerine X (Gelen X koordinat değeri) eklenmeli

                                    sceneList3.addLast(keypoints_sceneList.get(good_matches3.get(i).trainIdx).pt);
                                    objList3.addLast(keypoints_objectList3.get(good_matches3.get(i).queryIdx).pt);

                                    // KMeans algoritmasını kullanabilmek için Point tipinde bir nesne oluşturduk.
                                    exp1.sensor.oda114.sensorapp6.kmeans.Point point = null;

                                    /**
                                     *
                                     *  Kmeans algoritmasını kullanabilmek için KMeans sınıfından iki nesne türetteik.
                                     *  objKMeans ve sceneKMeans
                                     *  bu iki nesnenin list elemanına eşeleşen en iyi noktaları ekledik.
                                     *  point nesnesinin yapıcı metoduna en iyi eşleşen noktaların x ve y değerlerini göndedik.
                                     *
                                     *  Eğer eşleşen noktalar arasındaki fark negatif ise bu dğerler yanlış eşleitiğinden almıyoruz.
                                     * */

                                    double x1 = keypoints_sceneList.get(good_matches3.get(i).trainIdx).pt.x;
                                    double x2 = keypoints_objectList3.get(good_matches3.get(i).queryIdx).pt.x;

                                    x1 += X;
                                    x2 += x;
                                    double fark = x1 - x2;
                                    if (!neTaraf)
                                        fark = x2 - x1; // Eğer neTaraf değişkeni false ise sola hareket var, true ise sağa hareket var demektir.

                                    if ((fark) >= 0) {
                                        ort += (fark);
                                        farkList3.add(fark);

                                        // sceneList değerleri ekleniyor

                                        point = new exp1.sensor.oda114.sensorapp6.kmeans.Point(keypoints_sceneList.get(good_matches3.get(i).trainIdx).pt.x + X, keypoints_sceneList.get(good_matches3.get(i).trainIdx).pt.y + Y);
                                        sceneKMeans3.getPoints().add(point);
                                    }
                                }
                            }
                            if(KAC_TANE == 4){
                                for (int i = 0; i < good_matches4.size(); i++) {

                                    // ilk resim kırpıldığı için bulunan noktalar kırpılmış resme göre bulunuyor
                                    // bundan dolayı bukunan noktaların X değerlerine X (Gelen X koordinat değeri) eklenmeli

                                    sceneList4.addLast(keypoints_sceneList.get(good_matches4.get(i).trainIdx).pt);
                                    objList4.addLast(keypoints_objectList4.get(good_matches4.get(i).queryIdx).pt);

                                    // KMeans algoritmasını kullanabilmek için Point tipinde bir nesne oluşturduk.
                                    exp1.sensor.oda114.sensorapp6.kmeans.Point point = null;

                                    /**
                                     *
                                     *  Kmeans algoritmasını kullanabilmek için KMeans sınıfından iki nesne türetteik.
                                     *  objKMeans ve sceneKMeans
                                     *  bu iki nesnenin list elemanına eşeleşen en iyi noktaları ekledik.
                                     *  point nesnesinin yapıcı metoduna en iyi eşleşen noktaların x ve y değerlerini göndedik.
                                     *
                                     *  Eğer eşleşen noktalar arasındaki fark negatif ise bu dğerler yanlış eşleitiğinden almıyoruz.
                                     * */

                                    double x1 = keypoints_sceneList.get(good_matches4.get(i).trainIdx).pt.x;
                                    double x2 = keypoints_objectList4.get(good_matches4.get(i).queryIdx).pt.x;

                                    x1 += X;
                                    x2 += x;
                                    double fark = x1 - x2;
                                    if (!neTaraf)
                                        fark = x2 - x1; // Eğer neTaraf değişkeni false ise sola hareket var, true ise sağa hareket var demektir.

                                    if ((fark) >= 0) {
                                        ort += (fark);
                                        farkList4.add(fark);

                                        // sceneList değerleri ekleniyor

                                        point = new exp1.sensor.oda114.sensorapp6.kmeans.Point(keypoints_sceneList.get(good_matches4.get(i).trainIdx).pt.x + X, keypoints_sceneList.get(good_matches4.get(i).trainIdx).pt.y + Y);
                                        sceneKMeans4.getPoints().add(point);
                                    }
                                }
                            }


                            /**
                             * Eşeleşen noktalar alındı.
                             * KMeans algoritmsı kullanılarak kümeleme işlemi gerçekleştirirlecek.
                             * İki resim için bulunan eşleşen noktalar kümeleme algoritmasına tabi tutluyor.
                             * */

                            ort = ort / count;
                            System.out.println(ort);
                            objKMeans.init();
                            objKMeans.calculate();

                            sceneKMeans.init(X + 150, Y + 150);
                            sceneKMeans.calculate();


                            int i = sceneKMeans.clusterQuality(sceneKMeans);
                            ort = 0;
                            ArrayList<Double> farkListesi = new ArrayList<>();
                            for (int j = 0; j < sceneKMeans.getClusters().get(i).getPoints().size(); j++) {
                                int index = sceneKMeans.getPoints().indexOf(sceneKMeans.getClusters().get(i).getPoints().get(j));
                                ort += farkList.get(index);
                                farkListesi.add(farkList.get(index));
                                System.out.println(index);
                            }
                            ort = ort / sceneKMeans.getClusters().get(i).getPoints().size();
                            ort /= 10000;
                            TextView txtDistance = (TextView) findViewById(R.id.txtDistance);
                            String output = "";

                            double cm_5 = (0.34 * 5) / ort;

                            distMap.put(sceneKMeans.getClusters().get(i).centroid, cm_5);
                            ArrayList<Double> farkListesiIlkCluster = new ArrayList<>();

                            /**
                             * Bu iki dizi ise ilk kümede;
                             * (ilk kümenin küme merkezini kullanıcının dokunduğu nokta olarak belirlemiştik)7
                             * küme merkezine olan uzaklıkları hesaplıyor
                             *
                             * double enYakınNokta değişkeni ise dokunulan noktaya en yakın olan noktayı bulmak için kullanışacak
                             *
                             * */
                            double enYakınNokta = 3264; // başlangıç değeri
                            int enYakınNoktaIndex = 0; // başlangıç değeri
                            ArrayList<Double> farkListesiIlkClusterFarklar = new ArrayList<>();
                            ArrayList<Double> farkListesiIlkClusterFarklar2 = new ArrayList<>();
                            for (int j = 0; j < sceneKMeans.getClusters().get(0).getPoints().size(); j++) {
                                int index = sceneKMeans.getPoints().indexOf(sceneKMeans.getClusters().get(0).getPoints().get(j));
                                Point p = (Point) sceneKMeans.getClusters().get(0).getPoints().get(j);
                                farkListesiIlkCluster.add(farkList.get(index));
                                farkListesiIlkClusterFarklar.add(Point.distance(p, sceneKMeans.getClusters().get(0).getCentroid()));
                                if (enYakınNokta > Point.distance(p, new Point(X + 150, Y + 150))) {
                                    enYakınNokta = Point.distance(p, new Point(X + 150, Y + 150));
                                    enYakınNoktaIndex = i;
                                }
                                farkListesiIlkClusterFarklar2.add(Point.distance(p, new Point(X + 150, Y + 150)));


                            }
                            double enYakınNoktaDisparity = farkListesiIlkCluster.get(enYakınNoktaIndex);
                            System.out.println(farkListesi);
                            System.out.println(farkListesiIlkCluster);
                            System.out.println(farkListesiIlkClusterFarklar);
                            System.out.println(farkListesiIlkClusterFarklar2);
                            System.out.println(enYakınNokta);
                            System.out.println(enYakınNoktaIndex);
                            System.out.println(enYakınNoktaDisparity);
                            distMap.put(sceneKMeans.getClusters().get(0).centroid, (0.34 * BASE_LINE) / enYakınNoktaDisparity * 10000);
                            output += "HESAPLAMASI SONRASI\n\n" +
                                    "Uzaklık = " + (0.34 * BASE_LINE) / ort + "    ---- " + (0.34 * BASE_LINE) / enYakınNoktaDisparity * 10000 + "\n" +
                                /*"10 cm = " + (0.34 * 10) / ort + "\n" +
                                "15 cm = " + (0.34 * 15) / ort + "\n" +
                                "20 cm = " + (0.34 * 20) / ort +*/
                                    "\n";

                            System.out.println(ort);
                            System.out.println("K means Hesaplandı");

                            // Kaç küme oluştu: sceneDBScanTest.getTrl().size();
                            // i. kümedeki eleman sayısı. sceneDBScanTest.getTrl().get(i).size();

                            sceneDBScanTest.applyDbscan();
                            objectDBScanTest.applyDbscan();
                            List<Double> ortList = new ArrayList<Double>();
                            List<exp1.sensor.oda114.sensorapp6.kmeans.Point> pointList = new ArrayList<>();
                            try {
                                for (i = 0; i < sceneDBScanTest.getTrl().size(); i++) {
                                    double ortalama = 0;
                                    double X = 0, Y = 0;
                                    for (int j = 0; j < sceneDBScanTest.getTrl().get(i).size(); j++) {
                                        int index = sceneDBScanTest.getHset().indexOf(sceneDBScanTest.getTrl().get(0).get(j));
                                        X += sceneDBScanTest.getHset().get(index).getX();
                                        Y += sceneDBScanTest.getHset().get(index).getY();
                                        ortalama += farkList.get(index);
                                    }
                                    ortalama /= sceneDBScanTest.getTrl().get(i).size();
                                    X /= sceneDBScanTest.getTrl().get(i).size();
                                    Y /= sceneDBScanTest.getTrl().get(i).size();
                                    exp1.sensor.oda114.sensorapp6.kmeans.Point p = new exp1.sensor.oda114.sensorapp6.kmeans.Point(X, Y);
                                    pointList.add(p);
                                    ortList.add(ortalama / 10000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            output += "\n----------------------------------------------\n";
                            output += "\n\nDBSCAN HESAPLAMASI İLE\n\n";
                            System.out.println(ortList);
                            for (i = 0; i < ortList.size(); i++) {
                                output +=
                                        "5  cm = " + (0.34 * BASE_LINE) / ortList.get(i) + "\n" +
                                                "10 cm = " + (0.34 * 10) / ortList.get(i) + "\n" +
                                                "15 cm = " + (0.34 * 15) / ortList.get(i) + "\n" +
                                                "20 cm = " + (0.34 * 20) / ortList.get(i) + "\n" +
                                                "---------------\n";
                                distMap.put(pointList.get(i), (0.34 * BASE_LINE) / ortList.get(i));
                            }

                            System.out.println("DBSCAN UYGULANDI");
                            txtDistance.setText(output);



                            /**
                             * Eğer ikiden fazla ise
                             *
                             * */

                            sceneKMeans3.init(X + 150, Y + 150);
                            sceneKMeans3.calculate();

                            if (KAC_TANE == 4) {
                                sceneKMeans4.init(X + 150, Y + 150);
                                sceneKMeans4.calculate();
                            }
                            ArrayList<Double> farkListesiIlkCluster3 = new ArrayList<>();
                            ArrayList<Double> farkListesiIlkCluster4 = new ArrayList<>();
                            if (KAC_TANE > 2){
                                enYakınNoktaIndex = 0;
                                 enYakınNokta = 3264; // başlangıç değeri
                                 enYakınNoktaIndex = 0; // başlangıç değeri
                                ArrayList<Double> farkListesiIlkClusterFarklar3 = new ArrayList<>();
                                ArrayList<Double> farkListesiIlkClusterFarklar23 = new ArrayList<>();
                                for (int j = 0; j < sceneKMeans3.getClusters().get(0).getPoints().size(); j++) {
                                    int index = sceneKMeans3.getPoints().indexOf(sceneKMeans3.getClusters().get(0).getPoints().get(j));
                                    Point p = (Point) sceneKMeans3.getClusters().get(0).getPoints().get(j);
                                    farkListesiIlkCluster3.add(farkList3.get(index));
                                    farkListesiIlkClusterFarklar3.add(Point.distance(p, sceneKMeans3.getClusters().get(0).getCentroid()));
                                    if (enYakınNokta > Point.distance(p, new Point(X + 150, Y + 150))) {
                                        enYakınNokta = Point.distance(p, new Point(X + 150, Y + 150));
                                        enYakınNoktaIndex = i;
                                    }
                                    farkListesiIlkClusterFarklar23.add(Point.distance(p, new Point(X + 150, Y + 150)));


                                }
                                double enYakınNoktaDisparity3 = farkListesiIlkCluster3.get(enYakınNoktaIndex);

                                System.out.println(enYakınNoktaDisparity);
                                System.out.println(farkListesiIlkCluster3);
                                System.out.println(farkListesiIlkClusterFarklar3);
                                System.out.println(farkListesiIlkClusterFarklar23);
                                System.out.println(enYakınNokta);
                                System.out.println(enYakınNoktaIndex);
                                System.out.println(enYakınNoktaDisparity3);

                            }
                            if(KAC_TANE == 4){

                                enYakınNoktaIndex = 0;
                                enYakınNokta = 3264; // başlangıç değeri
                                enYakınNoktaIndex = 0; // başlangıç değeri
                                ArrayList<Double> farkListesiIlkClusterFarklar4 = new ArrayList<>();
                                ArrayList<Double> farkListesiIlkClusterFarklar24 = new ArrayList<>();
                                for (int j = 0; j < sceneKMeans4.getClusters().get(0).getPoints().size(); j++) {
                                    int index = sceneKMeans4.getPoints().indexOf(sceneKMeans4.getClusters().get(0).getPoints().get(j));
                                    Point p = (Point) sceneKMeans3.getClusters().get(0).getPoints().get(j);
                                    farkListesiIlkCluster4.add(farkList4.get(index));
                                    farkListesiIlkClusterFarklar4.add(Point.distance(p, sceneKMeans4.getClusters().get(0).getCentroid()));
                                    if (enYakınNokta > Point.distance(p, new Point(X + 150, Y + 150))) {
                                        enYakınNokta = Point.distance(p, new Point(X + 150, Y + 150));
                                        enYakınNoktaIndex = i;
                                    }
                                    farkListesiIlkClusterFarklar24.add(Point.distance(p, new Point(X + 150, Y + 150)));


                                }
                                double enYakınNoktaDisparity4 = farkListesiIlkCluster3.get(enYakınNoktaIndex);

                                System.out.println(farkListesiIlkCluster3);
                                System.out.println(farkListesiIlkClusterFarklar4);
                                System.out.println(farkListesiIlkClusterFarklar24);
                                System.out.println(enYakınNokta);
                                System.out.println(enYakınNoktaIndex);
                                System.out.println(enYakınNoktaDisparity4);

                            }

                        }
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Hesaplama yaparken hata oluştu. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_detection_on_photo3);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                imgPath2 = extras.getString("IMG_PATH_1");
                imgPath1 = extras.getString("IMG_PATH_2");
                neTaraf = extras.getBoolean("NE_TARAF"); // true if right, false, if left
                X = extras.getInt("X");
                Y = extras.getInt("Y");
                KAC_TANE = extras.getInt("KAC_TANE");
            }
        } else {
            imgPath2 = (String) savedInstanceState.getSerializable("IMG_PATH_1");
            imgPath1  = (String) savedInstanceState.getSerializable("IMG_PATH_2");
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showDistance4 (View view){
        try {
            Intent i = new Intent(FeatureDetectionOnPhotoActivity3.this, ShowDistanceActivity.class);
            i.putExtra("IMG_PATH_1", imgPath1);
            i.putExtra("DİST_MAP", distMap);
            finish();
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}