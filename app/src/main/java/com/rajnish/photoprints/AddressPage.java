package com.rajnish.photoprints;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;


public class AddressPage extends AppCompatActivity {

    EditText  houseNo, GaliLocality, postOffice, state,issueDate,downloadDate ;
    TextInputLayout stateEd,postOfficeEd,issueDateEd,downloadDateEd;
    Button Submit, Previous;
    String str_voterPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_page);

        initWork();

        Intent detectActivity = getIntent();
         str_voterPrint = detectActivity.getStringExtra("voterPrint");
        if (str_voterPrint.equals("voterPrint")) {

            issueDateEd.setVisibility(View.GONE);
            downloadDateEd.setVisibility(View.GONE);
        }


        Submit.setOnClickListener(v -> {

            if (getDetails()) {
                Intent intent = new Intent(AddressPage.this, AddressInLocalLanguage.class);
                if(str_voterPrint.equals("voterPrint")) {
                    intent.putExtra("voterPrint", "voterPrint");
                }
                else {
                    intent.putExtra("voterPrint", "adharPrint");
                }
                startActivity(intent);
            }
        });
        Previous.setOnClickListener(v -> {
            goTOParentActivity();
        });


    }

    private void goTOParentActivity(){
        Intent intent = new Intent(AddressPage.this, PresonalDetailsPage.class);
        if(str_voterPrint.equals("voterPrint")){

            intent.putExtra("voterPrint", "voterPrint");
        }
        else {
            intent.putExtra("voterPrint", "adharPrint");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private boolean getDetails() {
        if (isNotEmptyEditText()) {

            String Str_houseNo = houseNo.getText().toString().trim();
            String Str_GaliLocality = GaliLocality.getText().toString().trim();
            String Str_postOffice = postOffice.getText().toString().trim();
            String Str_state = state.getText().toString().trim();


            SharedPreferences sharedPreferences = getSharedPreferences("AInEDetailSharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            myEdit.putString("house", Str_houseNo);
            myEdit.putString("galiLocality", Str_GaliLocality);
            myEdit.putString("postOffice", Str_postOffice);
            myEdit.putString("state", Str_state);

            myEdit.apply();
            return true;

        }
        return false;
    }
    private void beforeNextClick(){

        String Str_houseNo = houseNo.getText().toString().trim();
        String Str_GaliLocality = GaliLocality.getText().toString().trim();
        String Str_postOffice = postOffice.getText().toString().trim();
        String Str_state = state.getText().toString().trim();
        String Str_issueDate = issueDate.getText().toString().trim();
        String Str_download = downloadDate.getText().toString().trim();


        SharedPreferences sharedPreferences = getSharedPreferences("AInEDetailSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putString("house", Str_houseNo);
        myEdit.putString("galiLocality", Str_GaliLocality);
        myEdit.putString("postOffice", Str_postOffice);
        myEdit.putString("state", Str_state);
        myEdit.putString("issueDate", Str_issueDate);
        myEdit.putString("downloadDate", Str_download);
        myEdit.apply();
    }

    private void initWork() {

        houseNo = findViewById(R.id.houseNo);
        GaliLocality = findViewById(R.id.GaliLocality);
        postOffice = findViewById(R.id.postOffice);
        postOfficeEd = findViewById(R.id.postOfficeEd);
        state = findViewById(R.id.state);
        stateEd = findViewById(R.id.stateEd);
        Submit = findViewById(R.id.Submit);
        Previous = findViewById(R.id.Previous);
        issueDate = findViewById(R.id.issueDate);
        issueDateEd = findViewById(R.id.issueDateEd);
        downloadDate = findViewById(R.id.downloadDate);
        downloadDateEd = findViewById(R.id.downloadDateEd);


    }

    private boolean isNotEmptyEditText() {

        if (TextUtils.isEmpty(houseNo.getText().toString())) {
            houseNo.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(GaliLocality.getText().toString())) {
            GaliLocality.setError("Required");
            return false;
        }
        if (postOffice.getVisibility()==View.VISIBLE&&TextUtils.isEmpty(postOffice.getText().toString())) {
            postOffice.setError("Required");
            return false;
        }

        if (state.getVisibility()==View.VISIBLE&&TextUtils.isEmpty(state.getText().toString())) {
            state.setError("Required");
            return false;
        }
        if(issueDate.getVisibility()==View.VISIBLE&& !TextUtils.isEmpty(issueDate.getText().toString()) && issueDate.getText().length()<10){
            issueDate.setError("Invalid");
            return false;
        }
        if(downloadDate.getVisibility()==View.VISIBLE&& !TextUtils.isEmpty(downloadDate.getText().toString()) && downloadDate.getText().length()<10){
            downloadDate.setError("Invalid");
            return false;
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

        SharedPreferences shAInEDetailSharedPref = getApplicationContext().getSharedPreferences("AInEDetailSharedPref", Context.MODE_PRIVATE);

        String Str_houseNo = shAInEDetailSharedPref.getString("house", "");
        String Str_GaliLocality = shAInEDetailSharedPref.getString("galiLocality", "");
        String Str_postOffice = shAInEDetailSharedPref.getString("postOffice", "");
        String Str_state = shAInEDetailSharedPref.getString("state", "");

        if (shAInEDetailSharedPref.contains("house")) {

            houseNo.setText(Str_houseNo);
            GaliLocality.setText(Str_GaliLocality);
            postOffice.setText(Str_postOffice);
            state.setText(Str_state);

        }


    }
}