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

public class ManualVoterPrintClass {
    Context context;
    Bitmap newBitmap, imageBitmap;


    public ManualVoterPrintClass(Context context) {
        this.context = context;
    }

    public void ManualVoterCardPrintFunction(Context context, int density, Bitmap ImageBitmap) {

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
        String Str_district = shAInEDetailSharedPref.getString("state", "");

        SharedPreferences shAInHDetailSharedPref = context.getSharedPreferences("AInHDetailSharedPref", Context.MODE_PRIVATE);
        String Str_HiName = shAInHDetailSharedPref.getString("HiName", "");
        String Str_HiFatherName = shAInHDetailSharedPref.getString("HiFatherName", "");
        String Str_HiHouseNo = shAInHDetailSharedPref.getString("HiHouseNo", "");
        String Str_HiGaliLocality = shAInHDetailSharedPref.getString("HiGaliLocality", "");
        String Str_HiPostOffice = shAInHDetailSharedPref.getString("HiPoliceStation", "");
        String Str_Hi_district = shAInHDetailSharedPref.getString("HiDistrict", "");


        SharedPreferences shVoterDetailsActivity =context.getSharedPreferences("ShVoterDetailsActivity",Context.MODE_PRIVATE);
        String Str_partName = shVoterDetailsActivity.getString("partName", "");
        String Str_partNumber = shVoterDetailsActivity.getString("partNumber", "");
        String Str_Assembly = shVoterDetailsActivity.getString("Assembly", "");
        String Str_Hi_PartName = shVoterDetailsActivity.getString("Hi_PartName", "");
        String Str_Hi_Assembly = shVoterDetailsActivity.getString("Hi_Assembly", "");
        String selectedRelation = shVoterDetailsActivity.getString("selectedRelation", "");





        String patiKaNameInHindi = "";
        String  patiKaName= "";

        if (selectedRelation.equals("Father")) {
            patiKaNameInHindi = "पिता का नाम";
            patiKaName = "Father's Name";


        }
        if (selectedRelation.equals("Husband")) {
            patiKaNameInHindi = "पति का नाम";
            patiKaName = "Husband's Name";

        }


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


        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.voter_dummy_image, null);
        assert drawable != null;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        shortcodeforphoto(density, bitmap, 870, 870, 0);
        shortcodeforphoto(density, ImageBitmap, 1000, 960, 1);

        Date today = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String dateToStr = format.format(today);


        PdfDocument myPdfDoccument = new PdfDocument();


        Paint paint = new Paint();
        Paint namePaint = new Paint();
        namePaint.setColor(Color.rgb(0, 0, 0));
        namePaint.setTextSize(6f);
        namePaint.setFakeBoldText(true);

        /// rotate text


        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page myPage1 = myPdfDoccument.startPage(mypageinfo1);
        Canvas canvas = myPage1.getCanvas();


        canvas.drawBitmap(newBitmap, 30, 20, paint);


        /// image
        canvas.drawBitmap(imageBitmap, 90, 90, paint);


        canvas.drawBitmap(imageBitmap, 90, 90, paint);
        namePaint.setTextSize(8f);
        canvas.drawText(Str_adharNumber, 150, 84, namePaint);

        namePaint.setTextSize(7f);
        canvas.drawText(Str_HiName, 105, 208, namePaint);
        canvas.drawText(Str_name, 105, 230, namePaint);

        namePaint.setTextSize(6f);
        namePaint.setFakeBoldText(true);
        canvas.drawText(patiKaNameInHindi, 52, 252, namePaint);
        canvas.drawText(patiKaName, 52, 273, namePaint);

        namePaint.setTextSize(7f);
        canvas.drawText(Str_HiFatherName, 105, 252, namePaint);
        canvas.drawText(Str_fatherName, 105, 273, namePaint);

        //second display
        namePaint.setTextSize(5f);
        canvas.drawText(HiGender + "/" + gender, 305, 45, namePaint);
        canvas.drawText(Str_DateOfBirth, 305, 57, namePaint);

        // address
        canvas.drawText("पता : " + Str_HiGaliLocality+ ", " + Str_HiHouseNo +", " , 235, 66, namePaint);
        canvas.drawText( Str_HiPostOffice + ", " + Str_Hi_district, 235, 74, namePaint);

        canvas.drawText("Address : " + Str_GaliLocality + ", " + Str_houseNo, 235, 83, namePaint);
        canvas.drawText(Str_postOffice + ", " + Str_district , 235, 91, namePaint);

        namePaint.setTextSize(5f);
        canvas.drawText(dateToStr, 248, 107, namePaint);

        canvas.drawText(Str_Hi_Assembly, 235, 132, namePaint);

        canvas.drawText(Str_Assembly, 235, 145, namePaint);


        canvas.drawText(Str_partNumber+" "+Str_Hi_PartName , 280, 156, namePaint);
        canvas.drawText(Str_partNumber+" "+Str_partName, 278, 164, namePaint);


        myPdfDoccument.finishPage(myPage1);

        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/voter1234.pdf");
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
