package exp1.sensor.oda114.sensorapp6.show;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import exp1.sensor.oda114.sensorapp6.R;
import exp1.sensor.oda114.sensorapp6.kmeans.Point;

public class ShowDistanceActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private String imgPath1 = "";
   HashMap<Point, Double> distMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_distance);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                imgPath1 = extras.getString("IMG_PATH_1");
                distMap = (HashMap<Point, Double>) extras.get("DİST_MAP");

            }
        } else {
            imgPath1  = (String) savedInstanceState.getSerializable("IMG_PATH_1");

        }
        String path = Environment.getExternalStorageDirectory() + "/AutoExperiment2/" + imgPath1;
        BitmapDrawable d = new BitmapDrawable(path);
        relativeLayout = (RelativeLayout) findViewById(R.id.showRelativelayout);
        relativeLayout.setBackgroundDrawable(d);

        TextView txtMeasure;


        Iterator<Point> keySetIterator = distMap.keySet().iterator();
        while(keySetIterator.hasNext()){
            Point key = keySetIterator.next();
            System.out.println("key: " + key + " value: " + distMap.get(key));
            txtMeasure = new TextView(this);
            txtMeasure.setText("----->" + distMap.get(key));




            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setX((float) (key.getX() / 1.275));
            layout.setY((float) ((key.getY()) / 1.1475));
            layout.setBackground(getDrawable(R.drawable.text_bg));
            layout.addView(txtMeasure);

            relativeLayout.addView(layout);

        }










    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_distance, menu);
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
}
