package com.rajnish.photoprints;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;



public class VoterDetailsActivity extends AppCompatActivity {

    EditText partNumber, partName, Assembly, Hi_PartName, Hi_Assembly;
    String selectedRelation;
    RadioButton fatherRadioBtn, husbandRadioBtn;
    RadioGroup relationGroup;
    Button Submit, Previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_details);
        initWork();


        Submit.setOnClickListener(v -> {
            if (getDetails()) {
                Intent i = new Intent(VoterDetailsActivity.this, ManualAadharPrint.class);
                i.putExtra("voterPrint", "voterPrint");
                startActivity(i);
            }
        });
        Previous.setOnClickListener(v -> {
            Intent i = new Intent(VoterDetailsActivity.this, AddressInLocalLanguage.class);
            i.putExtra("voterPrint", "voterPrint");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });


        relationGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            int selectedId = relationGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            selectedRelation = selectedRadioButton.getText().toString();


        });


    }

    private boolean getDetails() {
        if (isNotEmptyEditText() && selectedRelation.length() > 0) {
            String Str_partName = partName.getText().toString().trim();
            String Str_partNumber = partNumber.getText().toString().trim();
            String Str_Assembly = Assembly.getText().toString().trim();
            String Str_Hi_PartName = Hi_PartName.getText().toString().trim();
            String Str_Hi_Assembly = Hi_Assembly.getText().toString().trim();


            SharedPreferences sharedPreferences = getSharedPreferences("ShVoterDetailsActivity", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("partName", Str_partName);
            myEdit.putString("partNumber", Str_partNumber);
            myEdit.putString("Assembly", Str_Assembly);
            myEdit.putString("Hi_PartName", Str_Hi_PartName);
            myEdit.putString("Hi_Assembly", Str_Hi_Assembly);
            myEdit.putString("selectedRelation", selectedRelation);
            myEdit.apply();

            return true;
        } else {
            return false;
        }
    }

    private void beforeNextClick(){
        String Str_partName = partName.getText().toString().trim();
        String Str_partNumber = partNumber.getText().toString().trim();
        String Str_Assembly = Assembly.getText().toString().trim();
        String Str_Hi_PartName = Hi_PartName.getText().toString().trim();
        String Str_Hi_Assembly = Hi_Assembly.getText().toString().trim();


        SharedPreferences sharedPreferences = getSharedPreferences("ShVoterDetailsActivity", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("partName", Str_partName);
        myEdit.putString("partNumber", Str_partNumber);
        myEdit.putString("Assembly", Str_Assembly);
        myEdit.putString("Hi_PartName", Str_Hi_PartName);
        myEdit.putString("Hi_Assembly", Str_Hi_Assembly);
        myEdit.putString("selectedRelation", selectedRelation);
        myEdit.apply();
    }

    private boolean isNotEmptyEditText() {

        if (TextUtils.isEmpty(partNumber.getText().toString())) {
            partNumber.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(partName.getText().toString())) {
            partName.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(Assembly.getText().toString())) {
            Assembly.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(Hi_PartName.getText().toString())) {
            Hi_PartName.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(Hi_Assembly.getText().toString())) {
            Hi_Assembly.setError("Required");
            return false;
        }
        if(relationGroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(this, "Select Name Printed On Card", Toast.LENGTH_SHORT).show();
            return  false;
        }
        else {
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


        SharedPreferences shVoterDetailsActivity = getApplicationContext().getSharedPreferences("ShVoterDetailsActivity", MODE_PRIVATE);
        String Str_partName = shVoterDetailsActivity.getString("partName", "");
        String Str_partNumber = shVoterDetailsActivity.getString("partNumber", "");
        String Str_Assembly = shVoterDetailsActivity.getString("Assembly", "");
        String Str_Hi_PartName = shVoterDetailsActivity.getString("Hi_PartName", "");
        String Str_Hi_Assembly = shVoterDetailsActivity.getString("Hi_Assembly", "");
        String selectedRelation = shVoterDetailsActivity.getString("selectedRelation", "");


        if (shVoterDetailsActivity.contains("partName")) {
            partName.setText(Str_partName);
            partNumber.setText(Str_partNumber);
            Assembly.setText(Str_Assembly);
            Hi_PartName.setText(Str_Hi_PartName);
            Hi_Assembly.setText(Str_Hi_Assembly);
            if (selectedRelation.equals("Father")) {
                relationGroup.check(R.id.fatherRadioBtn);
            } else {
                relationGroup.check(R.id.husbandRadioBtn);
            }
            relationGroup.setSaveEnabled(true);
        }


    }

    private void initWork() {
        partNumber = findViewById(R.id.partNumber);
        partName = findViewById(R.id.partName);
        Assembly = findViewById(R.id.Assembly);
        Hi_PartName = findViewById(R.id.Hi_PartName);
        Hi_Assembly = findViewById(R.id.Hi_Assembly);
        fatherRadioBtn = findViewById(R.id.fatherRadioBtn);
        husbandRadioBtn = findViewById(R.id.husbandRadioBtn);
        relationGroup = findViewById(R.id.relationGroup);
        Previous = findViewById(R.id.Previous);
        Submit = findViewById(R.id.Submit);

    }

}