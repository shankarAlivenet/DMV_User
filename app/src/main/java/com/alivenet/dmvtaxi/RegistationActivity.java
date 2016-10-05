package com.alivenet.dmvtaxi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.CreditcardinfoDialog;
import com.alivenet.dmvtaxi.dialog.NotVerifiedUserDialog;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import constant.MyApplication;
import constant.MyPreferences;
import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 6/16/2016.
 */
public class RegistationActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btnNext, btnJoinVip;
    ProgressDialog prgDialog;

    EditText metfirstName, metlastName, metemailAddress, metpassword, metcallNumber, metcreaditCardInfo;
    CheckBox chnewLetters;
    String newLater, roleId, cardNo, cardExpMont, cardExpYear, cvvNo;
    String cardInfo = "false";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Spinner spinnerCountryCode;
    int countryCode;
    int keyDel;
    String a;
    String mcountrycode;
    String[] countryCodeArray = {"+1", "+1242", "+1246", "+1264", "+1268", "+1284", "+1340",
            "+1345", "+1441", "+1473", "+1649", "+1664", "+1670", "+1671", "+1684", "+1758", "+1767",
            "+1784", "+1787",  "+1808", "+1809", "+1868", "+1869", "+1869", "+1876", "+20", "+212",
            "+213", "+216", "+218", "+220", "+221", "+222", "+223", "+224", "+225", "+226", "+227", "+228",
            "+229", "+230", "+231", "+232", "+233", "+234", "+235","+236", "+237", "+238", "+239",
            "+240", "+241", "+242", "+242", "+243", "+243", "+244", "+245", "+246", "+246", "+247", "+248",
            "+249", "+250", "+251", "+252", "+253", "+254",  "+255", "+256", "+257", "+258", "+260",
            "+261", "+262",  "+263", "+264", "+265", "+266", "+267", "+268",
            "+269", "+27", "+290", "+291", "+297", "+298", "+299", "+30", "+31", "+32",
            "+33", "+34", "+350", "+351", "+352", "+353", "+354", "+355", "+356",
            "+357", "+358", "+359", "+36", "+370", "+371", "+372", "+373", "+374", "+375",
            "+376", "+377", "+378", "+380", "+381", "+382", "+385", "+386", "+387", "+389", "+39",
            "+39066", "+40", "+41", "+420", "+421", "+423", "+43", "+44",
            "+45", "+46", "+47", "+48", "+49", "+500", "+501", "+502", "+503", "+504", "+505",
            "+506", "+507", "+508", "+509", "+51", "+52", "+53", "+5399", "+5399", "+54", "+55", "+56", "+56", "+57",
            "+58", "+590", "+591", "+592", "+593", "+594", "+595","+596","+597", "+598", "+599",
            "+60", "+61", "+62", "+63", "+64", "+65", "+66", "+670", "+672", "+673",
            "+674", "+675", "+676", "+677", "+678", "+679", "+680", "+681", "+682", "+683", "+685", "+686", "+687", "+688",
            "+689", "+690", "+691", "+692", "+7", "+76", "+7840", "+800", "+808", "+81", "+82", "+84",
            "+850", "+852", "+853", "+855", "+856", "+857", "+86",
            "+870", "+878", "+880", "+881", "+8810", "+8812", "+8816", "+8818", "+88213", "+88216", "+886", "+90",
            "+91", "+92", "+93", "+94", "+95", "+960", "+961", "+962", "+963", "+964", "+965",
            "+966", "+967", "+968", "+970", "+971", "+972", "+973", "+974", "+975", "+976",
            "+977", "+98", "+992", "+993", "+994", "+995", "+996", "+998"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_personal_info);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
        ;




            toolbar = (Toolbar) findViewById(R.id.maintoolbar);
            toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back_icon));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            Intent intent = getIntent();
            roleId = intent.getStringExtra("role_Id");

            metfirstName = (EditText) findViewById(R.id.et_regfirstname);
            metlastName = (EditText) findViewById(R.id.et_reglastname);
            metemailAddress = (EditText) findViewById(R.id.et_regemail);
            metpassword = (EditText) findViewById(R.id.et_regpassword);
            metcallNumber = (EditText) findViewById(R.id.et_regcellnumber);
            metcreaditCardInfo = (EditText) findViewById(R.id.et_regcreditinfo);
            chnewLetters = (CheckBox) findViewById(R.id.ch_regnewlater_registation);
            spinnerCountryCode = (Spinner) findViewById(R.id.strip_countrycode);
            ArrayAdapter monthAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, countryCodeArray);
            monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCountryCode.setAdapter(monthAdapter);


            btnJoinVip = (Button) findViewById(R.id.btn_joinvip);
            btnNext = (Button) findViewById(R.id.btn_next);



        spinnerCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mcountrycode = (String) parent.getItemAtPosition(position);
                //System.out.println("mcountrycode>>>>>>>>>>>>>>>>" + mcountrycode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkValidationJoinUs()) {

                        String EmailId = metemailAddress.getText().toString();

                        checkEmailWS(EmailId);

                    }
                }
            });

            if (MyApplication.vipaccount == true) {
                btnJoinVip.setVisibility(View.GONE);
            }

            btnJoinVip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkValidationJoinUs()) {

                        String EmailId = metemailAddress.getText().toString();
                        RequestParams params = new RequestParams();
                        params.put("email", EmailId);
                        checkEmailIdWS(params);

                    }
                }
            });


            metcreaditCardInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent in = new Intent(RegistationActivity.this, CardInfoActivity.class);
                    startActivityForResult(in, 1);

                }
            });


            ////////////Phone number formate start///////////////


            metcallNumber.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    boolean flag = true;
                    String eachBlock[] = metcallNumber.getText().toString().split("-");
                    for (int i = 0; i < eachBlock.length; i++) {
                        if (eachBlock[i].length() > 12) {
                            flag = false;
                        }
                    }
                    if (flag) {

                        metcallNumber.setOnKeyListener(new View.OnKeyListener() {

                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {

                                if (keyCode == KeyEvent.KEYCODE_DEL)
                                    keyDel = 1;
                                return false;
                            }
                        });

                        if (keyDel == 0) {

                            if (((metcallNumber.getText().length() + 1) % 4) == 0) {

                                if (metcallNumber.getText().toString().split("-").length <= 2) {
                                    metcallNumber.setText(metcallNumber.getText() + "-");
                                    metcallNumber.setSelection(metcallNumber.getText().length());
                                }
                            }
                            a = metcallNumber.getText().toString();
                        } else {
                            a = metcallNumber.getText().toString();
                            keyDel = 0;
                        }

                    } else {
                        metcallNumber.setText(a);
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
// TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("Code", "requestCode--" + requestCode + ",resultCode--" + resultCode);

        if (requestCode == 1) {
            if (resultCode == 1) {

                cardNo = data.getStringExtra("cardNo");
                cardExpMont = data.getStringExtra("cardexpmont");
                cardExpYear = data.getStringExtra("cardexpyear");
                cardInfo = data.getStringExtra("cardinfo");
                cvvNo = data.getStringExtra("cvvNo");
                Log.e("cardInfo", " cardNo " + cardNo + " cvvNo " + cvvNo + " , ExpMonth " + cardExpMont + " , ExpYear" + cardExpYear);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    public void checkEmailWS(String EmailId) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email",EmailId);
        client.post(WebserviceUrlClass.CheckEmailId, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                prgDialog.show();

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                prgDialog.hide();
                try {
                    Log.d("Json_con", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        if (responseMessage.equals("success")) {
                            if (roleId.equals("3") && cardInfo.equals("true")) {
                                Intent in = new Intent(getApplication(), VerificationActivity.class);
                                in.putExtra("regfirstname", metfirstName.getText().toString().trim());
                                in.putExtra("reglastname", metlastName.getText().toString().trim());
                                in.putExtra("regemailaddress", metemailAddress.getText().toString().trim());
                                in.putExtra("regpassword", metpassword.getText().toString().trim());
                                in.putExtra("regmobileno", metcallNumber.getText().toString().trim());
                                in.putExtra("cardNo", cardNo);
                                in.putExtra("cardExpdate", cardExpMont + "/" + cardExpYear);
                                in.putExtra("cvvNo", cvvNo);
                                in.putExtra("roleId", roleId);
                                in.putExtra("countryCode",mcountrycode);
                                if (chnewLetters.isChecked()) {
                                    in.putExtra("regnewsLater", "yes");
                                } else {
                                    in.putExtra("regnewsLater", "no");
                                }
                                startActivity(in);
                            }
                            if (roleId.equals("3") && cardInfo.equals("false")) {
                                CommanMethod.showAlert("Please enter card Info", RegistationActivity.this);
                            }
                            if (roleId.equals("2") && cardInfo.equals("true")) {
                                Intent in = new Intent(getApplication(), VerificationActivity.class);
                                in.putExtra("regfirstname", metfirstName.getText().toString().trim());
                                in.putExtra("reglastname", metlastName.getText().toString().trim());
                                in.putExtra("regemailaddress", metemailAddress.getText().toString().trim());
                                in.putExtra("regpassword", metpassword.getText().toString().trim());
                                in.putExtra("regmobileno", metcallNumber.getText().toString().trim());
                                in.putExtra("cardNo", cardNo);
                                in.putExtra("cardExpdate", cardExpMont + "/" + cardExpYear);
                                in.putExtra("cvvNo", cvvNo);
                                in.putExtra("roleId", roleId);
                                in.putExtra("countryCode",spinnerCountryCode.getSelectedItem().toString().trim());
                                if (chnewLetters.isChecked()) {
                                    in.putExtra("regnewsLater", "yes");
                                } else {
                                    in.putExtra("regnewsLater", "no");
                                }
                                startActivity(in);
                            }
                            if (roleId.equals("2") && cardInfo.equals("false")) {
                                Intent in = new Intent(getApplication(), VerificationActivity.class);
                                in.putExtra("regfirstname", metfirstName.getText().toString().trim());
                                in.putExtra("reglastname", metlastName.getText().toString().trim());
                                in.putExtra("regemailaddress", metemailAddress.getText().toString().trim());
                                in.putExtra("regpassword", metpassword.getText().toString().trim());
                                in.putExtra("regmobileno", metcallNumber.getText().toString().trim());
                                in.putExtra("cardNo", "");
                                in.putExtra("cardExpdate", "");
                                in.putExtra("cvvNo", "");
                                in.putExtra("roleId", roleId);
                                in.putExtra("countryCode",spinnerCountryCode.getSelectedItem().toString().trim());
                                if (chnewLetters.isChecked()) {
                                    in.putExtra("regnewsLater", "yes");
                                } else {
                                    in.putExtra("regnewsLater", "no");
                                }
                                startActivity(in);
                            }
                        }
                        else if (responseMessage.equals("email already exist")) {

                            Toast.makeText(getApplicationContext(),responseMessage,Toast.LENGTH_LONG).show();

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                prgDialog.dismiss();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),RegistationActivity.this);
            }
            @Override
            public void onFinish() {

                prgDialog.dismiss();
                super.onFinish();
            }

        });
    }

    public void checkEmailIdWS(RequestParams params) {

        String url = WebserviceUrlClass.CheckEmailId;
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                prgDialog.show();

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                prgDialog.hide();
                try {
                    Log.d("Json_con", response.toString());
                    String responseCode = response.getString("responseCode");
                    String responseMessage = response.getString("responseMsg");

                    if (responseCode.equals("200")) {

                        if (responseMessage.equals("success")) {
                            if (roleId.equals("2") && cardInfo.equals("true")) {
                                Intent in = new Intent(getApplication(), VerificationActivity.class);
                                in.putExtra("regfirstname", metfirstName.getText().toString().trim());
                                in.putExtra("reglastname", metlastName.getText().toString().trim());
                                in.putExtra("regemailaddress", metemailAddress.getText().toString().trim());
                                in.putExtra("regpassword", metpassword.getText().toString().trim());

                                String result = metcallNumber.getText().toString().replaceAll("[-+.^:,]","");
                                result.replaceAll("\\s+"," ");
                                result.trim();
                                in.putExtra("regmobileno", result);

                                in.putExtra("cardNo", cardNo);
                                in.putExtra("cvvNo", cvvNo);
                                in.putExtra("cardExpdate", cardExpMont + "/" + cardExpYear);
                                in.putExtra("roleId", "3");
                                in.putExtra("countryCode",spinnerCountryCode.getSelectedItem().toString().trim());
                                if (chnewLetters.isChecked()) {
                                    in.putExtra("regnewsLater", "yes");
                                } else {
                                    in.putExtra("regnewsLater", "no");
                                }
                                startActivity(in);
                            }
                            if (roleId.equals("2") && cardInfo.equals("false")) {
                                CommanMethod.showAlert("Please enter card Info", RegistationActivity.this);
                            }

                        }
                        else if (responseMessage.equals("email already exist")) {

                            Toast.makeText(getApplicationContext(),responseMessage,Toast.LENGTH_LONG).show();

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                prgDialog.dismiss();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),RegistationActivity.this);
            }
            @Override
            public void onFinish() {

                prgDialog.dismiss();
                super.onFinish();
            }
        });
    }




    private boolean checkValidationJoinUs() {

        if (metfirstName.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your first Name.", RegistationActivity.this);
            return false;
        } else if (metlastName.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter yoour last Name.", RegistationActivity.this);
            return false;
        } else if (metemailAddress.getText().toString().trim().length() == 0&&metemailAddress.getText().toString().trim().matches(emailPattern) ) {
            CommanMethod.showAlert("Please enter your Email.", RegistationActivity.this);
            return false;
        } else if (!CommanMethod.isEmailValid(metemailAddress.getText().toString().trim())) {
            CommanMethod.showAlert("Please enter valid Email.", RegistationActivity.this);
            return false;
        } else if (metpassword.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your Password.", RegistationActivity.this);
            return false;
        } else if(metpassword.getText().toString().trim().length()<6) {
            CommanMethod.showAlert("Minimum 6 character required for password", RegistationActivity.this);
            return false;

        }else  if (metcallNumber.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your cell Number.", RegistationActivity.this);
            return false;
        } else if (metcallNumber.getText().toString().trim().length() != 12) {
            CommanMethod.showAlert("Please enter valid cell Number.", RegistationActivity.this);
            return false;
        }
        return true;

    }



}
