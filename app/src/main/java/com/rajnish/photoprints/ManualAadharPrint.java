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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.rajnish.photoprints.nowInkotlin.utils.InternetUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ManualAadharPrint extends AppCompatActivity implements OnUserEarnedRewardListener {

    Button Submit, share, edit_btn;
    TextView ImageNameTextView, gallary;
    Bitmap ImageBitmap;
    public RewardedInterstitialAd mrewardedInterstitialAd;
    Dialog dialog;
    ProgressBar progressBar;

    PDFView pdfView;
    int density;
    CreatePhoto createPhoto = new CreatePhoto();
    SharedPreferences sh;
    String str_voterPrint = "";
    int a;
    boolean afterOnePrint = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_aadhar_print);




        initWork();

        Intent detectActivity = getIntent();
        str_voterPrint = detectActivity.getStringExtra("voterPrint");


        sh = getApplicationContext().getSharedPreferences("PDetailSharedPref", Context.MODE_PRIVATE);
        dialog = new Dialog(this);
        gallary.setOnClickListener(v -> {
            gallary.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            ImagePicker.with(ManualAadharPrint.this).crop(2.4f, 2.9f)    //User can only select image from Gallery
                    .galleryOnly().start(5);


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
                    if (str_voterPrint.equals("voterPrint")) {
                        shareFile("voter1234");
                    } else {
                        shareFile("Adhar1234");
                    }

                } else {
                    if (afterOnePrint && str_voterPrint.equals("voterPrint")) {
                        shareFile("voter1234");
                    } else if (afterOnePrint) {
                        shareFile("Adhar1234");
                    } else {
                        LowCoinsDialog();
                    }

                }
            } else {
                Toast.makeText(ManualAadharPrint.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
            }


        });

        edit_btn.setOnClickListener(v -> {
            Intent i = new Intent(ManualAadharPrint.this, PresonalDetailsPage.class);
            if (str_voterPrint.equals("voterPrint")) {
                i.putExtra("voterPrint", "voterPrint");
            } else {
                i.putExtra("voterPrint", "adharPrint");
            }
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.INVISIBLE);
        gallary.setVisibility(View.VISIBLE);

        if (requestCode == 5 && resultCode == RESULT_OK && sh.contains("name")) {
            assert data != null;
            Uri uri = data.getData();
            ImageBitmap = uriToBitmap(uri);
            ImageNameTextView.setText(uri.getPath());


            if (str_voterPrint.equals("voterPrint")) {
                GenerateVoterCard();
            } else {
                GenerateAdhar();
            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();

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


    private void GenerateAdhar() {

        ManualPrintClass manualPrintClass = new ManualPrintClass(getApplicationContext());
        manualPrintClass.ManualAdharPrintFunction(getApplicationContext(), density * 9, ImageBitmap);
        VisibleAdjust("/Adhar1234");

    }

    private void GenerateVoterCard() {

        ManualVoterPrintClass manualVoterPrintClass = new ManualVoterPrintClass(getApplicationContext());
        manualVoterPrintClass.ManualVoterCardPrintFunction(getApplicationContext(), density * 9, ImageBitmap);
        VisibleAdjust("/voter1234");

    }

    private void VisibleAdjust(String filename) {
        File file22 = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename + ".pdf");
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
                mrewardedInterstitialAd.show(ManualAadharPrint.this, ManualAadharPrint.this);
                mrewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                });


            }
        });
        buycoin.setOnClickListener(v -> {
            Intent intent = new Intent(ManualAadharPrint.this, BuyCoins.class);
            startActivity(intent);
        });


    }

    private void shareFile(String s) {
        createPhoto.onsSharePdf(ManualAadharPrint.this, s);
    }

    private void initWork() {
        Submit = findViewById(R.id.Submit);
        pdfView = findViewById(R.id.pdfView);
        share = findViewById(R.id.share);
        edit_btn = findViewById(R.id.edit_btn);
        gallary = findViewById(R.id.Gallary);
        ImageNameTextView = findViewById(R.id.ImageNameTextView);
        progressBar = findViewById(R.id.progressBar);
        share.setVisibility(View.GONE);
        edit_btn.setVisibility(View.GONE);
    }




    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        dialog.dismiss();
        if (str_voterPrint.equals("voterPrint")) {
            shareFile("voter1234");
        } else {
            shareFile("Adhar1234");
        }

        afterOnePrint = true;
        mrewardedInterstitialAd = null;
    }
}