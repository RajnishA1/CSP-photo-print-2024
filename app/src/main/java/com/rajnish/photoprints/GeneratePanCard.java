package com.rajnish.photoprints;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class GeneratePanCard {


    Bitmap SignatureBitmap, panBitMap,NewQrBitmap,PhotoBitmap;
    Context context;

    public GeneratePanCard(Context context) {
        this.context = context;
    }

    public void ManualPanPrintFunction(Context context, int density, Bitmap ImageBitmap, Bitmap Signature,String format){

        SharedPreferences sh = context.getSharedPreferences("PDetailSharedPref", Context.MODE_PRIVATE);

        String Str_name = sh.getString("name", "");
        String Str_fatherName = sh.getString("father", "");
        String Str_DateOfBirth = sh.getString("DateOfBirth", "");
        String Str_adharNumber = sh.getString("aadhar", "");

        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.pan_card, null);
        assert drawable != null;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        shortcodeforphoto(density, bitmap, 1170, 1170, 0);

        shortcodeforphoto(density, Signature, 600, 100, 1);

        shortcodeforphoto(density, ImageBitmap, 400, 400, 5);

        /// Qr Code
        QrCodeGenerator qr = new QrCodeGenerator();
        Bitmap QrBitmap = qr.generateQRCode(Str_name +"\n "+ Str_fatherName +"\n "+ Str_DateOfBirth+"\n " + Str_adharNumber + context.getString(R.string.adharCardQrCodeTerms),150,150);
        shortcodeforphoto(density, QrBitmap, 600, 600, 2);

        PdfDocument myPdfDoccument = new PdfDocument();

        Paint paint = new Paint();

        Paint textPaint = new Paint();
        textPaint.setColor(Color.rgb(0, 0, 0));
        textPaint.setTextSize(8f);
        textPaint.setLetterSpacing(0.1f);

        Paint acn = new Paint();
        acn.setTextSize(10);
        acn.setFakeBoldText(true);
        textPaint.setColor(Color.rgb(0, 0, 0));


        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page myPage1 = myPdfDoccument.startPage(mypageinfo1);
        Canvas canvas = myPage1.getCanvas();



        /// image

        canvas.drawBitmap(panBitMap, 30, 10, paint);
        canvas.drawBitmap(NewQrBitmap, 207, 66, paint);
        canvas.drawBitmap(PhotoBitmap, 48, 65, paint);
        canvas.drawBitmap(SignatureBitmap, 125, 155, paint);

        canvas.drawText(Str_adharNumber.toUpperCase(Locale.ROOT),120,95,acn);

        canvas.drawText(Str_name.toUpperCase(Locale.ROOT),53,125,textPaint);
        canvas.drawText(Str_fatherName.toUpperCase(Locale.ROOT),53,146,textPaint);

        ///back of Pan card
        Paint backText = new Paint();
        backText.setFakeBoldText(true);
        backText.setTextSize(6.3f);
        backText.setLetterSpacing(0.1f);



        textPaint.setTextSize(7);
        textPaint.setLetterSpacing(0);

        canvas.drawText(Str_DateOfBirth,53,172,textPaint);


        if(format.equals("radioNSDL")){
            canvas.drawText("इस कार्ड के खोने/पाने पर कृपया सूचित करें/लौटाएं:",310,40,backText);

            backText.setTextSize(5.3f);
            backText.setFakeBoldText(false);

            canvas.drawText("आयकर पैन सेवा इकाई, एन एस डी एल",310,52,backText);
            canvas.drawText("चौथी मंजिल, मंत्री स्टर्लिंग,",310,60,backText);
            canvas.drawText("फ्लॉट नं. 341, सर्वे नं, 997 / 8,",310,68,backText);
            canvas.drawText("मॉडल कालोनी, दीप बंगला चौक के पास,",310,76,backText);
            canvas.drawText("पुणे - 411 016.",310,84,backText);

            // in english
            backText.setFakeBoldText(true);
            backText.setTextSize(6.3f);
            canvas.drawText("If this card is lost / someone's lost card is found ,",310,96,backText);
            canvas.drawText("please inform / return to :,",310,104,backText);

            backText.setTextSize(5.3f);
            backText.setFakeBoldText(false);

            canvas.drawText("Income Tax PAN Services Unit, NSDL,",310,112,backText);
            canvas.drawText("4th Floor, Mantri Sterling,",310,120,backText);
            canvas.drawText("Plot No 341,Survey No 997/8,",310,128,backText);
            canvas.drawText("Model Colony, Near Deep Bungalow Chowk,",310,136,backText);
            canvas.drawText("Pune - 411 016",310,144,backText);
            canvas.drawText("Tel: 91-20-2721 8080, Fax: 91-202721 8081",310,156,backText);
            canvas.drawText("e-mail: tininfo@nsdl.co.in",310,164,backText);

        }
        if(format.equals("radioITD")){

            canvas.drawText("इस कार्ड के खोने/पाने पर कृपया सूचित करें /लौटाएं:",310,40,backText);

            backText.setTextSize(5.3f);
            backText.setFakeBoldText(false);

            canvas.drawText("संयुक्त निर्देशक (पद्धति) -1, पैन मॉड्यूल ",310,52,backText);
            canvas.drawText("9वीं मंजिल, आयकर भवन, सेक्टर -3,वैशाली,,",310,60,backText);
            canvas.drawText("गाजियाबाद - 201010, उत्तर प्रदेश",310,68,backText);


            // in english
            backText.setFakeBoldText(true);
            backText.setTextSize(6.3f);
            canvas.drawText("If this card is lost / someone's lost card is found ,",310,96,backText);
            canvas.drawText("please inform / return to :,",310,104,backText);

            backText.setTextSize(5.3f);
            backText.setFakeBoldText(false);

            canvas.drawText("Joint Director (Systems)-1, PAN Module,",310,112,backText);
            canvas.drawText("9th Floor, Aayakar Bhawan, Sector -3 Vaishali",310,120,backText);
            canvas.drawText("Ghaziabad - 201010, Uttar Pradesh",310,128,backText);


            canvas.drawText("Tel: 0120-2770078, Fax: 0120-2770078",310,156,backText);
            canvas.drawText("Mail-id: epan@incometax.gov.in",310,164,backText);
        }
        if(format.equals("radioUTIITSL")){
            canvas.drawText("If this card is lost / found, kindly inform / return to :",305,100,backText);

            backText.setTextSize(5.3f);
            backText.setFakeBoldText(false);

            canvas.drawText("Income Tax PAN Services Unit, UTIITSL ",305,108,backText);
            canvas.drawText("Plot No 3, Sector 11 CBD Belapur, ",305,116,backText);
            canvas.drawText("Navi Mumbai - 400 614. ",305,124,backText);

            backText.setFakeBoldText(true);
            backText.setTextSize(6.3f);

            canvas.drawText("इस कार्ड के खोने/पाने पर कृपया सूचित करें /लौटाएं:",305,136,backText);

            backText.setTextSize(5.3f);
            backText.setFakeBoldText(false);

            canvas.drawText("आयकर पैन सेवा यूनिट, UTIITSL ",305,144,backText);
            canvas.drawText("प्लाट नं : ३, सेक्टर ११ , सी.बी.डी.बेलापुर, ",305,152,backText);
            canvas.drawText("नवी मुंबई-४०० ६१४. ",305,160,backText);

            Paint drawRect = new Paint();
            drawRect.setColor(Color.BLACK);
            drawRect.setStyle(Paint.Style.STROKE);
            drawRect.setStrokeWidth(1);

            Rect rect = new Rect(520,110,420,160);
            canvas.drawRect(rect,drawRect);
            backText.setTextSize(6.3f);
            backText.setFakeBoldText(true);

            canvas.drawText("Aaykar Sampark Kendras",430,120,backText);
            backText.setTextSize(6.3f);
            canvas.drawText("For Income Tax Related",430,128,backText);
            canvas.drawText("Queries call Toll Free Nos.",430,136,backText);
            canvas.drawText("1961 or 18001801961.",430,144,backText);

        }


        myPdfDoccument.finishPage(myPage1);


        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/panPrint.pdf");

        try {
            myPdfDoccument.writeTo(new FileOutputStream(file2));

            Toast.makeText(context, "Ready to Print", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }


        myPdfDoccument.close();



    }
    public void shortcodeforphoto(int density, Bitmap camerabitmp, int recievedwidth, int recievedheight, int check) {


        float ratio = Math.min(
                (float) recievedwidth / camerabitmp.getWidth(),
                (float) recievedheight / camerabitmp.getHeight());
        int width = Math.round(ratio * camerabitmp.getWidth());
        int height = Math.round(ratio * camerabitmp.getHeight());
        if (check == 0) {
            panBitMap = Bitmap.createScaledBitmap(camerabitmp, width,

                    height, true);
            panBitMap.setDensity(density/4);
        }
        if(check == 2){
            NewQrBitmap = Bitmap.createScaledBitmap(camerabitmp, width,

                    height, true);
            NewQrBitmap.setDensity(density);
        }
        if(check == 5){
            PhotoBitmap =Bitmap.createScaledBitmap(camerabitmp, width,

                    height, true);
            PhotoBitmap.setDensity(density);
        }

        if(check ==1) {
            SignatureBitmap = Bitmap.createScaledBitmap(camerabitmp, recievedwidth,
                    recievedheight, true);
            SignatureBitmap.setDensity(density);
        }


    }

}
