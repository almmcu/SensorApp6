package exp1.sensor.oda114.sensorapp6.image;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import exp1.sensor.oda114.sensorapp6.R;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionOnPhotoActivity;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionOnPhotoActivity2;
import exp1.sensor.oda114.sensorapp6.photo.FeatureDetectionOnPhotoActivity3;

public class ImageActivity extends AppCompatActivity {

    private String imgPath1 = "", imgPath2 = "";
    private int X, Y;
    boolean neTaraf = true;
    private int KAC_TANE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                imgPath2 = extras.getString("IMG_PATH_1");
                imgPath1 = extras.getString("IMG_PATH_2");
                neTaraf = extras.getBoolean("NE_TARAF");
                KAC_TANE = extras.getInt("KAC_TANE");

            }
        } else {
            imgPath2 = (String) savedInstanceState.getSerializable("IMG_PATH_1");
            imgPath1  = (String) savedInstanceState.getSerializable("IMG_PATH_2");
        }
        String path = Environment.getExternalStorageDirectory() + "/AutoExperiment2/" + imgPath2;
        //  ImageView image = (ImageView) findViewById(R.id.imageView34);
        LinearLayout li = (LinearLayout) findViewById(R.id.LineerLayouImageActivity);

        //Bitmap bmp = BitmapFactory.decodeFile(path);
        BitmapDrawable bmp= new BitmapDrawable(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            li.setBackground(bmp);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
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
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        X = (int)(x*1.275) - 150;
        Y = (int)(y*1.1475) - 150;
        final int action = event.getAction();
        if  (X < 0 )  X = 0;
        if  (Y < 0 )  Y = 0;

        if(action == MotionEvent.ACTION_DOWN) {
            Intent i ;
            if (KAC_TANE > 2){
               i  = new Intent(ImageActivity.this, FeatureDetectionOnPhotoActivity3.class);
            }else {
                i = new Intent(ImageActivity.this, FeatureDetectionOnPhotoActivity2.class);
            }
            //imgPath1 = "3mv5o4tbdhn41o7ovp0cm9d5seLEFT.jpg";
            //imgPath2 = "3mv5o4tbdhn41o7ovp0cm9d5seRİGHT.jpg";
            //imgPath1 = "o9o6ethrg1h41qgmohb9i9omjfLEFT.jpg";
            //imgPath2 = "o9o6ethrg1h41qgmohb9i9omjfRİGHT.jpg";
            i.putExtra("IMG_PATH_1", imgPath1);
            i.putExtra("IMG_PATH_2", imgPath2);
            i.putExtra("NE_TARAF", neTaraf);
            i.putExtra("X", X);
            i.putExtra("Y", Y);
            i.putExtra("KAC_TANE", KAC_TANE);
            startActivity(i);

            Toast.makeText(getApplicationContext(), "x:" + (int) (x * 1.275) + "\ny:  " + (int) (y * 1.1475), Toast.LENGTH_SHORT).show();
        }



 /*
        try {
            X = (int)(x*1.275) - 200;
            Y = (int)(y*1.1475) - 200;
           AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
            builder1.setMessage("Hesaplama İşlemine Devam Etmek İstiyor musunuz?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent i = new Intent(ImageActivity.this, FeatureDetectionOnPhotoActivity2.class);
                            i.putExtra("IMG_PATH_1", imgPath1);
                            i.putExtra("IMG_PATH_2", imgPath2);
                            i.putExtra("X", X);
                            i.putExtra("Y", Y);
                            startActivity(i);
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return super.onTouchEvent(event);
    }


}
