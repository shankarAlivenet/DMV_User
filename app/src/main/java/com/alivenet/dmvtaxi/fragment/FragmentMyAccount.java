package com.alivenet.dmvtaxi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.alivenet.dmvtaxi.R;

/**
 * Created by narendra on 6/17/2016.
 */
public class FragmentMyAccount extends Fragment {

    Toolbar toolbar;

    public static FragmentMyAccount newInstance(String sectionTitle) {
        FragmentMyAccount fragment = new FragmentMyAccount();
        Bundle args = new Bundle();
        // args.putString(ARG_SECTION_TITLE, sectionTitle);
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
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);


        return view;
    }
}


