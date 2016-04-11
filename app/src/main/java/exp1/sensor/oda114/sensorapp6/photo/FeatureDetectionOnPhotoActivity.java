package exp1.sensor.oda114.sensorapp6.photo;


import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import exp1.sensor.oda114.sensorapp6.R;
import exp1.sensor.oda114.sensorapp6.dbscan.DBScanTest;
import exp1.sensor.oda114.sensorapp6.kmeans.KMeans;

public class FeatureDetectionOnPhotoActivity extends AppCompatActivity {

    public static final String TAG = "Photo Activity ";
    MatOfKeyPoint keyPoints;
    MatOfKeyPoint logokeyPoints;

    List<DMatch> matchesList;
    DescriptorMatcher matcher;
    MatOfDMatch matches;
    MatOfDMatch gm;
    LinkedList<DMatch> good_matches;
    private String imgPath1 = "", imgPath2 = "";
    ArrayList<Double> farkList ;
    boolean neTaraf = true;

    // Opencv Kontrol ve Kod yazma
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    System.loadLibrary("opencv_java");
                    System.loadLibrary("nonfree");

                    /**
                     * imgPath1 = "/tekNesne/60cm/1_5.jpg";
                     * imgPath2 = "/tekNesne/60cm/1_0.jpg";
                     * 2r0mv7tehchfbd0bm0jpqv6jqaLEFT
                    */

                    /**
                     *imgPath1 = "64php3hj29lrg6cbeov3igosrtRİGHT.jpg";
                     imgPath2 = "64php3hj29lrg6cbeov3igosrtLEFT.jpg";
                     * *
                    * */
                    /*imgPath1 = "Duzgun15cmRİGHT.jpg";
                    imgPath2 = "Duzgun15cmLEFT.jpg";
                    imgPath1 = "Hatali5cmRİGHT.jpg";
                    imgPath2 = "Hatali5cmLEFT.jpg";*/

                   // File file1 = new File(Environment.getExternalStorageDirectory(), "openCvPhotos/" + imgPath1);
                    //File file2 = new File(Environment.getExternalStorageDirectory(), "openCvPhotos/" + imgPath2);

