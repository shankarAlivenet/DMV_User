package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import constant.MyApplication;


public class SplitAddFrnd extends AppCompatActivity {
	
	Button btnDisplay,btncancel;
	ImageButton btnAdd,back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.split_add_frnd);



		Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		btnDisplay = (Button) findViewById(R.id.btnDisplay);
		btncancel = (Button) findViewById(R.id.btncancel);
		MyLayoutOperation.bydefaultFirstItemadd(this);
		MyLayoutOperation.add(this, btnAdd);
		MyLayoutOperation.sendinvitaions(this, btnDisplay);

		btncancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SplitAddFrnd.this.finish();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home:


				SplitAddFrnd.this.finish();
				return false;
			default:
				return super.onOptionsItemSelected(item);
		}


	}

}
