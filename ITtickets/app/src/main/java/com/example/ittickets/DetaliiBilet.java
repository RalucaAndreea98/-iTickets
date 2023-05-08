package com.example.ittickets;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import android.graphics.Bitmap;
import android.graphics.Color;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;



public class DetaliiBilet extends AppCompatActivity {
    TextView id2,linie2,tip2,pret2;
    private Button button;
    String PretBilet ,TipBilet,LinieBilet,IdBilet,trimis,EmailTxt,userId;
    private ImageView imageView, imageView1;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String Txt2,Txt1;
    //private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalii_bilet);
        imageView = findViewById(R.id.imageView);
        imageView1 = findViewById(R.id.imageView1);
        id2=findViewById(R.id.id2);

        id2.setText("Id" + IdBilet);
        id2=findViewById(R.id.id2);
        IdBilet=getIntent().getStringExtra("Id");
        id2.setText("Id" + IdBilet);

        linie2=findViewById(R.id.linie2);
        LinieBilet=getIntent().getStringExtra("Linie");
        linie2.setText(LinieBilet);

        tip2=findViewById(R.id.tip2);
        TipBilet=getIntent().getStringExtra("Tip");
        tip2.setText("Tip: " + TipBilet);

        pret2=findViewById(R.id.pret2);
        PretBilet=getIntent().getStringExtra("Pret");
        pret2.setText("Pret: " + PretBilet);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userId=fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                EmailTxt=documentSnapshot.getString("email");
                //em = EmailTxt.split("@");
                //  Toast.makeText(getApplicationContext(),EmailTxt,Toast.LENGTH_SHORT).show();

                trimis= IdBilet + "@" + LinieBilet + "@" + TipBilet + "@" + PretBilet;
                button=(Button) findViewById(R.id.foloseste);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        writeIstoricFile();
                        deleteFile();
                        ImageView imageView =(ImageView) findViewById(R.id.imageView);
                        imageView.setVisibility(View.VISIBLE);
                        QRCodeButton(view);
                        // openMain();
                    }
                });

                button=(Button) findViewById(R.id.trimite);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        writeIstoricFile1();
                        deleteFile();
                        ImageView imageView =(ImageView) findViewById(R.id.imageView1);
                        imageView.setVisibility(View.VISIBLE);
                        QRCodeButton1(view);
                        // openMain();
                    }
                });


            }
        });


    }








    public void deleteFile() {
        EmailTxt=EmailTxt + ".txt";
        String id = "",lines="";
        String linie = "";
        String tip = "";
        String pret = "",bun="",bun2="",linii_bune="";
        String id_sters="6";
        String temp1="",temp2="nu";
        String[] parts;
        try {

            FileInputStream fileInputStream = openFileInput(EmailTxt);

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            while ((lines = bufferedReader.readLine()) != null) {

                linii_bune=lines;
                parts = linii_bune.split("@");

                if(!(parts[0].equals(IdBilet)))
                    bun=bun + lines + "\n";
                //if(!(lines.startsWith("3/")))
                //  bun=bun + "," +  parts[0] + "," + "\n";

            }
            //scrierea dupa ce a fost sters
            try{

                FileOutputStream fileOutputStream=openFileOutput(EmailTxt,MODE_PRIVATE);
                //Toast.makeText(getApplicationContext(),"Merge",Toast.LENGTH_SHORT).show();
                fileOutputStream.write( bun.getBytes());
                fileOutputStream.close();

            }catch (FileNotFoundException e){
                //Toast.makeText(getApplicationContext(),"Nu Merge" + Environment.getExternalStorageDirectory().toString(),Toast.LENGTH_SHORT).show();
                // getFileStreamPath("new.xml");
                //Toast.makeText(getApplicationContext(),"nu Merge  " + getFileStreamPath(EmailTxt),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }



            inputStreamReader.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void writeIstoricFile(){

        String data= getCurrentDate();


        String salveazatxt="";
        Txt2=EmailTxt +"Istoric" + ".txt";
        //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();

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

        tot= "Ai folosit"  + "@" + data +"@" + LinieBilet + "@" + TipBilet + "@" + PretBilet + "\n"+salveazatxt ;
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

    public void writeIstoricFile1(){

        String data= getCurrentDate();


        String salveazatxt="";
        Txt2=EmailTxt +"Istoric" + ".txt";
        //Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();

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
            salveazatxt=creeaza3();
            // e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }


        String tot;
        String id= UUID.randomUUID().toString() ;

        tot= "Ai trimis"  + "@" + data +"@" + LinieBilet + "@" + TipBilet + "@" + PretBilet + "\n"+salveazatxt;
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

    private String creeaza3() {
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


    public static final String DATE_FORMAT= "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }






    public void QRCodeButton (View view)  {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(id2.getText().toString(), BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }}





    public void QRCodeButton1 (View view)  {

        QRCodeWriter qrCodeWriter1 = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter1.encode(trimis, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            imageView1.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
