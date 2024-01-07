package com.rajnish.photoprints;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.rajnish.photoprints.nowInkotlin.utils.InternetUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ManualPanCardActivity extends AppCompatActivity implements OnUserEarnedRewardListener {


    Button Submit, share, edit_btn;
    TextView ImageNameTextView, ImageNameTextViewS, ChoosePhoto, ChooseSignature;
    Bitmap ImageBitmap;
    Bitmap Signature;
    public RewardedInterstitialAd mrewardedInterstitialAd;
    Dialog dialog;
    ProgressBar progressBar;
    ProgressBar progressBarS;
    RadioButton radioNSDL;
    RadioButton radioUTIITSL;
    RadioButton radioITD;
    RadioGroup radioGroup;
    String format;

    PDFView pdfView;
    int density;
    CreatePhoto createPhoto = new CreatePhoto();
    SharedPreferences sh;
    int a;
    boolean afterOnePrint = false;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_pan_card);

        initWork();

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

        sh = getApplicationContext().getSharedPreferences("PDetailSharedPref", Context.MODE_PRIVATE);
        dialog = new Dialog(this);
        ChoosePhoto.setOnClickListener(v -> {
            ChoosePhoto.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            ImagePicker.with(ManualPanCardActivity.this).crop(2.4f, 2.9f)    //User can only select image from Gallery
                    .galleryOnly().start(5);

        });

        ChooseSignature.setOnClickListener(v -> {
            if(radioGroup.getCheckedRadioButtonId() == -1){
                Toast.makeText(this, "Select Format", Toast.LENGTH_SHORT).show();
            }else if(ImageBitmap==null){
                Toast.makeText(this, "Select Photo First", Toast.LENGTH_SHORT).show();
            }

            else {


                ChooseSignature.setVisibility(View.INVISIBLE);
                progressBarS.setVisibility(View.VISIBLE);



                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);


            }

        });


        SharedPreferences shs = getSharedPreferences("Coins", 0);
        a = shs.getInt("Points", 10);
        if (a < 10) {
            loadAd();
        }
        ///// getting device density
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = DisplayMetrics.DENSITY_DEVICE_STABLE;

        share.setOnClickListener(v -> {

            if (InternetUtils.INSTANCE.isInternetConnected(this)) {

                if (a > 5) {

                    SharedPreferences sharedPreferences = getSharedPreferences("Coins", 0);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putInt("Points", a - 10);
                    myEdit.apply();

                        shareFile();


                } else {
                    if(afterOnePrint){
                        shareFile();
                    }else {
                        LowCoinsDialog();

                    }

                }
            } else {
                Toast.makeText(ManualPanCardActivity.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
            }


        });

        edit_btn.setOnClickListener(v -> {
            Intent i = new Intent(ManualPanCardActivity.this, PresonalDetailsPage.class);
            i.putExtra("voterPrint", "panPrint");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();

        });


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.radioITD:
                    format = "radioITD";
                    break;
                case R.id.radioNSDL:
                    format = "radioNSDL";
                    break;
                case R.id.radioUTIITSL:
                    format = "radioUTIITSL";
                    break;
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.INVISIBLE);
        progressBarS.setVisibility(View.INVISIBLE);
        ChoosePhoto.setVisibility(View.VISIBLE);
        ChooseSignature.setVisibility(View.VISIBLE);

        if (requestCode == 5 && resultCode == RESULT_OK && sh.contains("name")) {
            assert data != null;
            Uri uri = data.getData();
            ImageBitmap = uriToBitmap(uri);
            ImageNameTextView.setText(uri.getPath());


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && sh.contains("name")) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri uri = result.getUri();
                Signature = uriToBitmap(uri);
                ImageNameTextViewS.setText(uri.getPath());
                GeneratePan();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }



    }

    private void startImageCropping(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAutoZoomEnabled(true)
                .start(this);
    }

    private void GeneratePan() {

        GeneratePanCard generatePanCard = new GeneratePanCard(ManualPanCardActivity.this);
        generatePanCard.ManualPanPrintFunction(this, density*9, ImageBitmap, Signature,format);
        VisibleAdjust();
    }

    private void VisibleAdjust() {
        File file22 = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/panPrint" + ".pdf");
        pdfView.fromFile(file22).load();
        share.setVisibility(View.VISIBLE);
        edit_btn.setVisibility(View.VISIBLE);
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


    private void LowCoinsDialog() {
        dialog.setContentView(R.layout.low_blance_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView close = dialog.findViewById(R.id.close_image_btn);
        ProgressBar progressBarD = dialog.findViewById(R.id.progressBar);
        TextView watch_ads = dialog.findViewById(R.id.watch_ads);
        Button buycoin = dialog.findViewById(R.id.buy_coin);

        dialog.show();
        close.setOnClickListener(view -> dialog.dismiss());

        watch_ads.setOnClickListener(view -> {
            progressBarD.setVisibility(View.VISIBLE);
            watch_ads.setVisibility(View.INVISIBLE);

            if (mrewardedInterstitialAd != null) {
                mrewardedInterstitialAd.show(ManualPanCardActivity.this, ManualPanCardActivity.this);
                mrewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                });


            }
        });
        buycoin.setOnClickListener(v -> {
            Intent intent = new Intent(ManualPanCardActivity.this, BuyCoins.class);
            startActivity(intent);
        });


    }


    private void shareFile() {
        createPhoto.onsSharePdf(ManualPanCardActivity.this, "panPrint");
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



    private void initWork() {
        Submit = findViewById(R.id.Submit);
        pdfView = findViewById(R.id.pdfView);
        share = findViewById(R.id.share);
        edit_btn = findViewById(R.id.edit_btn);
        ChoosePhoto = findViewById(R.id.ChoosePhoto);
        ChooseSignature = findViewById(R.id.ChooseSignature);
        ImageNameTextView = findViewById(R.id.ImageNameTextView);
        ImageNameTextViewS = findViewById(R.id.ImageNameTextViewS);
        progressBar = findViewById(R.id.progressBar);
        progressBarS = findViewById(R.id.progressBarS);
        radioGroup = findViewById(R.id.radioGroup);
        radioUTIITSL = findViewById(R.id.radioUTIITSL);
        radioNSDL = findViewById(R.id.radioNSDL);
        radioITD = findViewById(R.id.radioITD);
        share.setVisibility(View.GONE);
        edit_btn.setVisibility(View.GONE);

    }


    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        dialog.dismiss();

        shareFile();


        afterOnePrint = true;
        mrewardedInterstitialAd = null;
    }
}