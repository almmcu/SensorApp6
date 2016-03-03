package exp1.sensor.oda114.sensorapp6.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.SecureRandom;

import exp1.sensor.oda114.sensorapp6.R;

public class MyCameraActivity extends AppCompatActivity implements SensorEventListener {

    private static final int CAMERA_REQUEST = 1888;

    private SensorManager sMgr;
    private Sensor mLineerAccSensor;
    static String str_Camera_Photo_ImagePath = "";
    private static File f;
    private static int Take_Photo = 2;
    private static String str_randomnumber = "";
    static String str_Camera_Photo_ImageName = "";
    public static String str_SaveFolderName;
    private static File wallpaperDirectory;
    Bitmap bitmap;
    int storeposition = 0;
    public static GridView gridview;
    public static ImageView imageView;
    private CameraBridgeViewBase mOpenCvCameraView;
    public static final String TAG = "My    Camera Activity ";
    int counter4Images = 0;
    TextView txtAcc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);

        sMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLineerAccSensor = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        txtAcc = (TextView) findViewById(R.id.txtAcc);
        Button photoButton = (Button) this.findViewById(R.id.button1);

    }



    public void resimCek (View view){

        str_SaveFolderName = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/openCvPhotos";
        str_randomnumber = String.valueOf(nextSessionId());
        wallpaperDirectory = new File(str_SaveFolderName);
        if (!wallpaperDirectory.exists())
            wallpaperDirectory.mkdirs();
        str_Camera_Photo_ImageName = str_randomnumber
                + ".jpg";
        str_Camera_Photo_ImagePath = str_SaveFolderName
                + "/" + str_randomnumber + ".jpg";
        System.err.println(" str_Camera_Photo_ImagePath  "
                + str_Camera_Photo_ImagePath);


        // eğer sayactan ikiye bölünen 0 ise ilk resim çekilmiş demektir.
        // sensor aktif edilebililir.

        if (counter4Images %4 == 0 ){
            try {
                sMgr.unregisterListener((SensorEventListener) this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        counter4Images ++;

        f = new File(str_Camera_Photo_ImagePath);
        startActivityForResult(new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)),
                Take_Photo);
        System.err.println("f  " + f);

    }
    // used to create randon numbers
    public String nextSessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Take_Photo) {
            String filePath = null;

            filePath = str_Camera_Photo_ImagePath;
            if (filePath != null) {
                Bitmap faceView = ( new_decode(new File(
                        filePath))); // ========================> good
                // lines



                imageView.setImageBitmap(faceView);

            } else {
                bitmap = null;
            }

            // eğer sayactan ikiye bölünen 0 ise ilk resim çekilmiş demektir.
            // sensor aktif edilebililir.

            if (counter4Images %4 == 1 ){
                sMgr.registerListener(this, mLineerAccSensor, SensorManager.SENSOR_DELAY_NORMAL);

            }
            counter4Images ++;
        }
    }

    public static Bitmap new_decode(File f) {

        // decode image size

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        o.inDither = false; // Disable Dithering mode

        o.inPurgeable = true; // Tell to gc that whether it needs free memory,
        // the Bitmap can be cleared

        o.inInputShareable = true; // Which kind of reference will be used to
        // recover the Bitmap data after being
        // clear, when it will be used in the future
        try {
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Find the correct scale value. It should be the power of 2.
        final int REQUIRED_SIZE = 300;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 1.5 < REQUIRED_SIZE && height_tmp / 1.5 < REQUIRED_SIZE)
                break;
            width_tmp /= 1.5;
            height_tmp /= 1.5;
            scale *= 1.5;
        }

        // decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        // o2.inSampleSize=scale;
        o.inDither = false; // Disable Dithering mode

        o.inPurgeable = true; // Tell to gc that whether it needs free memory,
        // the Bitmap can be cleared

        o.inInputShareable = true; // Which kind of reference will be used to
        // recover the Bitmap data after being
        // clear, when it will be used in the future
        // return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        try {

//          return BitmapFactory.decodeStream(new FileInputStream(f), null,
//                  null);
            Bitmap bitmap= BitmapFactory.decodeStream(new FileInputStream(f), null, null);
            System.out.println(" IW " + width_tmp);
            System.out.println("IHH " + height_tmp);
            int iW = width_tmp;
            int iH = height_tmp;

            return Bitmap.createScaledBitmap(bitmap, iW, iH, true);

        } catch (OutOfMemoryError e) {
            // TODO: handle exception
            e.printStackTrace();
            // clearCache();

            // System.out.println("bitmap creating success");
            System.gc();
            return null;
            // System.runFinalization();
            // Runtime.getRuntime().gc();
            // System.gc();
            // decodeFile(f);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
       // sMgr.registerListener((SensorEventListener) this, mLineerAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        try {
            sMgr.unregisterListener((SensorEventListener) this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        try {
            sMgr.unregisterListener((SensorEventListener) this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;


            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                float lineerXAccerometer = event.values[0];
                float lineerYAccerometer = event.values[1];
                float lineerZAccerometer = event.values[2];
                txtAcc.setText("" + lineerXAccerometer);
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    try {



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
}
