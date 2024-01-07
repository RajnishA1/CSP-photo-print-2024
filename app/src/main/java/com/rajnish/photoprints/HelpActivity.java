package com.rajnish.photoprints;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.rajnish.photoprints.R;

public class HelpActivity extends AppCompatActivity {


    TextView tv_passportPhoto;
    TextView termCondition;
    TextView tv_aadhaar;
    TextView tv_voter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        tv_passportPhoto = findViewById(R.id.tv_passportPhoto);
        tv_aadhaar = findViewById(R.id.tv_aadhaar);
        tv_voter = findViewById(R.id.tv_voter);
        termCondition = findViewById(R.id.termCondition);
        tv_passportPhoto.setMovementMethod(LinkMovementMethod.getInstance());
        termCondition.setMovementMethod(LinkMovementMethod.getInstance());
        tv_voter.setMovementMethod(LinkMovementMethod.getInstance());
        tv_aadhaar.setMovementMethod(LinkMovementMethod.getInstance());

    }
}