package com.rajnish.photoprints;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCodeGenerator {



    public Bitmap generateQRCode(String input,int widths,int Height) {
        Bitmap bitmap = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(input, BarcodeFormat.QR_CODE, widths,Height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);

            if(widths==150){

// Get the width and height of the actual QR code
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();

// Find the x and y coordinates of the top-left corner of the QR code within the bitmap
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                int left = width;
                int top = height;
                int right = 0;
                int bottom = 0;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = pixels[y * width + x];
                        if (pixel == 0xff000000) {  // Check if pixel is black
                            if (x < left) {
                                left = x;
                            }
                            if (x > right) {
                                right = x;
                            }
                            if (y < top) {
                                top = y;
                            }
                            if (y > bottom) {
                                bottom = y;
                            }
                        }
                    }
                }

// Calculate the width and height of the actual QR code
                int qrWidth = right - left + 1;
                int qrHeight = bottom - top + 1;

// Crop the bitmap to the actual size of the QR code
                bitmap = Bitmap.createBitmap(bitmap, left, top, qrWidth, qrHeight);
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }




}
