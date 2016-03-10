package exp1.sensor.oda114.sensorapp6.photo;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.TimePicker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.sql.SQLOutput;
import java.util.List;

import exp1.sensor.oda114.sensorapp6.R;

public class FeatureDetectionActivityOnPhoto extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {


private CameraBridgeViewBase mOpenCvCameraView;
public static final String TAG = "Camera Activity ";
private String imgPath1 = "", imgPath2 = "";
        Mat image ; File fileimage ;
        TimePicker time ;
private Mat                    mRgba;
private Mat                    mGrayMat;

        Mat descriptors ;
        List<Mat> descriptorsList;

        FeatureDetector featureDetector;
        MatOfKeyPoint keyPoints;
        DescriptorExtractor descriptorExtractor;
        DescriptorMatcher descriptorMatcher;

        boolean mIsJavaCamera = true;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_detection_activity_on_photo);

    if (savedInstanceState == null) {
        Bundle extras = getIntent().getExtras();
        if(extras == null) {

        } else {
            imgPath1= extras.getString("IMG_PATH_1");
            imgPath2= extras.getString("IMG_PATH_2");
        }
    } else {
        imgPath1= (String) savedInstanceState.getSerializable("IMG_PATH_1");
        imgPath2= (String) savedInstanceState.getSerializable("IMG_PATH_2");
    }

    /*mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
    mOpenCvCameraView.setCvCameraViewListener(this);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feature_detection_activity_on_photo, menu);
        return true;
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
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGrayMat = new Mat();
        featureDetector=FeatureDetector.create(FeatureDetector.SIFT);
        descriptorExtractor=DescriptorExtractor.create(DescriptorExtractor.SURF);
        descriptorMatcher=DescriptorMatcher.create(6);
        keyPoints = new MatOfKeyPoint();
        descriptors = new Mat();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final Mat rgba = inputFrame.rgba();

        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2GRAY);
        featureDetector.detect(rgba, keyPoints);
        Features2d.drawKeypoints(rgba, keyPoints, rgba);
        return rgba;
    }

    // Opencv Kontrol ve Kod yazma
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    System.loadLibrary("opencv_java");
                    System.loadLibrary("nonfree");
                    /*mOpenCvCameraView.enableView();*/
                    try {
                        mRgba = new Mat();
                        mGrayMat = new Mat();
                        featureDetector=FeatureDetector.create(FeatureDetector.SIFT);
                        descriptorExtractor=DescriptorExtractor.create(DescriptorExtractor.SURF);
                        descriptorMatcher=DescriptorMatcher.create(6);
                        keyPoints = new MatOfKeyPoint();
                        descriptors = new Mat();
                        final Mat rgba =  Imgcodecs.imread(imgPath1);
                       // final Mat rgba = inputFrame.rgba();

                        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2GRAY);
                        featureDetector.detect(rgba, keyPoints);
                        for (int i = 0; i < 1000; i++) {
                            Log.e("LOG!1!!!!!!!!!!!!!!!!!!", "number of query Keypoints= " + keyPoints.size());
                        }

                        //Features2d.drawKeypoints(rgba, keyPoints, rgba);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
                    DescriptorExtractor SurfExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);

                    Mat img1 = Imgcodecs.imread(imgPath1, 4); // ilk resim
                    Mat img2 = Imgcodecs.imread(imgPath2, 4); // ikinci resim

                    //extract keypoints
                    MatOfKeyPoint keypoints = new MatOfKeyPoint();
                    MatOfKeyPoint logoKeypoints = new MatOfKeyPoint();

                    detector.detect(img1, keypoints);//this is the problem "fatal signal"
                    Log.e("LOG!1!!!!!!!!!!!!!!!!!!", "number of query Keypoints= " + keypoints.size());
                    System.out.println(keypoints.size());
                    detector.detect(img2, logoKeypoints);
                    Log.e("LOG!!!!!!!!!!!!!!!!!!!", "number of logo Keypoints= " + logoKeypoints.size());
                    System.out.println(logoKeypoints.size());

                    try {
                        Features2d.drawKeypoints(img1, keypoints, img1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                 //   int whichDescriptor = siftDescriptor; //freakDescriptor;

                    // Features SEARCH
                    int detectorType = FeatureDetector.SURF;
                     detector = FeatureDetector.create(detectorType);

                    Mat mask = new Mat();
                     keypoints = new MatOfKeyPoint();
                    detector.detect(img1, keypoints , mask);

                    if (!detector.empty()){

                        // Draw kewpoints
                        Mat outputImage = new Mat();
                        Scalar color = new Scalar(0, 0, 255); // BGR
                        int flags = Features2d.DRAW_RICH_KEYPOINTS; // For each keypoint, the circle around keypoint with keypoint size and orientation will be drawn.
                        Log.e("LOG!!!!!!!!!!!!!!!!!!!!", "number of query Keypoints= " + keypoints.size());

                        try {
                            Features2d.drawKeypoints(img1, keypoints, outputImage, color, flags);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //displayImage(Mat2BufferedImage(outputImage), "Feautures_"+detectorType);
                    }*/

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        /*if (myOpenCvCameraView != null)
            myOpenCvCameraView.disableView();*/
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        /*if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();*/
    }
}
