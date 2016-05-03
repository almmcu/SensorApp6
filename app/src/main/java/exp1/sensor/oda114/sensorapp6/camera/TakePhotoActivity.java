package exp1.sensor.oda114.sensorapp6.camera;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import exp1.sensor.oda114.sensorapp6.R;
import exp1.sensor.oda114.sensorapp6.image.ImageActivity;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionActivityOnPhoto;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionOnPhotoActivity;
import exp1.sensor.oda114.sensorapp6.show.ShowDistanceActivity;

public class TakePhotoActivity extends AppCompatActivity implements  SensorEventListener{

    private static final int CAMERA_REQUEST = 1888;

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
    public static final String TAG = "TAKE_PHOTO_ACTİVİTY";
    int counter4Images = 0;
    private String image1 = "", image2 = "";
    Button btnCalculate ;
    // Sensor kullanarak cihazın hareketini tahmin ediyor
    private SensorManager sMgr;
    private Sensor mLineerAccSensor;
    long currentTimeinMilisecoond;// gecen zaman arasındaki farkı hesaplamak içn

    /**
     *  <h1> Değişkenler </h1>
     *
     *  passedTime Herbir ölçümde geçen zamanı kaydediyor
     *  saniyelikMesurement Herbşr ölçümde ne gelen değerlieri kaydediyor.
     *  saniyelikMesafe Herbşr ölçümde ne kadar hareketetmiş
     *  <br >
     * */

    ArrayList<Double> passedTime = new ArrayList<>();
    ArrayList<Double> anlikMesurement = new ArrayList<>();
    ArrayList<Double> anlikMesafeler = new ArrayList<>();
    /**
     * neTaraf eğer sağa hareket etmişse true, eğer sola hareket etmişse false
     * */

    /**
     * Hareket yönünü belirlemek için kullanılan değişkenler
     * netaraf değişkeni true ise sağa false ise sola doğru bir hareketlenme söz konusu oluyor.
     *
     * */
    TextView txtAcc;
    StringBuilder builder = new StringBuilder();
    float [] history = new float[2];
    public static final double X_THERESHOLD = 0.3;
    public static final double Y_THERESHOLD = 2.0;
    boolean neTaraf = true;
    String [] direction = {"NONE","NONE"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        btnCalculate = (Button) findViewById(R.id.btnCalculate);
        str_randomnumber = String.valueOf(nextSessionId());

        sMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLineerAccSensor = sMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void resimCek (View view){

        str_SaveFolderName = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/AutoExperiment2";
        str_Camera_Photo_ImageName = str_randomnumber;
        wallpaperDirectory = new File(str_SaveFolderName);
        if (!wallpaperDirectory.exists())
            wallpaperDirectory.mkdirs();


        if (counter4Images  == 0 ){
            try {
                str_Camera_Photo_ImageName += "LEFT.jpg";
                str_Camera_Photo_ImagePath = str_SaveFolderName
                        + "/" + str_Camera_Photo_ImageName;
                image1 = str_Camera_Photo_ImageName;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (counter4Images  == 1 ){
            str_Camera_Photo_ImageName += "RİGHT.jpg";
            str_Camera_Photo_ImagePath = str_SaveFolderName
                    + "/" + str_Camera_Photo_ImageName;
            image2 = str_Camera_Photo_ImageName;

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

                imageView.setImageBitmap(faceView);

            } else {
                bitmap = null;
            }
            if (    counter4Images == 1 ){
                sMgr.registerListener(this, mLineerAccSensor, SensorManager.SENSOR_DELAY_NORMAL);

            }
            if (counter4Images  == 2 ){
                sMgr.unregisterListener((SensorEventListener) this);
                btnCalculate.setVisibility(View.VISIBLE);

                System.out.println(passedTime);
                System.out.println(anlikMesurement);
                double toplamMesafe = 0;
                for (int i = 1 ; i < passedTime.size(); i ++){
                    double anlikMeasure =  anlikMesurement.get(i);
                    if ( anlikMeasure < 0) anlikMeasure *= -1;
                    double anlikMesafe = 0.5 * passedTime.get(i) * passedTime.get(i) * anlikMeasure;
                    anlikMesafe /= 10000;
                    anlikMesafeler.add(anlikMesafe);
                    toplamMesafe += anlikMesafe;
                }
                System.out.println(anlikMesafeler);
                System.out.println(toplamMesafe);
                TextView txtMeasure = (TextView) findViewById(R.id.txtMeasure);
                txtMeasure.setText("Geçen mesafe: " + toplamMesafe + "\n\n" + direction[0]);

                Intent i = new Intent(TakePhotoActivity.this, ImageActivity.class);
                i.putExtra("IMG_PATH_1", image1);
                i.putExtra("IMG_PATH_2", image2);
                i.putExtra("NE_TARAF", neTaraf);

                startActivity(i);

            }else   btnCalculate.setVisibility(View.GONE);
        }
    }

    public void hesapla (View view){
        Intent i = new Intent(TakePhotoActivity.this, FeatureDetectionOnPhotoActivity.class);

        i.putExtra("IMG_PATH_1", image1);
        i.putExtra("IMG_PATH_2", image2);
        i.putExtra("NE_TARAF", neTaraf);
        startActivity(i);
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
    public void onPause()
    {
        super.onPause();


        try {
            sMgr.unregisterListener((SensorEventListener) this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();

        try {
            sMgr.unregisterListener((SensorEventListener) this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        long temp = System.currentTimeMillis();

        long timediff = temp - currentTimeinMilisecoond;
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            float xChange = history[0] - event.values[0];
            float yChange = history[1] - event.values[1];

            history[0] = event.values[0];
            history[1] = event.values[1];

            if (xChange > X_THERESHOLD){
                direction[0] = "SOL";
                neTaraf = false;
            }
            else if (xChange < -X_THERESHOLD){
                direction[0] = "SAG";
                neTaraf = true;
            }

            if (yChange > Y_THERESHOLD){
                direction[1] = "YUKARI";
            }
            else if (yChange < -Y_THERESHOLD){
                direction[1] = "ASAGI";
            }

            /*float angularXSpeed = event.values[0];
            float angularYSpeed = event.values[1];
            float angularZSpeed = event.values[2];//*/

            anlikMesurement.add( (double) event.values[0]);
            passedTime.add((double) timediff );
            currentTimeinMilisecoond = System.currentTimeMillis();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