                    File file1 = new File(Environment.getExternalStorageDirectory(), "AutoExperiment/" + imgPath1);
                    File file2 = new File(Environment.getExternalStorageDirectory(), "AutoExperiment/" + imgPath2);
                    Mat image1, image2;
                    if (file1.exists() && file2.exists()) {
                        // Reesimleri Grayscale olarak okuma
                        image1 = Imgcodecs.imread(file1.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                        image2 = Imgcodecs.imread(file2.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

                        /**
                         * Keypoints ve bunlardan elde edilecek descriptorların hesaplanması
                         * Bu hesaplamalar için SURF algoritması kullanılıyor.
                         * Bu işlem her iki resim içinde aynı şekilde yapılıyor.
                         * Bundan somra iki elde edilen desriptorlar yardımıyla iki resiminkarşılaştırılması yapışlıyor.
                         **/
                        FeatureDetector SURF = FeatureDetector.create(FeatureDetector.SURF);

                        keyPoints = new MatOfKeyPoint();
                        logokeyPoints = new MatOfKeyPoint();
                        // İki reim içinde keypoints hesabı
                        SURF.detect(image1, keyPoints);
                        SURF.detect(image2, logokeyPoints);

                        Log.e(TAG, "#keypoints " + keyPoints.size());
                        Log.e(TAG, "#logokeypoints " + logokeyPoints.size());
                        Size ketP = keyPoints.size();
                        System.out.println(ketP);
                        ketP = logokeyPoints.size();
                        System.out.println(ketP);
                        DescriptorExtractor SurfExtractor = DescriptorExtractor
                                .create(DescriptorExtractor.SURF);
                        Mat descriptors = new Mat();
                        Mat logoDescriptors = new Mat();
                        // İki resim içinde desriptor hesabı
                        SurfExtractor.compute(image1, keyPoints, descriptors);
                        SurfExtractor.compute(image2, logokeyPoints, logoDescriptors);
                        /**
                         * İki resimin karşılaştırma işlemi burada yapılıyor
                         *
                         * */
                        gm = new MatOfDMatch();
                        matches = new MatOfDMatch();
                        good_matches = new LinkedList<>();

                        double max_dist = 0;
                        double min_dist = 1000;

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
                        // En yakın mesafenin 1.5 katı büyüklüğünde olan bütün mesafeler alınıyor.
                        for (int i = 0; i < descriptors.rows(); i++) {
                            if (matchesList.get(i).distance < 2 * min_dist) {
                                good_matches.addLast(matchesList.get(i));
                            }
                        }
                        /**
                         * Eşleşen noktalar bulundu.
                         * Eşleşen noktaların koordinatları bulunacak.
                         *
                         * */

                        /**
                         * !!!!!!!!!!!!!!!!!!!!!!!!!!
                         * Buradan sonra eşleşen noktların doğru bulunup bulunmadığı,
                         * Doğru ise yorumunun nasıl yapılacağı tartışılacak.
                         * Bunun nasıl yapılacağını öğrenmen gerekiyor.
                         * !!!!!!!!!!!!!!!!!!!!!!!!!!
                         * */
                        gm.fromList(good_matches);

                        List<KeyPoint> keypoints_objectList = keyPoints.toList();
                        List<KeyPoint> keypoints_sceneList = logokeyPoints.toList();
                        //MatOfPoint2f obj = new MatOfPoint2f();
                        //MatOfPoint2f scene = new MatOfPoint2f();
                        LinkedList<Point> objList = new LinkedList<>();
                        LinkedList<Point> sceneList = new LinkedList<>();
                        KMeans objKMeans = new KMeans();
                        KMeans sceneKMeans = new KMeans();
                        DBScanTest sceneDBScanTest = new DBScanTest();
                        DBScanTest objectDBScanTest = new DBScanTest();


                        double ort = 0;
                        int count   = 0;
                        farkList = new ArrayList<>();

                        for (int i = 0; i < good_matches.size(); i++) {
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
                            double fark = x1 - x2 ;
                            if (!neTaraf) fark = x2 - x2 ; // Eğer neTaraf değişkeni false ise sola hareket var, true ise sağa hareket var demektir.



                            if ((fark) >= 0) {
                                ort += (fark) ;
                                farkList.add(fark);
                                count ++;

                                // objList değerleri ekleniyor
                                point = new exp1.sensor.oda114.sensorapp6.kmeans.Point(keypoints_objectList.get(good_matches.get(i).queryIdx).pt.x, keypoints_objectList.get(good_matches.get(i).queryIdx).pt.y);
                                objKMeans.getPoints().add(point);
                                objectDBScanTest.getHset().add(point);

                                // sceneList değerleri ekleniyor
                                point = new exp1.sensor.oda114.sensorapp6.kmeans.Point(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt.x, keypoints_sceneList.get(good_matches.get(i).trainIdx).pt.y);
                                sceneKMeans.getPoints().add(point);
                                sceneDBScanTest.getHset().add(point);
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

                        sceneKMeans.init();
                        sceneKMeans.calculate();


                        int i = sceneKMeans.clusterQuality(sceneKMeans);
                        ort = 0;
                        for (int j = 0; j < sceneKMeans.getClusters().get(i).getPoints().size(); j ++){
                            int index = sceneKMeans.getPoints().indexOf( sceneKMeans.getClusters().get(i).getPoints().get(j));
                            ort += farkList.get(index);
                            System.out.println(index);
                        }
                        ort = ort / sceneKMeans.getClusters().get(i).getPoints().size();
                        ort /=10000;
                        TextView txtDistance = (TextView) findViewById(R.id.txtDistance);
                        String output = "";
                        output += "K-MEANS HESAPLAMASI İLE\n\n" +
                                        "5  cm = " + (0.34 * 5) / ort + "\n" +
                                        "10 cm = " + (0.34 * 10) / ort + "\n" +
                                        "15 cm = " + (0.34 * 15) / ort + "\n" +
                                        "20 cm = " + (0.34 * 20) / ort + "\n";

                        System.out.println(ort);
                        System.out.println("K means Hesaplandı");
// Kaç küme oluştu: sceneDBScanTest.getTrl().size();
                        // i. kümedeki eleman sayısı. sceneDBScanTest.getTrl().get(i).size();

                        sceneDBScanTest.applyDbscan();
                        objectDBScanTest.applyDbscan();
                        List<Double> ortList = new ArrayList<Double>();
                        try {
                            for (  i = 0; i < sceneDBScanTest.getTrl().size() ; i++ ){
                                double ortalama = 0;
                                for (int j = 0 ; j < sceneDBScanTest.getTrl().get(i).size(); j++){

                                    int index = sceneDBScanTest.getHset().indexOf(sceneDBScanTest.getTrl().get(0).get(j));
                                    ortalama += farkList.get(index);
                                }
                                ortalama /= sceneDBScanTest.getTrl().get(i).size();
                                ortList.add(ortalama /10000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        output +="\n----------------------------------------------\n";
                        output += "\n\nDBSCAN HESAPLAMASI İLE\n\n";
                        System.out.println(ortList);
                        for (i = 0 ; i < ortList.size() ; i ++){
                            output +=
                                    "5  cm = " + (0.34 * 5) / ortList.get(i) + "\n" +
                                    "10 cm = " + (0.34 * 10) / ortList.get(i) + "\n" +
                                    "15 cm = " + (0.34 * 15) / ortList.get(i) + "\n" +
                                    "20 cm = " + (0.34 * 20) / ortList.get(i) + "\n"+
                                    "---------------\n";
                        }

                        System.out.println("DBSCAN UYGULANDI");
                        txtDistance.setText(output);
                        // Bu kısımları yorum satırına almamızın nedeni bu kısımları henüz kullanma ihtiyacı hisstmediimizden kaynaklanmakta.
                       /* try {
                       obj.fromList(objList);
                        Mat Mat1 = new Mat();
                        scene.fromList(sceneList);
                            Features2d.drawKeypoints(image1, keyPoints, image1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Mat H = Calib3d.findHomography(obj, scene);

                        Mat warpimg = Mat1.clone();
                        org.opencv.core.Size ims = new org.opencv.core.Size(Mat1.cols(), Mat1.rows());
                        // hata veriyor mat1 boş olduğundan diye tahmin ediyorum
                        Imgproc.warpPerspective(Mat1, warpimg, H, ims);
                        */

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
        setContentView(R.layout.activity_feature_detection_on_photo);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                imgPath2 = extras.getString("IMG_PATH_1");
                imgPath1 = extras.getString("IMG_PATH_2");
                neTaraf = extras.getBoolean("NE_TARAF");
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