package exp1.sensor.oda114.sensorapp6.accdeneme;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import exp1.sensor.oda114.sensorapp6.R;

public class AccelerometerExample extends AppCompatActivity  implements SensorEventListener {

    TextView txtAcc;
    StringBuilder builder = new StringBuilder();
    public static final double X_THERESHOLD = 0.5;
    public static final double Y_THERESHOLD = 2.0;

    float [] history = new float[2];
    String [] direction = {"NONE","NONE"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer_example);


        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        txtAcc = (TextView) findViewById(R.id.txtAccle);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float xChange = history[0] - event.values[0];
        float yChange = history[1] - event.values[1];

        history[0] = event.values[0];
        history[1] = event.values[1];

        if (xChange > X_THERESHOLD){
            direction[0] = "SOL";
        }
        else if (xChange < -X_THERESHOLD){
            direction[0] = "SAG";
        }

        if (yChange > Y_THERESHOLD){
            direction[1] = "YUKARI";
        }
        else if (yChange < -Y_THERESHOLD){
            direction[1] = "ASAGI";
        }

        builder.setLength(0);
        builder.append("x: ");
        builder.append(direction[0]);
        builder.append(" y: ");
        builder.append(direction[1]);

        txtAcc.setText(builder.toString());

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}