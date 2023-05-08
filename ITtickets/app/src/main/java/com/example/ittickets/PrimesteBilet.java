package com.example.ittickets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class PrimesteBilet extends AppCompatActivity {
    SurfaceView cameraView;
Button button;
    TextView txtResult,txtMuta;
    String userId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    String muta,EmailTxt,Txt1,Txt2;
    final int RequestCameraPermissionID=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primeste_bilet);
        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        userId=fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                EmailTxt=documentSnapshot.getString("email");
                //em = EmailTxt.split("@");
                //  Toast.makeText(getApplicationContext(),EmailTxt,Toast.LENGTH_SHORT).show();


                button = (Button) findViewById(R.id.button4);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        writeIstoricFile();
                        writeFile();
                    }
                });

            }
        });


        cameraView = findViewById(R.id.cameraPreview);
        txtResult = findViewById(R.id.showResult);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 480).build();
        //Now add event to show camera preview

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //Check permissions here first

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PrimesteBilet.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    //If permission Granted then start Camera
                    cameraSource.start(cameraView.getHolder());
                } catch (Exception e) {
                    Toast.makeText(PrimesteBilet.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();

            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> sparseArray = detections.getDetectedItems();
                if (sparseArray.size() != 0) {
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Here i am creating vibration so when we scan any barcode its vibrate
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(10);//here give the time in MilliSeconds
                            txtResult.setText(sparseArray.valueAt(0).displayValue);
                            muta = sparseArray.valueAt(0).displayValue;
                            //writeFile();
                            //Thats All

                        }
                    });


                }



            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case RequestCameraPermissionID:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        return;
                    }
                    try {
                        //If permission Granted then start Camera
                        cameraSource.start(cameraView.getHolder());
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        }
    }
    public void writeIstoricFile(){

        String data= getCurrentDate();


        String salveazatxt="";
        Txt2=EmailTxt +"Istoric" + ".txt";
        // Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();

        //mai intai citeste

        try{

            FileInputStream fileInputStream=openFileInput(Txt2);

            InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer=new StringBuffer();

            String lines;
            while((lines=bufferedReader.readLine()) !=null){
                stringBuffer.append(lines + "\n");
            }

            salveazatxt=stringBuffer.toString();

        }catch (FileNotFoundException e){
            salveazatxt=creeaza2();
            // e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }


        String tot;
        String id= UUID.randomUUID().toString() ;
        String[] parts;
        parts = muta.split("@");
        tot= "Ai primit"  + "@" + data +"@" + parts[1] + "@" + parts[2] + "@" + parts[3] + "\n" + salveazatxt ;
        try{

            FileOutputStream fileOutputStream=openFileOutput(Txt2,MODE_PRIVATE);
            //  Toast.makeText(getApplicationContext(),salveazatxt,Toast.LENGTH_SHORT).show();
            fileOutputStream.write( tot.getBytes());
            fileOutputStream.close();

        }catch (FileNotFoundException e){

            //  Toast.makeText(getApplicationContext(),"nu Merge  " + getFileStreamPath(Txt2),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private String creeaza2() {
        String salveaza="";
        try{

            FileOutputStream fileOutputStream=openFileOutput(Txt2,MODE_PRIVATE);
            //  Toast.makeText(getApplicationContext(),"da?",Toast.LENGTH_SHORT).show();
            fileOutputStream.close();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        try {

            FileInputStream fileInputStream = openFileInput(Txt2);

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }

            salveaza = stringBuffer.toString();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return salveaza;

    }
    public void writeFile(){
        //  Toast.makeText(getApplicationContext(),EmailTxt,Toast.LENGTH_SHORT).show();
        String salveazatxt="";
        Txt1=EmailTxt + ".txt";

        //mai intai citeste
    /*    try{

            FileOutputStream fileOutputStream=openFileOutput("bilete.txt",MODE_PRIVATE);
          //  Toast.makeText(getApplicationContext(),"da?",Toast.LENGTH_SHORT).show();
            fileOutputStream.close();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }


     */
        try{

            FileInputStream fileInputStream=openFileInput(Txt1);

            InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer=new StringBuffer();

            String lines;
            while((lines=bufferedReader.readLine()) !=null){
                stringBuffer.append(lines + "\n");
            }

            salveazatxt=stringBuffer.toString();

        }catch (FileNotFoundException e){
            salveazatxt=creeaza();
            // e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }


        String tot;
        String id= UUID.randomUUID().toString() ;

        tot= salveazatxt + muta + "\n";
        try{

            FileOutputStream fileOutputStream=openFileOutput(Txt1,MODE_PRIVATE);
            //  Toast.makeText(getApplicationContext(),salveazatxt,Toast.LENGTH_SHORT).show();
            fileOutputStream.write( tot.getBytes());
            fileOutputStream.close();

        }catch (FileNotFoundException e){

            Toast.makeText(getApplicationContext(),"nu Merge  " + getFileStreamPath(Txt1),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private String creeaza() {
        String salveaza="";
        try{

            FileOutputStream fileOutputStream=openFileOutput(Txt1 ,MODE_PRIVATE);
            //  Toast.makeText(getApplicationContext(),"da?",Toast.LENGTH_SHORT).show();
            fileOutputStream.close();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        try {

            FileInputStream fileInputStream = openFileInput(Txt1);

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }

            salveaza = stringBuffer.toString();

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return salveaza;

    }

    public static final String DATE_FORMAT= "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        // dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
}
