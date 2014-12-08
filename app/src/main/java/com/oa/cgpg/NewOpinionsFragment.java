package com.oa.cgpg;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.oa.cgpg.connectivity.Connectivity;
import com.oa.cgpg.customControls.NoConnectionDialog;
import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLOpinionSend;
import com.oa.cgpg.models.opinionNetEntity;
import com.oa.cgpg.models.userNetEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewOpinionsFragment extends Fragment implements AsyncResponse {
    private int poiId;
    private RadioButton positive;
    private RadioButton negative;
    private final int POSITIVE = 0;
    private final int NEGATIVE = 1;

    public NewOpinionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_opinions, container, false);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
        Bundle args = getArguments();
        poiId = args.getInt(Keys.POI_NUMBER, 0);
        final Button addOpinion = (Button) rootView.findViewById(R.id.addOpinion);
        positive = (RadioButton) rootView.findViewById(R.id.radioPlus);
        negative = (RadioButton) rootView.findViewById(R.id.radioMinus);

        final EditText opinionText = (EditText) rootView.findViewById(R.id.newOpinionText);
        opinionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) {
                    addOpinion.setEnabled(true);
                } else {
                    addOpinion.setEnabled(false);
                }
            }
        });
        InputFilter filter= new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    String checkMe = String.valueOf(source.charAt(i));

                    Pattern pattern = Pattern.compile("[AĄBCĆDEĘFGHIJKLŁMNŃOÓPQRSŚTUVWXYZŹŻaąbcćdeęfghijklłmnńoópqrsśtuvwxyzżź123456789[]]\n _:;/\"*+-@#)$%^&(!?><',.\\\\]*");
                    Matcher matcher = pattern.matcher(checkMe);
                    boolean valid = matcher.matches();
                    if(!valid){
                        Log.d("", "invalid");
                        return "";
                    }
                }
                return null;
            }
        };

        opinionText.setFilters(new InputFilter[]{filter});
        opinionText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(Connectivity.isNetworkAvailable(getActivity()))
                        sendOpinion(opinionText.getText().toString());
                    else{
                        NoConnectionDialog ncDialog = new NoConnectionDialog();
                        ncDialog.setMessage("Brak połączenia z Internetem");
                        ncDialog.show(getFragmentManager(), "no connection");
                    }
                    return true;
                }
                return false;
            }
        });
        addOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Connectivity.isNetworkAvailable(getActivity()))
                    sendOpinion(opinionText.getText().toString());
                else{
                    NoConnectionDialog ncDialog = new NoConnectionDialog();
                    ncDialog.setMessage("Brak połączenia z Internetem");
                    ncDialog.show(getFragmentManager(), "no connection");
                }
            }
        });
        return rootView;
    }

    private void sendOpinion(String text) {
        int type = (positive.isChecked() == true) ? POSITIVE : NEGATIVE;
        List<opinionNetEntity> list = new ArrayList<opinionNetEntity>();
        Log.i("type", String.valueOf(type));
        opinionNetEntity opinionEntity = new opinionNetEntity(1, text, LoggedUserInfo.getInstance().getUserName(), poiId, 0, 0, -1, type, new Date());
        //TODO - wysłanie opinii
        list.add(opinionEntity);
        XMLOpinionSend XOS = new XMLOpinionSend(getActivity(), list);
        XOS.delegate = this;
        Log.i("dsasd", "sending xml");
        XOS.execute();
    }

    @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {
    }

    @Override
    public void processFinish(String o) {
        getFragmentManager().popBackStack();
    }

    @Override
    public void processFinish(userNetEntity output) {

    }
}
