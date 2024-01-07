package com.rajnish.photoprints;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.rajnish.photoprints.R;
import com.rajnish.photoprints.ads.Admob;
import com.rajnish.photoprints.nowInkotlin.utils.InternetUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;


public class AdharCardPrint extends AppCompatActivity implements OnUserEarnedRewardListener {

    Button printbtn;
    TextView ForntBtn, BackBtn;
    ImageView ImageFront, ImageBack;
    Uri Fronturl, BackUrl;
    PDFView pdfView;
    TextView textpreview;
    boolean frondatacheck = false;
    Dialog dialog;
    int checkside = 0;
    boolean afterOnePrint = false;
    ProgressBar progressBar, progressBar2;


    CreatePhoto createPhoto = new CreatePhoto();
    int density;
    public RewardedInterstitialAd mrewardedInterstitialAd;
    int a;
    private ActivityResultLauncher<Intent> imagePickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhar_card_print);
        ForntBtn = findViewById(R.id.FrontBtn);
        BackBtn = findViewById(R.id.BackBtn);
        pdfView = findViewById(R.id.adharpdf);
        printbtn = findViewById(R.id.Adharprintptn);
        ImageFront = findViewById(R.id.ImageFront);
        ImageBack = findViewById(R.id.ImageBack);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        textpreview = findViewById(R.id.textpreview);

        textpreview.setVisibility(View.GONE);

        pdfView.setVisibility(View.GONE);
        printbtn.setVisibility(View.GONE);
        BackBtn.setEnabled(false);


        dialog = new Dialog(this);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            // Start image cropping activity
                            startImageCropping(imageUri);
                        }
                    }
                });

        SharedPreferences sh = getSharedPreferences("Coins", 0);
        a = sh.getInt("Points", 10);
        if (a < 5) {
            loadAd();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.densityDpi;


        ForntBtn.setOnClickListener(v -> {
            ForntBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);

        });

        BackBtn.setOnClickListener(v -> {

            if (Admob.mInterstitialAd != null) {

                Admob.mInterstitialAd.show(AdharCardPrint.this);
                Admob.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Admob.mInterstitialAd = null;
                        Admob.loadInter(AdharCardPrint.this);
                        BackBtn.setVisibility(View.INVISIBLE);
                        progressBar2.setVisibility(View.VISIBLE);
                        checkside = 1;
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        imagePickerLauncher.launch(intent);
                    }
                });

            } else {
                BackBtn.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);
                checkside = 1;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }

        });


        printbtn.setOnClickListener(v -> {


            if (InternetUtils.INSTANCE.isInternetConnected(this)) {


                if (a > 0) {

                    SharedPreferences sharedPreferences = getSharedPreferences("Coins", 0);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putInt("Points", a - 5);
                    myEdit.apply();
                    createPhoto.onsSharePdf(AdharCardPrint.this, "final");
                } else {
                    if (afterOnePrint) {
                        createPhoto.onsSharePdf(AdharCardPrint.this, "final");
                    } else {
                        LowCoinsDialog();
                    }

                }
            } else {
                Toast.makeText(AdharCardPrint.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BackBtn.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.INVISIBLE);
        ForntBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        if (checkside == 0) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    assert result != null;

                    Fronturl = result.getUri();
                    ImageFront.setImageURI(Fronturl);
                    frondatacheck = true;
                    BackBtn.setEnabled(true);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    assert result != null;
                    Exception error = result.getError();
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (checkside == 1) {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    assert result != null;

                    BackUrl = result.getUri();
                    ImageBack.setImageURI(BackUrl);
                    createPhoto.AdharCreatePDF(AdharCardPrint.this, Fronturl, BackUrl, density);
                    pdfView.setVisibility(View.VISIBLE);
                    printbtn.setVisibility(View.VISIBLE);
                    textpreview.setVisibility(View.VISIBLE);

                    File file2 = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/final.pdf");
                    pdfView.fromFile(file2).load();
                    checkside = 0;

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    assert result != null;
                    Exception error = result.getError();
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
                }
            }

        }

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
        TextView watch_ads = dialog.findViewById(R.id.watch_ads);
        ProgressBar progressBarD = dialog.findViewById(R.id.progressBar);
        Button buy_coin = dialog.findViewById(R.id.buy_coin);
        dialog.show();
        close.setOnClickListener(view -> dialog.dismiss());
        buy_coin.setOnClickListener(view -> {
            Intent i = new Intent(AdharCardPrint.this, BuyCoins.class);
            startActivity(i);
        });
        watch_ads.setOnClickListener(view -> {
            progressBarD.setVisibility(View.VISIBLE);
            watch_ads.setVisibility(View.INVISIBLE);


            if (mrewardedInterstitialAd != null) {
                mrewardedInterstitialAd.show(AdharCardPrint.this, AdharCardPrint.this);
                mrewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                });


            }

        });


    }

    private void startImageCropping(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAutoZoomEnabled(true)
                .start(this);
    }


    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        dialog.dismiss();
        createPhoto.onsSharePdf(AdharCardPrint.this, "final");
        afterOnePrint = true;
        mrewardedInterstitialAd = null;
    }
}