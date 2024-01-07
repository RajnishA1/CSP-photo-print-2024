package com.rajnish.photoprints;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class CreatePhoto {

    Bitmap newBitmap,
            FrontBitmap, BackBitmap,
            NewFrontBitmap, newBackBitmap, newoutputImage, passbookimage;

    public void onsSharePdf(Context context, String PdfName) {

        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/" + PdfName + ".pdf");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                context, "com.rajnish.photoprints.fileprovider", new File(
                        file2.getPath()
                )
        ));
        context.startActivity(Intent.createChooser(intent, "share the file"));


    }

    public void AdharCreatePDF(Context context, Uri FrontUri, Uri BackUri, int density) {
        FrontBitmap = BitmapFactory.decodeFile(FrontUri.getPath());
        BackBitmap = BitmapFactory.decodeFile(BackUri.getPath());
        NewFrontBitmap = Bitmap.createScaledBitmap(FrontBitmap, 1100,
                720, true);
        NewFrontBitmap.setDensity(density * 6);

        newBackBitmap = Bitmap.createScaledBitmap(BackBitmap, 1100,
                720, true);
        newBackBitmap.setDensity(density * 6);

        PdfDocument myPdfDoccument = new PdfDocument();


        Paint paint = new Paint();
        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(395, 470, 1).create();
        PdfDocument.Page myPage1 = myPdfDoccument.startPage(mypageinfo1);
        Canvas canvas = myPage1.getCanvas();


        canvas.drawBitmap(NewFrontBitmap, 10, 10, paint);

        canvas.drawBitmap(newBackBitmap, 200, 10, paint);


        myPdfDoccument.finishPage(myPage1);


        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/final.pdf");

        try {
            myPdfDoccument.writeTo(new FileOutputStream(file2));

            Toast.makeText(context, "Ready to Print", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }


        myPdfDoccument.close();


    }


    public void PassbookPDF(Context context, Uri uri, int density) {
        passbookimage = BitmapFactory.decodeFile(uri.getPath());

        passbookimage = Bitmap.createScaledBitmap(passbookimage, 2000,
                1540, true);
        passbookimage.setDensity(density * 6);


        PdfDocument myPdfDoccument = new PdfDocument();


        Paint paint = new Paint();
        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(395, 470, 1).create();
        PdfDocument.Page myPage1 = myPdfDoccument.startPage(mypageinfo1);
        Canvas canvas = myPage1.getCanvas();


        canvas.drawBitmap(passbookimage, 30, 10, paint);


        myPdfDoccument.finishPage(myPage1);


        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/final.pdf");

        try {
            myPdfDoccument.writeTo(new FileOutputStream(file2));

            Toast.makeText(context, "Ready to Print", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }


        myPdfDoccument.close();


    }





    public void createPDF(Context context, int photocount, Bitmap bitmap, int density) {

        shortcodeforphoto(density, bitmap, 1000, 1040);

        PdfDocument myPdfDoccument = new PdfDocument();

        Paint paint = new Paint();
        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page myPage1 = myPdfDoccument.startPage(mypageinfo1);

        try {
            Canvas canvas = myPage1.getCanvas();

            if (photocount == 6) {

                canvas.drawBitmap(newoutputImage, 10, 15, paint);
                canvas.drawBitmap(newoutputImage, 110, 15, paint);
                canvas.drawBitmap(newoutputImage, 209, 15, paint);
                canvas.drawBitmap(newoutputImage, 308, 15, paint);
                canvas.drawBitmap(newoutputImage, 407, 15, paint);
                canvas.drawBitmap(newoutputImage, 506, 15, paint);
                myPdfDoccument.finishPage(myPage1);

            }
            if (photocount == 12) {

                canvas.drawBitmap(newoutputImage, 10, 15, paint);
                canvas.drawBitmap(newoutputImage, 110, 15, paint);
                canvas.drawBitmap(newoutputImage, 209, 15, paint);
                canvas.drawBitmap(newoutputImage, 308, 15, paint);
                canvas.drawBitmap(newoutputImage, 407, 15, paint);
                canvas.drawBitmap(newoutputImage, 506, 15, paint);

                //// bottom after 6
                int topAfterSix = 146;
                canvas.drawBitmap(newoutputImage, 10, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 110, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 209, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 308, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 407, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 506, topAfterSix, paint);

                myPdfDoccument.finishPage(myPage1);

            }
            if (photocount == 24) {

                canvas.drawBitmap(newoutputImage, 10, 15, paint);
                canvas.drawBitmap(newoutputImage, 110, 15, paint);
                canvas.drawBitmap(newoutputImage, 209, 15, paint);
                canvas.drawBitmap(newoutputImage, 308, 15, paint);
                canvas.drawBitmap(newoutputImage, 407, 15, paint);
                canvas.drawBitmap(newoutputImage, 506, 15, paint);

                //// bottom after 6
                int topAfterSix = 146;
                canvas.drawBitmap(newoutputImage, 10, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 110, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 209, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 308, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 407, topAfterSix, paint);
                canvas.drawBitmap(newoutputImage, 506, topAfterSix, paint);

                /// bottom 12 after 12th
                int topAfterTwelve = 273;
                canvas.drawBitmap(newoutputImage, 10, topAfterTwelve, paint);
                canvas.drawBitmap(newoutputImage, 110, topAfterTwelve, paint);
                canvas.drawBitmap(newoutputImage, 209, topAfterTwelve, paint);
                canvas.drawBitmap(newoutputImage, 308, topAfterTwelve, paint);
                canvas.drawBitmap(newoutputImage, 407, topAfterTwelve, paint);
                canvas.drawBitmap(newoutputImage, 506, topAfterTwelve, paint);

                /// bottom 18 after 12th
                int topAfterEighteen = 403;

                canvas.drawBitmap(newoutputImage, 10, topAfterEighteen, paint);
                canvas.drawBitmap(newoutputImage, 110, topAfterEighteen, paint);
                canvas.drawBitmap(newoutputImage, 209, topAfterEighteen, paint);
                canvas.drawBitmap(newoutputImage, 308, topAfterEighteen, paint);
                canvas.drawBitmap(newoutputImage, 407, topAfterEighteen, paint);
                canvas.drawBitmap(newoutputImage, 506, topAfterEighteen, paint);

                myPdfDoccument.finishPage(myPage1);

            }
        } catch (RuntimeException e) {
            Toast.makeText(context, "Something went wrong Please try again", Toast.LENGTH_SHORT).show();
        }


        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/final.pdf");


        try {
            myPdfDoccument.writeTo(new FileOutputStream(file2));
            Toast.makeText(context, "Ready to Print", Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDoccument.close();

    }



    public void createPDFForEption(Context context, int photocount, Bitmap bitmap, int density) {
        shortcodeforphoto(density, bitmap, 710, 1065);

        PdfDocument myPdfDoccument = new PdfDocument();

        Paint paint = new Paint();
        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(360, 504, 1).create();
        PdfDocument.Page myPage1 = myPdfDoccument.startPage(mypageinfo1);


        Canvas canvas = myPage1.getCanvas();

        if (photocount == 4) {


            canvas.drawBitmap(newoutputImage, 15, 10, paint);
            canvas.drawBitmap(newoutputImage, 103, 10, paint);
            canvas.drawBitmap(newoutputImage, 191, 10, paint);
            canvas.drawBitmap(newoutputImage, 280, 10, paint);
            myPdfDoccument.finishPage(myPage1);


        }


        if (photocount == 8) {


            canvas.drawBitmap(newoutputImage, 15, 10, paint);
            canvas.drawBitmap(newoutputImage, 103, 10, paint);
            canvas.drawBitmap(newoutputImage, 191, 10, paint);
            canvas.drawBitmap(newoutputImage, 280, 10, paint);

            //// after 4


            canvas.drawBitmap(newoutputImage, 15, 130, paint);
            canvas.drawBitmap(newoutputImage, 103, 130, paint);
            canvas.drawBitmap(newoutputImage, 191, 130, paint);
            canvas.drawBitmap(newoutputImage, 280, 130, paint);


            myPdfDoccument.finishPage(myPage1);


        }

        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/final.pdf");


        try {


            myPdfDoccument.writeTo(new FileOutputStream(file2));
            Toast.makeText(context, "Ready to Print", Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDoccument.close();

    }

    public void FourIntoSixPaper(Context context, int photocount, Bitmap bitmap, int density) {
        shortcodeforphoto(density, bitmap, 1160, 1100);

        PdfDocument myPdfDoccument = new PdfDocument();

        Paint paint = new Paint();
        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(288, 432, 1).create();
        PdfDocument.Page myPage1 = myPdfDoccument.startPage(mypageinfo1);


        Canvas canvas = myPage1.getCanvas();

        if (photocount == 4) {
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            Bitmap n = Bitmap.createBitmap(newoutputImage, 0, 0, newoutputImage.getWidth(), newoutputImage.getHeight(), matrix, true);


            canvas.drawBitmap(n, 20, 8, paint);
            canvas.drawBitmap(n, 151, 8, paint);

            canvas.drawBitmap(n, 20, 110, paint);
            canvas.drawBitmap(n, 151, 110, paint);

            canvas.drawBitmap(n, 20, 212, paint);
            canvas.drawBitmap(n, 151, 212, paint);

            canvas.drawBitmap(n, 20, 314, paint);
            canvas.drawBitmap(n, 151, 314, paint);


            myPdfDoccument.finishPage(myPage1);


        }


        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/final.pdf");


        try {


            myPdfDoccument.writeTo(new FileOutputStream(file2));
            Toast.makeText(context, "Ready to Print", Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDoccument.close();

    }

    public void createPDFFor3(Context context, int photocount, Bitmap bitmap, int density) {
        shortcodeforphoto(density, bitmap, 1120, 1680);

        PdfDocument myPdfDoccument = new PdfDocument();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        PdfDocument.PageInfo mypageinfo1 = new PdfDocument.PageInfo.Builder(421, 595, 1).create();
        PdfDocument.Page myPage1 = myPdfDoccument.startPage(mypageinfo1);

        Canvas canvas = myPage1.getCanvas();

        if (photocount == 3) {

            canvas.drawBitmap(newoutputImage, 15, 10, paint);
            canvas.drawBitmap(newoutputImage, 152, 10, paint);
            canvas.drawBitmap(newoutputImage, 289, 10, paint);

            myPdfDoccument.finishPage(myPage1);

        }


        if (photocount == 6) {

            canvas.drawBitmap(newoutputImage, 15, 10, paint);
            canvas.drawBitmap(newoutputImage, 152, 10, paint);
            canvas.drawBitmap(newoutputImage, 289, 10, paint);

            //// after 3

            canvas.drawBitmap(newoutputImage, 15, 190, paint);
            canvas.drawBitmap(newoutputImage, 152, 190, paint);
            canvas.drawBitmap(newoutputImage, 289, 190, paint);

            myPdfDoccument.finishPage(myPage1);

        }

        File file2 = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/final.pdf");


        try {

            myPdfDoccument.writeTo(new FileOutputStream(file2));
            Toast.makeText(context, "Ready to Print", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDoccument.close();

    }


    public void shortcodeforphoto(int density, Bitmap camerabitmp, int recievedwidth, int recievedheight) {


        float ratio = Math.min(
                (float) recievedwidth / camerabitmp.getWidth(),
                (float) recievedheight / camerabitmp.getHeight());
        int width = Math.round(ratio * camerabitmp.getWidth());
        int height = Math.round(ratio * camerabitmp.getHeight());


        newBitmap = Bitmap.createScaledBitmap(camerabitmp, width,
                height, true);

        newBitmap.setDensity(density);


        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(20));
        myFilter.addSubFilter(new ContrastSubFilter(1.1f));

        newoutputImage = myFilter.processFilter(newBitmap);
    }
}
