package com.rajnish.photoprints;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.rajnish.photoprints.ads.Admob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class PassportSizePhoto extends AppCompatActivity implements OnUserEarnedRewardListener {

    Button printbtn, CreatePhotoBtn;
    TextView gallary, camera;
    ImageView imageView;
    Spinner spinner;
    int a;
    boolean afterOnePrint = false;


    public RewardedInterstitialAd mrewardedInterstitialAd;
    ReviewManager manager;
    ReviewInfo reviewInfo;
    ProgressBar progressBar;
    ProgressBar progressBarForCamera;


    Bitmap newBitmap;
    Dialog dialog;


    private static final String[] paths = {"3 Photo (4 x 6 inches)", "6 Photo (4 x 6 inches)", "4 Photo (5 x 7 inches) ", "8 Photo (5 x 7 inches) ", "6 Photo A4 (210 x 297mm)", "12 Photo A4 (210 x 297mm)", "24 Photo A4 (210 x 297mm)", "8 Photo (4 x 6 Landscape)"};


    static {
        System.loadLibrary("NativeImageProcessor");
    }

    PDFView pdfView;
    CreatePhoto createPhoto = new CreatePhoto();
    int density;
    int detectSpinnerPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_size_photo);

        printbtn = findViewById(R.id.printbtn);
        CreatePhotoBtn = findViewById(R.id.fourintoeightphotoprint);
        pdfView = findViewById(R.id.pdfView);
        camera = findViewById(R.id.Camera);
        gallary = findViewById(R.id.Gallary);
        imageView = findViewById(R.id.displayimage);
        spinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);
        progressBarForCamera = findViewById(R.id.progressBarForCamera);


        spinner.setVisibility(View.GONE);
        CreatePhotoBtn.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        printbtn.setVisibility(View.GONE);

        dialog = new Dialog(this);

        SharedPreferences sh = getSharedPreferences("Coins", 0);
        a = sh.getInt("Points", 10);
        if (a < 5) {
            loadAd();
        }


///// getting device density
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = DisplayMetrics.DENSITY_DEVICE_STABLE;


