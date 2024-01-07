package com.rajnish.photoprints;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.rajnish.photoprints.ads.Admob;
import com.rajnish.photoprints.nowInkotlin.ImageResize;
import com.rajnish.photoprints.nowInkotlin.ImagesCompressor;
import com.rajnish.photoprints.nowInkotlin.JPGToPDF;
import com.rajnish.photoprints.nowInkotlin.utils.InternetUtils;

import java.util.LinkedList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements OnUserEarnedRewardListener {
    LinearLayout passportsizephoto, aadhaarcardprint, PassbookPrint, manualAadharPrint, manualVoterPrint, manualPanCardPrint,imageCompressor,ImageResizer,JPGToPDF;
    Button WatchAds, BuyPoint_btn;
    ProgressBar progress_circular;
    TextView tv_point;
    ImageView Hide_icon;
    ImageView help_icon_btn;
    RelativeLayout ButtonGroup;
    Dialog dialog;

    private static final int PERMISSIONS_REQUEST_CODE = 123;


    int point = 0;


    public static int UPDATECODE = 23;
    AppUpdateManager appUpdateManager;


    @Override
    protected void onResume() {
        super.onResume();
        inAppUp();
        tv_point.setText(String.valueOf(point));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        passportsizephoto = findViewById(R.id.passportsizephoto);
        aadhaarcardprint = findViewById(R.id.aadhaarcardprint);
        PassbookPrint = findViewById(R.id.bankpassbook);
        manualAadharPrint = findViewById(R.id.manualAadharPrint);
        manualVoterPrint = findViewById(R.id.manualVoterPrint);
        manualPanCardPrint = findViewById(R.id.manualPanCardPrint);
        imageCompressor = findViewById(R.id.imageCompressor);
        ImageResizer = findViewById(R.id.ImageResizer);
        JPGToPDF = findViewById(R.id.JPGToPDF);
        WatchAds = findViewById(R.id.WatchAds);
        BuyPoint_btn = findViewById(R.id.BuyPoint_btn);
        tv_point = findViewById(R.id.tv_point);
        Hide_icon = findViewById(R.id.Hide_icon);
        help_icon_btn = findViewById(R.id.help_icon_btn);
        ButtonGroup = findViewById(R.id.ButtonGroup);
        progress_circular = findViewById(R.id.progress_circular);
        dialog = new Dialog(this);

        SharedPreferences sharedPreferences = getSharedPreferences("Coins", 0);
        if (!sharedPreferences.getBoolean("isPurchasePoint",false)){
            Admob.loadInter(MainActivity.this);
        }


        SharedPreferences tr = getSharedPreferences("termsDialog", MODE_PRIVATE);
        boolean accepted = tr.getBoolean("accepted",false);
        if(!accepted){
            showTermsDialog();
        }

        Objects.requireNonNull(getSupportActionBar()).hide();


        //// ads section
        MobileAds.initialize(MainActivity.this);



        SharedPreferences sh = getSharedPreferences("Coins", MODE_PRIVATE);
        point = sh.getInt("Points", 5);
        tv_point.setText(String.valueOf(point));


        

        Hide_icon.setOnClickListener(view -> {
            if (ButtonGroup.getVisibility() == View.VISIBLE) {
                ButtonGroup.setVisibility(View.GONE);
                Hide_icon.setImageResource(R.drawable.ic_baseline_visibility_off_24);

            } else {
                ButtonGroup.setVisibility(View.VISIBLE);
                Hide_icon.setImageResource(R.drawable.ic_baseline_visibility_24);
            }
        });
        help_icon_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        BuyPoint_btn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BuyCoins.class);
            startActivity(intent);
        });

        WatchAds.setOnClickListener(view -> {
            loadAd();
            WatchAds.setVisibility(View.INVISIBLE);
            progress_circular.setVisibility(View.VISIBLE);
        });


        PassbookPrint.setOnClickListener(v -> moveToNextPage(new PassbookEdit()));


        passportsizephoto.setOnClickListener(v -> moveToNextPage(new PassportSizePhoto()));

        aadhaarcardprint.setOnClickListener(v -> moveToNextPage(new AdharCardPrint()));
        manualAadharPrint.setOnClickListener(v -> {
            if (InternetUtils.INSTANCE.isInternetConnected(this)) {

                Intent intent = new Intent(MainActivity.this, PresonalDetailsPage.class);
                intent.putExtra("voterPrint", "adharPrint");
                startActivity(intent);


            } else {
                Toast.makeText(MainActivity.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
            }
        });
        manualVoterPrint.setOnClickListener(v -> {
            if (InternetUtils.INSTANCE.isInternetConnected(this)) {

                Intent intent = new Intent(MainActivity.this, PresonalDetailsPage.class);
                intent.putExtra("voterPrint", "voterPrint");
                startActivity(intent);


            } else {
                Toast.makeText(MainActivity.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
            }
        });

        manualPanCardPrint.setOnClickListener(v -> {
            if (InternetUtils.INSTANCE.isInternetConnected(this)) {

                Intent intent = new Intent(MainActivity.this, PresonalDetailsPage.class);
                intent.putExtra("voterPrint", "panPrint");
                startActivity(intent);


            } else {
                Toast.makeText(MainActivity.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
            }
        });
        JPGToPDF.setOnClickListener(v -> moveToNextPage(new JPGToPDF()));
        ImageResizer.setOnClickListener(v -> moveToNextPage(new ImageResize()));
        imageCompressor.setOnClickListener(v -> moveToNextPage(new ImagesCompressor()));




    }


    private void moveToNextPage(Activity activity){
        if (InternetUtils.INSTANCE.isInternetConnected(this)) {
            Intent intent = new Intent(MainActivity.this, activity.getClass());
            startActivity(intent);


        } else {
            Toast.makeText(MainActivity.this, "No Internet Connected", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissionsAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            // Permissions are not granted, request them.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permissions denied, handle this case (e.g., show an explanation or disable the functionality).
                Toast.makeText(this, "Permissions required to access photos and videos.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showTermsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage("By using this app, you agree to the terms and conditions.");
        View view = getLayoutInflater().inflate(R.layout.dialog_terms, null);
        builder.setView(view);

        Button btnClose = view.findViewById(R.id.btn_close);
        CheckBox checkBox = view.findViewById(R.id.termsCheckBox);

        AlertDialog dialog = builder.create();
        dialog.show();
        btnClose.setOnClickListener(v -> {
            if(checkBox.isChecked()){
                SharedPreferences sh = getSharedPreferences("termsDialog", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sh.edit();
                myEdit.putBoolean("accepted",true);
                myEdit.apply();
                dialog.dismiss();
                checkPermissionsAndOpenGallery();
            }else {
                Toast.makeText(this, "Please Accept Terms And Condition ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inAppUp() {

        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> task = appUpdateManager.getAppUpdateInfo();
        task.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, MainActivity.this, UPDATECODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });
        appUpdateManager.registerListener(listener);

    }

    InstallStateUpdatedListener listener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp();
        }
    };

    private void popUp() {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content), "App update Almost Done",
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction("Reload", v -> appUpdateManager.completeUpdate());
        snackbar.setTextColor(Color.parseColor("#FF0000"));
        snackbar.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATECODE) {
            if (requestCode != RESULT_OK) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Downloading start", Toast.LENGTH_SHORT).show();
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
                        progress_circular.setVisibility(View.GONE);
                        WatchAds.setVisibility(View.VISIBLE);
                        ad.show(MainActivity.this, MainActivity.this);


                    }


                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        progress_circular.setVisibility(View.GONE);
                        WatchAds.setVisibility(View.VISIBLE);


                    }
                });
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

        point = point + 2;

        SharedPreferences sharedPreferences = getSharedPreferences("Coins", 0);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("Points", point);
        if (point <= 10){
            myEdit.putBoolean("isPurchasePoint",false);
        }
        myEdit.apply();


        tv_point.setText(String.valueOf(point));
        Toast.makeText(this, "Earned 2 Coins", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sh = getSharedPreferences("Coins", 0);
        point = sh.getInt("Points", 5);
        tv_point.setText(String.valueOf(point));


    }



}

