package exp1.sensor.oda114.sensorapp6;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

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

    double mesafe = 0;
    Button btn ;
    int cal = 0 ;
    ArrayList<Double> saniyelik = new ArrayList<>();
    ArrayList<Double> saniyelikMesurement = new ArrayList<>();
    ArrayList<Double> saniyelikMesafe = new ArrayList<>();
    ArrayList<ArrayList<Double>> accValueMap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
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

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void onSensorChanged(SensorEvent event) {
        long temp = System.currentTimeMillis();

        long timediff = temp - currentTimeinMilisecoond;
        Sensor sensor = event.sensor;
        if (timediff >= 10){

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
}