/// spinner work

        ArrayAdapter<String> adapter = new ArrayAdapter<>(PassportSizePhoto.this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CreatePhotoBtn.setEnabled(true);
                switch (position) {
                    case 0:
                        detectSpinnerPosition = 1;
                        break;
                    case 1:
                        detectSpinnerPosition = 2;
                        break;
                    case 2:
                        detectSpinnerPosition = 3;
                        break;
                    case 3:
                        detectSpinnerPosition = 4;
                        break;
                    case 4:
                        detectSpinnerPosition = 5;
                        break;
                    case 5:
                        detectSpinnerPosition = 6;
                        break;
                    case 6:
                        detectSpinnerPosition = 7;
                        break;
                    case 7:
                        detectSpinnerPosition = 8;
                        break;
                    default:
                        Toast.makeText(PassportSizePhoto.this, "Something went wrong", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PassportSizePhoto.this, "please select size", Toast.LENGTH_SHORT).show();
            }
        });


        gallary.setOnClickListener(v -> {

            gallary.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            if (Admob.mInterstitialAd != null) {

                Admob.mInterstitialAd.show(PassportSizePhoto.this);
                Admob.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Admob.mInterstitialAd = null;
                        Admob.loadInter(PassportSizePhoto.this);
                        ImagePicker.with(PassportSizePhoto.this)
                                .crop(3f, 4f)    //User can only select image from Gallery
                                .galleryOnly()
                                .start(5);
                    }
                });
            }else {
                ImagePicker.with(PassportSizePhoto.this)
                        .crop(3f, 4f)    //User can only select image from Gallery
                        .galleryOnly()
                        .start(5);
            }


        });
        camera.setOnClickListener(v -> {

            camera.setVisibility(View.INVISIBLE);
            progressBarForCamera.setVisibility(View.VISIBLE);
            if (Admob.mInterstitialAd != null) {

                Admob.mInterstitialAd.show(PassportSizePhoto.this);
                Admob.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Admob.mInterstitialAd = null;
                        Admob.loadInter(PassportSizePhoto.this);
                        ImagePicker.with(PassportSizePhoto.this)
                                .crop(3f, 4f)    //User can only select image from Camera
                                .cameraOnly()
                                .start(6);
                    }
                });
            }else {
                ImagePicker.with(PassportSizePhoto.this)
                        .crop(3f, 4f)    //User can only select image from Camera
                        .cameraOnly()
                        .start(6);
            }





        });
        CreatePhotoBtn.setOnClickListener(v -> {
            CreatePhotoBtn.setEnabled(false);
            pdfView.animate().alpha(1.0f).setDuration(2000);
            printbtn.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.VISIBLE);

            switch (detectSpinnerPosition) {
                case 1:
                    deviceDetectForEptionFor3(3);

                    break;
                case 2:
                    deviceDetectForEptionFor3(6);

                    break;
                case 3:
                    deviceDetectForEption(4);

                    break;
                case 4:
                    deviceDetectForEption(8);

                    break;
                case 5:
                    deviceDetect(6);

                    break;
                case 6:
                    deviceDetect(12);
                    break;
                case 7:
                    deviceDetect(24);
                    break;
                case 8:
                    detectDeviceForFourIntoSix(4);
            }


        });

        printbtn.setOnClickListener(v -> {


            if (checkConnection()) {


                if (a > 0) {

                    SharedPreferences sharedPreferences = getSharedPreferences("Coins", 0);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putInt("Points", a - 5);
                    myEdit.apply();
                    createPhoto.onsSharePdf(PassportSizePhoto.this, "final");
                } else {
                    if (afterOnePrint) {
                        createPhoto.onsSharePdf(PassportSizePhoto.this, "final");
                    } else {

                        LowCoinsDialog();

                    }

                }
            } else {
                Toast.makeText(PassportSizePhoto.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void detectDeviceForFourIntoSix(int photocount) {
        CreatePhoto createPhoto2 = new CreatePhoto();
        createPhoto2.FourIntoSixPaper(PassportSizePhoto.this, photocount, newBitmap, density * 9);
        extraCode();
        InAppReview();

    }

    public void deviceDetect(int photocount) {
        CreatePhoto createPhoto2 = new CreatePhoto();
        createPhoto2.createPDF(PassportSizePhoto.this, photocount, newBitmap, density * 9);
        extraCode();


    }


    public void deviceDetectForEption(int photocount) {
        CreatePhoto createPhoto2 = new CreatePhoto();
        createPhoto2.createPDFForEption(PassportSizePhoto.this, photocount, newBitmap, density * 9);
        extraCode();

    }

    public void deviceDetectForEptionFor3(int photocount) {
        CreatePhoto createPhoto2 = new CreatePhoto();
        createPhoto2.createPDFFor3(PassportSizePhoto.this, photocount, newBitmap, density * 9);
        extraCode();

    }

    public void extraCode() {
        pdfView.setVisibility(View.VISIBLE);
        printbtn.setVisibility(View.VISIBLE);
        File file22 = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/final.pdf");
        pdfView.fromFile(file22).load();
        InAppReview();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        camera.setVisibility(View.VISIBLE);
        gallary.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        progressBarForCamera.setVisibility(View.INVISIBLE);


        if (requestCode == 5 && resultCode == RESULT_OK) {
            assert data != null;

            Uri uri = data.getData();
            Bitmap CropBitmap = uriToBitmap(uri);
            assert CropBitmap != null;
            newBitmap = addBorderToBitmap(CropBitmap);
            imageView.setImageBitmap(newBitmap);
            spinner.setVisibility(View.VISIBLE);
            CreatePhotoBtn.setVisibility(View.VISIBLE);
            CreatePhotoBtn.setEnabled(true);


        }


        if (requestCode == 6 && resultCode == RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();

            Bitmap CropBitmap = uriToBitmap(uri);
            assert CropBitmap != null;
            newBitmap = addBorderToBitmap(CropBitmap);
            imageView.setImageBitmap(newBitmap);
            spinner.setVisibility(View.VISIBLE);
            CreatePhotoBtn.setVisibility(View.VISIBLE);
            CreatePhotoBtn.setEnabled(true);

        }


    }

    //Convert Uri to Bitmap
    private Bitmap uriToBitmap(Uri imageUri) {
        try {
            // Get the input stream of the image file
            InputStream input = getContentResolver().openInputStream(imageUri);

            // Decode the input stream into a bitmap
            return BitmapFactory.decodeStream(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add border to the bitmap
    private Bitmap addBorderToBitmap(Bitmap bmp) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + 2 * 2, bmp.getHeight() + 2 * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(android.graphics.Color.BLACK);
        canvas.drawBitmap(bmp, 2, 2, null);
        return bmpWithBorder;
    }


    public void InAppReview() {
        manager = ReviewManagerFactory.create(this);
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(PassportSizePhoto.this, reviewInfo);
                flow.addOnSuccessListener(unused -> {

                });

            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadAd() {
        // Use the test ad unit ID to load an ad.
        RewardedInterstitialAd.load(this, "ca-app-pub-1002841467878412/7180384425",
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        super.onAdLoaded(ad);

                        mrewardedInterstitialAd = ad;


                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        loadAd();

                    }
                });
    }


    private void LowCoinsDialog() {
        dialog.setContentView(R.layout.low_blance_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close = dialog.findViewById(R.id.close_image_btn);
        ProgressBar progressBarD = dialog.findViewById(R.id.progressBar);
        TextView watch_ads = dialog.findViewById(R.id.watch_ads);
        Button buy_coin = dialog.findViewById(R.id.buy_coin);
        dialog.show();
        close.setOnClickListener(view -> dialog.dismiss());
        buy_coin.setOnClickListener(view -> {
            Intent i = new Intent(PassportSizePhoto.this, BuyCoins.class);
            startActivity(i);
        });
        watch_ads.setOnClickListener(view -> {
            progressBarD.setVisibility(View.VISIBLE);
            watch_ads.setVisibility(View.INVISIBLE);


            if (mrewardedInterstitialAd != null) {
                mrewardedInterstitialAd.show(PassportSizePhoto.this, PassportSizePhoto.this);
                mrewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                });


            }
        });


    }


    boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        dialog.dismiss();
        createPhoto.onsSharePdf(PassportSizePhoto.this, "final");
        afterOnePrint = true;
        mrewardedInterstitialAd = null;

    }
}
