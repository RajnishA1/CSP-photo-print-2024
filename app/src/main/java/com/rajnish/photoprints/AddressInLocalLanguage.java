package com.rajnish.photoprints;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.material.textfield.TextInputLayout;
import com.rajnish.photoprints.R;
import com.rajnish.photoprints.ads.Admob;


public class AddressInLocalLanguage extends AppCompatActivity {


    EditText HiName, HiFatherName, HiHouseNo, HiGaliLocality, HiPoliceStation, HiDistrict;
    TextInputLayout HiGaliLocalityEd, HiFatherNameEd;
    Button Submit, Previous;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_in_local_language);

        initWork();

        Intent detectActivity = getIntent();
        String str_voterPrint = detectActivity.getStringExtra("voterPrint");
        if (str_voterPrint.equals("voterPrint")) {
            HiGaliLocalityEd.setHint("गली स्थान / ग्राम + थाना");
            HiFatherNameEd.setHint("पिता / पति का नाम");

        }


        Submit.setOnClickListener(v -> {
            if (getDetails()) {
                if (Admob.mInterstitialAd != null) {
                    Admob.mInterstitialAd.show(AddressInLocalLanguage.this);
                    Admob.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            Admob.mInterstitialAd = null;
                            Admob.loadInter(AddressInLocalLanguage.this);
                            goNext(str_voterPrint);
                        }
                    });


                } else {
                    goNext(str_voterPrint);
                }


            }

        });

        Previous.setOnClickListener(v -> {
            Intent intent = new Intent(AddressInLocalLanguage.this, AddressPage.class);
            if (str_voterPrint.equals("voterPrint")) {
                intent.putExtra("voterPrint", "voterPrint");
            } else {
                intent.putExtra("voterPrint", "adharPrint");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


    }

    private void goNext(String str_voterPrint) {
        if (str_voterPrint.equals("voterPrint")) {
            Intent intent = new Intent(AddressInLocalLanguage.this, VoterDetailsActivity.class);
            intent.putExtra("voterPrint", str_voterPrint);
            startActivity(intent);
        } else {
            Intent intent1 = new Intent(AddressInLocalLanguage.this, ManualAadharPrint.class);
            intent1.putExtra("voterPrint", "adharPrint");
            startActivity(intent1);
        }
    }

    private void initWork() {

        Submit = findViewById(R.id.Submit);
        HiName = findViewById(R.id.HiName);
        HiFatherName = findViewById(R.id.HiFatherName);
        HiFatherNameEd = findViewById(R.id.HiFatherNameEd);
        HiHouseNo = findViewById(R.id.HiHouseNumber);
        HiGaliLocalityEd = findViewById(R.id.HiGaliLocalityEd);
        HiGaliLocality = findViewById(R.id.HiGaliLocality);
        HiPoliceStation = findViewById(R.id.HiPoliceStation);
        HiDistrict = findViewById(R.id.Hi_District);
        Previous = findViewById(R.id.Previous);


    }

    private boolean getDetails() {
        if (isNotEmptyEditText()) {

            String Str_HiName = HiName.getText().toString().trim();
            String Str_HiFatherName = HiFatherName.getText().toString().trim();
            String Str_HiHouseNo = HiHouseNo.getText().toString().trim();
            String Str_HiGaliLocality = HiGaliLocality.getText().toString().trim();
            String Str_HiPoliceStation = HiPoliceStation.getText().toString().trim();
            String Str_HiDistrict = HiDistrict.getText().toString().trim();


            SharedPreferences sharedPreferences = getSharedPreferences("AInHDetailSharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("HiName", Str_HiName);
            myEdit.putString("HiFatherName", Str_HiFatherName);
            myEdit.putString("HiHouseNo", Str_HiHouseNo);
            myEdit.putString("HiGaliLocality", Str_HiGaliLocality);
            myEdit.putString("HiPoliceStation", Str_HiPoliceStation);
            myEdit.putString("HiDistrict", Str_HiDistrict);
            myEdit.apply();


            return true;
        }
        return false;
    }

    private void beforeNextClick() {
        String Str_HiName = HiName.getText().toString().trim();
        String Str_HiFatherName = HiFatherName.getText().toString().trim();
        String Str_HiHouseNo = HiHouseNo.getText().toString().trim();
        String Str_HiGaliLocality = HiGaliLocality.getText().toString().trim();
        String Str_HiPoliceStation = HiPoliceStation.getText().toString().trim();
        String Str_HiDistrict = HiDistrict.getText().toString().trim();


        SharedPreferences sharedPreferences = getSharedPreferences("AInHDetailSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("HiName", Str_HiName);
        myEdit.putString("HiFatherName", Str_HiFatherName);
        myEdit.putString("HiHouseNo", Str_HiHouseNo);
        myEdit.putString("HiGaliLocality", Str_HiGaliLocality);
        myEdit.putString("HiPoliceStation", Str_HiPoliceStation);
        myEdit.putString("HiDistrict", Str_HiDistrict);

        myEdit.apply();
    }

    private boolean isNotEmptyEditText() {
        if (TextUtils.isEmpty(HiName.getText().toString())) {
            HiName.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(HiFatherName.getText().toString())) {
            HiFatherName.setError("Required");
            return false;
        }

        if (TextUtils.isEmpty(HiHouseNo.getText().toString())) {
            HiHouseNo.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(HiGaliLocality.getText().toString())) {
            HiGaliLocality.setError("Required");
            return false;
        }
        if (HiPoliceStation.getVisibility() == View.VISIBLE && TextUtils.isEmpty(HiPoliceStation.getText().toString())) {
            HiPoliceStation.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(HiDistrict.getText().toString())) {
            HiDistrict.setError("Required");
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        beforeNextClick();

    }

    @Override
    protected void onPause() {
        super.onPause();
        beforeNextClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        beforeNextClick();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences shAInHDetailSharedPref = getApplicationContext().getSharedPreferences("AInHDetailSharedPref", Context.MODE_PRIVATE);
        String Str_HiName = shAInHDetailSharedPref.getString("HiName", "");
        String Str_HiFatherName = shAInHDetailSharedPref.getString("HiFatherName", "");
        String Str_HiHouseNo = shAInHDetailSharedPref.getString("HiHouseNo", "");
        String Str_HiGaliLocality = shAInHDetailSharedPref.getString("HiGaliLocality", "");
        String Str_HiTahshil = shAInHDetailSharedPref.getString("HiPoliceStation", "");
        String Str_HiDistrict = shAInHDetailSharedPref.getString("HiDistrict", "");

        if (shAInHDetailSharedPref.contains("HiName")) {
            HiName.setText(Str_HiName);
            HiFatherName.setText(Str_HiFatherName);
            HiHouseNo.setText(Str_HiHouseNo);
            HiGaliLocality.setText(Str_HiGaliLocality);
            HiPoliceStation.setText(Str_HiTahshil);
            HiDistrict.setText(Str_HiDistrict);
        }


    }
}