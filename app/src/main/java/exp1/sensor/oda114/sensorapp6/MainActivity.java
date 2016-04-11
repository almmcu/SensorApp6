package exp1.sensor.oda114.sensorapp6;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.OutputStreamWriter;
import java.util.ArrayList;

import exp1.sensor.oda114.sensorapp6.accdeneme.AccelerometerExample;
import exp1.sensor.oda114.sensorapp6.camera.TakePhotoActivity;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionActivity;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionActivityOnPhoto;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionOnPhotoActivity;
import exp1.sensor.oda114.sensorapp6.photo.MyCameraActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener,CameraBridgeViewBase.CvCameraViewListener {
///sdasefsadfasdfadfadf/**/
    private Sensor mGyroSensor;
    private Sensor mLineerAccSensor;
    private TextView tv;
    private TextView tv2;
    private TextView txtSaniyeilikMesafe;
    private TextView txtSaniyelikIvme;
    private SensorManager sMgr;
    float angularXMaxSpeedOneSec = 0;
    float angularXMaxSpeed = 0;
    float angularYMaxSpeed = 0;
    float angularZMaxSpeed = 0;
    OutputStreamWriter outputStreamWriter;
    long currentTimeinMilisecoond;
    long dif = 0;
    int a = 0;
    int mapIndex = 0;
public static final String TAG = "Bu Uygulama";
    double mesafe = 0;
    Button btn ;
    int cal = 0 ;
    ArrayList<Double> saniyelik = new ArrayList<>();
    ArrayList<Double> saniyelikMesurement = new ArrayList<>();
    ArrayList<Double> saniyelikMesafe = new ArrayList<>();
    ArrayList<ArrayList<Double>> accValueMap = new ArrayList<>();
   // static{System.loadLibrary("opencv_java3"); }
    //static{ System.loadLibrary("opencv_java"); }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {

           /* Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
            System.out.println(m.toString());*/
            sMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mLineerAccSensor = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

            tv= (TextView)findViewById(R.id.txt2);
            tv2= (TextView)findViewById(R.id.txt3);
            txtSaniyelikIvme= (TextView)findViewById(R.id.txtSaniyelikIvme);
            txtSaniyeilikMesafe= (TextView)findViewById(R.id.txtSaniyelikMesafe);
            btn = (Button) findViewById(R.id.btnBaslaBitir);}
        catch (Exception e){
            System.out.println(e);
        }




    }

 /*   @Override
    public void onSensorChanged(SensorEvent event) {

    }*/



    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG,"OpenCV Manager Connected");
                    //from now onwards, you can use OpenCV API
                    Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
                    break;
                case LoaderCallbackInterface.INIT_FAILED:
                    Log.i(TAG,"Init Failed");
                    break;
                case LoaderCallbackInterface.INSTALL_CANCELED:
                    Log.i(TAG,"Install Cancelled");
                    break;
                case LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION:
                    Log.i(TAG,"Incompatible Version");
                    break;
                case LoaderCallbackInterface.MARKET_ERROR:
                    Log.i(TAG,"Market Error");
                    break;
                default:
                    Log.i(TAG, "OpenCV Manager Install");
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void onSensorChanged(SensorEvent event) {
        long temp = System.currentTimeMillis();

        long timediff = temp - currentTimeinMilisecoond;
        Sensor sensor = event.sensor;
        if (timediff >= 5){

            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                float angularXSpeed = event.values[0];
                float angularYSpeed = event.values[1];
                float angularZSpeed = event.values[2];//

          /*  if (angularXMaxSpeed < angularXSpeed) angularXMaxSpeed = angularXSpeed;
            if (angularYMaxSpeed < angularYSpeed) angularYMaxSpeed = angularYSpeed;
            if (angularZMaxSpeed < angularZSpeed) angularZMaxSpeed = angularZSpeed;*/


            /*if (angularXSpeed < 0.150) angularXSpeed = 0 ;
            if (angularYSpeed < 0.270) angularYSpeed = 0 ;
            if (angularZSpeed < 0.2) angularZSpeed = 0 ;*/
          /*  double b = (0.5 * angularXSpeed * 0.1 * 0.1/4);
            if (b< 0) b = -b;
            a += ( b / 1000) ;*/

                tv.setText("Angular X speed level is: " + "" + angularXSpeed + "\n\n"
                                + "Angular Y speed level is: " + "" + angularYSpeed + "\n\n"
                                + "Angular Z speed level is: " + "" + angularZSpeed
                                + "\n\n"
/*
                            "Angular X speed level is: " + "" + angularXMaxSpeed + "\n\n"
                            // 0.033320963 -
                            + "Angular Y speed level is: " + "" + angularYMaxSpeed + "\n\n"
                            // 0.12496567 -
                            + "Angular Z speed level is: " + "" + angularZMaxSpeed

                            + "\n\n\n\n\n\n" +
                    ( a )
                    // 0.060460567 -*/


                );



                saniyelik.add( (double) angularXSpeed);
                a++;
                dif += timediff;
                angularXMaxSpeedOneSec += angularXSpeed ;
                if (dif >= 1000)
                {
                    angularXMaxSpeedOneSec /= a;
                    tv2.setText("\n\n\n\n" + angularXMaxSpeedOneSec  + "\n" +
                            "\n" +
                            " mesafe\n" +
                            (0.5*angularXMaxSpeedOneSec *angularXMaxSpeedOneSec)*100 );
                    dif = 0 ;
                    double tempp = (0.5*angularXMaxSpeedOneSec *angularXMaxSpeedOneSec);
                    if (tempp < 0) tempp = - tempp;
                    try {
                        mesafe = mesafe + tempp *100 ;
                        saniyelikMesurement.add(tempp*100);// saniyelik alınan mesafeler saklanılıyor.
                        saniyelikMesurement.add((double) angularXMaxSpeedOneSec);
                        accValueMap.add( saniyelik);
                        saniyelikMesafe.add(tempp * 100);
                        angularXMaxSpeedOneSec = 0 ;
                        saniyelik = new ArrayList<>();
                        a = 0 ;
                        mapIndex ++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }

                    System.out.println(accValueMap);
                }
                currentTimeinMilisecoond = System.currentTimeMillis();
            }
        }
       /* else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float angularXGyro = event.values[0];
            float angularYGyro = event.values[1];
            float angularZGyro = event.values[2];//
        }
*/


    }
    public void openCamera (View view){
        Intent inIntent = new Intent(getApplicationContext(), FeatureDetectionOnPhotoActivity.class);
//        inIntent = new Intent(getApplicationContext(), TakePhotoActivity.class);
        startActivity(inIntent);

    }
    public void xDirectionMovement (View view){
        Intent inIntent = new Intent(getApplicationContext(), AccelerometerExample.class);
//        inIntent = new Intent(getApplicationContext(), TakePhotoActivity.class);
        startActivity(inIntent);

    }

    public void baslaBitir (View view){

        if (cal % 2 == 0 )
        {
            btn.setText("Bitir");
            sMgr.registerListener(this, mLineerAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            btn.setText("Basla");
            sMgr.unregisterListener((SensorEventListener) this);
            tv2.setText("\n\nSon mesafe   " +mesafe
                    +"\n Integer Value:  " + (int)mesafe);
            int a1 = (int)mesafe;

            int i = 1;

            System.out.println(accValueMap);
            String mesafeler = "";
            for (i = 0; i < saniyelikMesurement.size() ; i++) {
                mesafeler += ((i + 1) + ". saniye  "+saniyelikMesurement.get(i++) + "  " + saniyelikMesurement.get(i) + "\n");
            }
            txtSaniyeilikMesafe.setText(mesafeler);
            saniyelikMesurement.clear();
            i = 1;
            mesafeler = "\n";
            for (ArrayList<Double> accList:accValueMap) {
                mesafeler += i +". saniye:\n ";
                for (double accValalue:accList
                        ) {

                    mesafeler += "   " +accValalue;
                }
                mesafeler += "\n";
                i++;
            }
            txtSaniyelikIvme.setMovementMethod(new ScrollingMovementMethod());
            txtSaniyelikIvme.setText(mesafeler);
            accValueMap.clear();
            saniyelikMesafe.clear();
            mesafe = 0;
            mapIndex = 0;

        }


        cal++;
    }


    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

       /* sMgr.registerListener((SensorEventListener) this, mLineerAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sMgr.registerListener((SensorEventListener) this, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);*/
    }

    @Override
    protected void onPause() {
        // important to unregister the sensor when the activity pauses.
        super.onPause();

        try {
            sMgr.unregisterListener((SensorEventListener) this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        return null;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }
}
