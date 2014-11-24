package com.oa.cgpg;


import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLOpinionSend;
import com.oa.cgpg.models.opinionNetEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
        opinionText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    sendOpinion(opinionText.getText().toString());
                    return true;
                }
                return false;
            }
        });
        addOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOpinion(opinionText.getText().toString());
            }
        });
        return rootView;
    }

    private void sendOpinion(String text) {
        int type = positive.isChecked() ? POSITIVE : NEGATIVE;
        List<opinionNetEntity> list = new ArrayList<opinionNetEntity>();

        opinionNetEntity opinionEntity = new opinionNetEntity(1, text, LoggedUserInfo.getInstance().getUserName(), poiId, 1, 3, 4, type, new Date());
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
}
