package exp1.sensor.oda114.sensorapp6.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
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

import exp1.sensor.oda114.sensorapp6.R;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionActivityOnPhoto;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionOnPhotoActivity;

public class TakePhotoActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        btnCalculate = (Button) findViewById(R.id.btnCalculate);
        str_randomnumber = String.valueOf(nextSessionId());
    }

    public void resimCek (View view){

        str_SaveFolderName = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                + "/AutoExperiment";
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

            if (counter4Images  == 2 ){

                btnCalculate.setVisibility(View.VISIBLE);
            }else   btnCalculate.setVisibility(View.GONE);
        }
    }

    public void hesapla (View view){
        Intent i = new Intent(TakePhotoActivity.this, FeatureDetectionOnPhotoActivity.class);

        i.putExtra("IMG_PATH_1", image1);
        i.putExtra("IMG_PATH_2", image2);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_photo, menu);
        return true;
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
}
