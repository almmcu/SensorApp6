package exp1.sensor.oda114.sensorapp6.photo;

import android.os.Environment;
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
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

import exp1.sensor.oda114.sensorapp6.R;

public class FeatureDetectionActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {


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
        setContentView(R.layout.activity_feature_detection);


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

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feature_detection, menu);
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

// Opencv Kontrol ve Kod yazma
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    System.loadLibrary("opencv_java");
                    System.loadLibrary("nonfree");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
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
        Mat mat = Imgcodecs.imread(imgPath1);

        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2GRAY);
        featureDetector.detect(rgba, keyPoints);
        Features2d.drawKeypoints(rgba, keyPoints, rgba);
        Log.e("LOG!1!!!!!!!!!!!!!!!!!!", "number of query Keypoints= " + keyPoints.size());


        return rgba;
    }

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
