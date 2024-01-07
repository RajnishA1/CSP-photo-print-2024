package com.rajnish.photoprints;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.rajnish.photoprints.ads.Admob;
import com.rajnish.photoprints.databinding.ActivityPresonalDetailsPageBinding;

import java.util.Objects;

public class PresonalDetailsPage extends AppCompatActivity {


    String selectedGender;

    ActivityPresonalDetailsPageBinding binding;
    int maxLength = 12;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresonalDetailsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent detectActivity = getIntent();
        String str_voterPrint = detectActivity.getStringExtra("voterPrint");
        assert str_voterPrint != null;
        if (str_voterPrint.equals("voterPrint")) {
            binding.adharNumberEd.setHint("Epic No");
            binding.adharNumberEd.setCounterMaxLength(10);
            binding.adharNumber.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(10)

            });
            binding.adharNumber.setInputType(InputType.TYPE_CLASS_TEXT);
            binding.fatherNameEd.setHint("Father's Name");

        } else if (str_voterPrint.equals("panPrint")) {
            binding.adharNumberEd.setHint("Pan No");
            binding.adharNumberEd.setCounterMaxLength(10);
            binding.adharNumber.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(10)

            });
            binding.adharNumber.setInputType(InputType.TYPE_CLASS_TEXT);
            binding.fatherNameEd.setHint("Father's Name");
            binding.tvGenderLabel.setVisibility(View.GONE);
            binding.genderGroup.setVisibility(View.GONE);
            binding.tvWebsiteLint.setVisibility(View.GONE);

        } else {
            binding.tvWebsiteLint.setVisibility(View.GONE);
        }


        binding.genderGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            int selectedId = binding.genderGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            selectedGender = selectedRadioButton.getText().toString();


        });


        binding.tvWebsiteLint.setMovementMethod(LinkMovementMethod.getInstance());

        binding.Next.setOnClickListener(v -> {

            if (Admob.mInterstitialAd != null) {

                Admob.mInterstitialAd.show(PresonalDetailsPage.this);
                Admob.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Admob.mInterstitialAd = null;
                        Admob.loadInter(PresonalDetailsPage.this);
                        goNext(str_voterPrint);
                    }
                });

            } else {
                goNext(str_voterPrint);
            }


        });

    }

    private void goNext(String str_voterPrint) {
        if (getDetails()) {
            if (str_voterPrint.equals("voterPrint")) {
                maxLength = 10;
            }
            if (str_voterPrint.equals("panPrint")) {
                maxLength = 10;
            }
            if (Objects.requireNonNull(binding.adharNumber.getText()).length() < maxLength) {
                binding.adharNumber.setError("Invalid Adhar Number");
            } else {
                Intent intent = new Intent(PresonalDetailsPage.this, AddressPage.class);
                if (str_voterPrint.equals("voterPrint")) {
                    intent.putExtra("voterPrint", "voterPrint");
                }
                if (str_voterPrint.equals("panPrint")) {
                    intent = new Intent(PresonalDetailsPage.this, ManualPanCardActivity.class);
                }
                if (str_voterPrint.equals("adharPrint")) {
                    intent.putExtra("voterPrint", "adharPrint");
                }
                startActivity(intent);

            }
        }
    }

    private boolean getDetails() {
        if (isNotEmptyEditText()) {
            String Str_name = Objects.requireNonNull(binding.name.getText()).toString().trim();
            String Str_fatherName = Objects.requireNonNull(binding.fatherName.getText()).toString().trim();
            String Str_DateOfBirth = Objects.requireNonNull(binding.DateOfBirth.getText()).toString().trim();
            String Str_adharNumber = Objects.requireNonNull(binding.adharNumber.getText()).toString().trim();


            SharedPreferences sharedPreferences = getSharedPreferences("PDetailSharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("name", Str_name);
            myEdit.putString("father", Str_fatherName);
            myEdit.putString("DateOfBirth", Str_DateOfBirth);
            myEdit.putString("aadhar", Str_adharNumber);
            myEdit.putString("gender", selectedGender);
            myEdit.apply();

            return true;
        } else {
            return false;
        }
    }

    private void beforeNextClick() {
        String Str_name = Objects.requireNonNull(binding.name.getText()).toString().trim();
        String Str_fatherName = Objects.requireNonNull(binding.fatherName.getText()).toString().trim();
        String Str_DateOfBirth = Objects.requireNonNull(binding.DateOfBirth.getText()).toString().trim();
        String Str_adharNumber = Objects.requireNonNull(binding.adharNumber.getText()).toString().trim();


        SharedPreferences sharedPreferences = getSharedPreferences("PDetailSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("name", Str_name);
        myEdit.putString("father", Str_fatherName);
        myEdit.putString("DateOfBirth", Str_DateOfBirth);
        myEdit.putString("aadhar", Str_adharNumber);
        myEdit.putString("gender", selectedGender);
        myEdit.apply();
    }


    private boolean isNotEmptyEditText() {
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.name.getText()).toString())) {
            binding.name.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.fatherName.getText()).toString())) {
            binding.fatherName.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.DateOfBirth.getText()).toString())) {
            binding.DateOfBirth.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.adharNumber.getText()).toString())) {
            binding.adharNumber.setError("Required");
            return false;
        }
        if (binding.genderGroup.getVisibility() == View.VISIBLE && binding.genderGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show();
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
        SharedPreferences sh = getApplicationContext().getSharedPreferences("PDetailSharedPref", Context.MODE_PRIVATE);

        String Str_name = sh.getString("name", "");
        String Str_fatherName = sh.getString("father", "");
        String Str_DateOfBirth = sh.getString("DateOfBirth", "");
        String Str_adharNumber = sh.getString("aadhar", "");
        String selectedGender = sh.getString("gender", "");


        if (sh.contains("name")) {
            binding.name.setText(Str_name);
            binding.fatherName.setText(Str_fatherName);
            binding.DateOfBirth.setText(Str_DateOfBirth);
            binding.adharNumber.setText(Str_adharNumber);
            if (selectedGender.equals("Male")) {
                binding.genderGroup.check(R.id.maleRadioBtn);
            } else {
                binding.genderGroup.check(R.id.femaleRadioBtn);
            }

            binding.genderGroup.setSaveEnabled(true);


        }
    }


}