package com.alivenet.dmvtaxi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

/**
 * Created by narendra on 8/8/2016.
 */
public class UpdatePaymentActivity extends AppCompatActivity {


    Toolbar toolbar;
    EditText etcardNumber,ercardCvvNumber;
    String cardNumber ,cvvNumber,expirationDate;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_payment);

        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back_icon));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etcardNumber = (EditText)findViewById(R.id.et_cardnumber);
        ercardCvvNumber = (EditText)findViewById(R.id.et_cardcvvnumber);

        cardNumber = getIntent().getStringExtra("cardNo");
        cvvNumber = getIntent().getStringExtra("cvvNo");
        expirationDate = getIntent().getStringExtra("expirationDate");

        etcardNumber.setText(cardNumber);
        ercardCvvNumber.setText(cvvNumber);






    }
}
