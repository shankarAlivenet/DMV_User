package com.alivenet.dmvtaxi.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alivenet.dmvtaxi.DeashboardActivity;
import com.alivenet.dmvtaxi.R;
import com.alivenet.dmvtaxi.UpdatePaymentActivity;
import com.alivenet.dmvtaxi.commonutil.CommanMethod;
import com.alivenet.dmvtaxi.commonutil.WebserviceUrlClass;
import com.alivenet.dmvtaxi.dialog.IncorrectPasswordDialog;
import com.alivenet.dmvtaxi.dialog.NotVerifiedUserDialog;
import com.alivenet.dmvtaxi.pojo.ReservationListModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by narendra on 8/7/2016.
 */
public class FragmentUpdateMyProfile extends Fragment {

    RecyclerView recyclerView;
    public static final String MYPREF = "user_info";
    SharedPreferences mPref;
    public String mUserId;
    ProgressDialog prgDialog;
    EditText etCellNumber, etFirstName, etLastName, etEmailId, spinnercountryCode;
    //  Spinner spinnercountryCode;
    Button btnSaveAccount;
    int keyDel;
    String a;


    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }
    public static FragmentUpdateMyProfile newInstance(String sectionTitle) {
        FragmentUpdateMyProfile fragment = new FragmentUpdateMyProfile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updatemyaccount, container, false);
        mPref = getActivity().getSharedPreferences(MYPREF, Context.MODE_PRIVATE);
        mUserId = mPref.getString("userId", null);
        Log.e("my user id ", mUserId);
        etFirstName = (EditText) view.findViewById(R.id.edt_firstname);
        etLastName = (EditText) view.findViewById(R.id.edt_lastname);
        etEmailId = (EditText) view.findViewById(R.id.edt_email);
        etCellNumber = (EditText) view.findViewById(R.id.edt_cellno);

        prgDialog = new ProgressDialog(activity);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        spinnercountryCode = (EditText) view.findViewById(R.id.strip_countrycode);
        btnSaveAccount = (Button) view.findViewById(R.id.btn_save_myaccount);
        btnSaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommanMethod.isOnline(getActivity())) {
                    if (checkValidationJoinUs()) {

                        RequestParams params = new RequestParams();
                        params.put("userId",mUserId);
                        params.put("firstName",etFirstName.getText().toString().trim());
                        params.put("lastName",etLastName.getText().toString().trim());
                        params.put("email",etEmailId.getText().toString().trim());

                        String result = etCellNumber.getText().toString().replaceAll("[-+.^:,]","");
                        params.put("mobileNo",result);
                        params.put("countryCode",spinnercountryCode.getText().toString().trim());
                        updateProfileWS(params);


                    }

                }
            }
        });


        RequestParams params = new RequestParams();
        params.put("userId", mUserId);
        getProfileDataWS(params);






        etCellNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean flag = true;
                String eachBlock[] = etCellNumber.getText().toString().split("-");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 14) {
                        flag = false;
                    }
                }
                if (flag) {

                    etCellNumber.setOnKeyListener(new View.OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((etCellNumber.getText().length() + 1) % 4) == 0) {

                            if (etCellNumber.getText().toString().split("-").length <= 2) {
                                etCellNumber.setText(etCellNumber.getText() + "-");
                                etCellNumber.setSelection(etCellNumber.getText().length());
                            }
                        }
                        a = etCellNumber.getText().toString();
                    } else {
                        a = etCellNumber.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    etCellNumber.setText(a);
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
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener


                    if (keyCode == KeyEvent.KEYCODE_BACK) {


                        Fragment homeFragment = new FragmentMainScreen();
                        FragmentManager frgManager;
                        frgManager = getFragmentManager();
                        frgManager.beginTransaction().replace(R.id.fragment_switch, homeFragment).commit();

                        return true;
                    }


                    return true;
                }
                return false;
            }
        });

        return view;
    }


    public void getProfileDataWS(RequestParams params) {

        String url = WebserviceUrlClass.ViewProfile;
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

                        JSONObject jsonObject = response.getJSONObject("profileData");

                        String firstName = jsonObject.getString("first_name");
                        String lastName = jsonObject.getString("last_name");
                        String email = jsonObject.getString("email");
                        String cellNumber = jsonObject.getString("mobile_no");
                        String countryCode = jsonObject.getString("country_code");

                        etFirstName.setText(firstName);
                        etLastName.setText(lastName);
                        etEmailId.setText(email);
                        etCellNumber.setText(cellNumber);
                        spinnercountryCode.setText(countryCode);

                    } else {
                        Toast.makeText(getActivity(),responseMessage,Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.hide();
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
            }
            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.hide();
            }
        });
    }





    public void updateProfileWS(RequestParams params) {

        String url = WebserviceUrlClass.UpdateProfile;
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

                        Toast.makeText(getActivity(),responseMessage,Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getActivity(),responseMessage,Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                CommanMethod.showAlert(getResources().getString(R.string.connection_error),getActivity());
                prgDialog.hide();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgDialog.hide();
            }
        });
    }





    private boolean checkValidationJoinUs() {

        if (etFirstName.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your first Name", getActivity());
            return false;
        } else if (etLastName.getText().toString().trim().length() == 0) {
            CommanMethod.showAlert("Please enter your last Name", getActivity());
            return false;
        }
        return true;

    }


}