package com.rajnish.photoprints;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManualPrintClass extends QrCodeGenerator {


    Bitmap newBitmap, imageBitmap;
    Context context;

    public ManualPrintClass(Context context) {
        this.context = context;
    }


    public void ManualAdharPrintFunction(Context context, int density, Bitmap ImageBitmap) {

        Date today = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String dateToStr = format.format(today);


        SharedPreferences sh = context.getSharedPreferences("PDetailSharedPref", Context.MODE_PRIVATE);

        String Str_name = sh.getString("name", "");
        String Str_fatherName = sh.getString("father", "");
        String Str_DateOfBirth = sh.getString("DateOfBirth", "");
        String Str_adharNumber = sh.getString("aadhar", "");
        String selectedGender = sh.getString("gender", "");


        SharedPreferences shAInEDetailSharedPref = context.getSharedPreferences("AInEDetailSharedPref", Context.MODE_PRIVATE);
        String Str_houseNo = shAInEDetailSharedPref.getString("house", "");
        String Str_GaliLocality = shAInEDetailSharedPref.getString("galiLocality", "");
        String Str_postOffice = shAInEDetailSharedPref.getString("postOffice", "");
        String Str_state = shAInEDetailSharedPref.getString("state", "");
        String Str_issueDate = shAInEDetailSharedPref.getString("issueDate",dateToStr);
        String Str_downloadDate = shAInEDetailSharedPref.getString("downloadDate",dateToStr);

        if(Str_issueDate.isEmpty()){
           Str_issueDate = dateToStr;
        }
        if(Str_downloadDate.isEmpty()){
            Str_downloadDate = dateToStr;
        }


        SharedPreferences shAInHDetailSharedPref = context.getSharedPreferences("AInHDetailSharedPref", Context.MODE_PRIVATE);
        String Str_HiName = shAInHDetailSharedPref.getString("HiName", "");
        String Str_HiFatherName = shAInHDetailSharedPref.getString("HiFatherName", "");
        String Str_HiHouseNo = shAInHDetailSharedPref.getString("HiHouseNo", "");
        String Str_HiGaliLocality = shAInHDetailSharedPref.getString("HiGaliLocality", "");
        String Str_HiPoliceStation = shAInHDetailSharedPref.getString("HiPoliceStation", "");
        String Str_HiDistrict = shAInHDetailSharedPref.getString("HiDistrict", "");



        String gender = "";
        String HiGender = "";

        if (selectedGender.equals("Female")) {
            gender = "FEMALE";
            HiGender = "महिला";

        }
        if (selectedGender.equals("Male")) {
            gender = "MALE";
            HiGender = "पुरुष";

        }


        String AdharFirstFour = Str_adharNumber.substring(0, 4);
        String AdharMiddleFour = Str_adharNumber.substring(4, 8);
        String AdharLastFour = Str_adharNumber.substring(8, 12);


        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.trytwo, null);
        assert drawable != null;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        shortcodeforphoto(density, bitmap, 1170, 1170, 0);

        shortcodeforphoto(density, ImageBitmap, 600, 580, 1);

        //AadhaarInEnglishLogo


        /// Qr Code
        QrCodeGenerator qr = new QrCodeGenerator();
        String qrData = Str_name + "\n" + Str_fatherName + "\n" + Str_DateOfBirth + "\n" + Str_adharNumber + " " + context.getString(R.string.adharCardQrCodeTerms);
        Bitmap QrBitmap = qr.generateQRCode(qrData, 10, 10);





        PdfDocument myPdfDocument = new PdfDocument();

        /// normal text like name

        Paint paint = new Paint();

        Paint textPaint = new Paint();
        textPaint.setColor(Color.rgb(0, 0, 0));
        textPaint.setTextSize(8f);
        // adhar number

        Paint AdharNumber = new Paint();
        AdharNumber.setColor(Color.rgb(0, 0, 0));
        AdharNumber.setTextSize(12f);
        AdharNumber.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);


        Paint address = new Paint();
        address.setColor(Color.rgb(0, 0, 0));
        address.setTextSize(8f);

        Paint HeadingAddress = new Paint();
        HeadingAddress.setColor(Color.rgb(0, 0, 0));
        HeadingAddress.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        HeadingAddress.setTextSize(8f);

        Paint MeraAdhar = new Paint();
        MeraAdhar.setColor(Color.rgb(0, 0, 0));
        MeraAdhar.setLetterSpacing(0.2f);
        MeraAdhar.setTextSize(8f);
        MeraAdhar.setTextScaleX(1.8f);


        /// rotate text


        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page myPage1 = myPdfDocument.startPage(mypageinfo1);
        Canvas canvas = myPage1.getCanvas();



        canvas.drawBitmap(QrBitmap, 455, 60, paint);

        canvas.drawBitmap(newBitmap, 30, 20, paint);


        /// image

        canvas.drawBitmap(imageBitmap, 60, 60, paint);


        canvas.drawText(Str_HiName, 130, 70, textPaint);
        canvas.drawText(Str_name, 130, 82, textPaint);
        canvas.drawText("जन्म तिथि / DOB : " + Str_DateOfBirth, 130, 94, textPaint);
        canvas.drawText(HiGender + " / " + gender, 130, 106, textPaint);
        canvas.drawText(AdharFirstFour + " " + AdharMiddleFour + " " + AdharLastFour, 125, 160, AdharNumber);

        ///back of adhar card


        ///Address in hindi


        canvas.drawText("पता :", 300, 65, HeadingAddress);
        canvas.drawText(" "+Str_HiFatherName + " " + Str_HiHouseNo, 300, 75, address);
        canvas.drawText(Str_HiGaliLocality +" "+Str_HiPoliceStation, 300, 85, address);
        canvas.drawText(Str_HiDistrict, 300, 95, address);

        // Address in english

        canvas.drawText("Address :", 300, 108, HeadingAddress);
        canvas.drawText(Str_fatherName + " " + Str_houseNo, 300, 118, address);
        canvas.drawText(Str_GaliLocality + " " + Str_postOffice, 300, 128, address);
        canvas.drawText(Str_state, 300, 138, address);
        canvas.drawText(AdharFirstFour + " " + AdharMiddleFour + " " + AdharLastFour, 365, 160, AdharNumber);


        /// external Logo


        canvas.drawText("मेरा ", 105, 175, MeraAdhar);
        MeraAdhar.setColor(Color.rgb(255, 0, 0));
        canvas.drawText("आधार", 130, 175, MeraAdhar);
        MeraAdhar.setColor(Color.rgb(0, 0, 0));
        canvas.drawText(", मेरी पहचान", 165, 175, MeraAdhar);

        Paint Download_text = new Paint();
        Download_text.setTextSize(6f);


        canvas.save();
        canvas.rotate(90, 45, 60);
        canvas.drawText("Download Date: " + Str_downloadDate, 45, 60, Download_text);
        canvas.restore();
        canvas.save();
        canvas.rotate(90, 260, 75);
        canvas.drawText("Issue Date: " + Str_issueDate, 260, 75, Download_text);
        canvas.restore();


        myPdfDocument.finishPage(myPage1);


        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/Adhar1234.pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(file2));

            Toast.makeText(context, "Ready to Print", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }


        myPdfDocument.close();


    }

    public void shortcodeforphoto(int density, Bitmap camerabitmp, int recievedwidth, int recievedheight, int check) {


        float ratio = Math.min(
                (float) recievedwidth / camerabitmp.getWidth(),
                (float) recievedheight / camerabitmp.getHeight());
        int width = Math.round(ratio * camerabitmp.getWidth());
        int height = Math.round(ratio * camerabitmp.getHeight());
        if (check == 0) {
            newBitmap = Bitmap.createScaledBitmap(camerabitmp, width,

                    height, true);
            int newDensity = density / 4;
            newBitmap.setDensity(newDensity);
        } else {
            imageBitmap = Bitmap.createScaledBitmap(camerabitmp, width,
                    height, true);
            imageBitmap.setDensity(density);
        }


    }


}
