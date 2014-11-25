package com.oa.cgpg;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class Logged_fragment extends Fragment implements View.OnClickListener {

    Button button4,button5;
    TextView login_text;

    Communicator comm;

    public Logged_fragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        getActivity().setTitle("Logowanie");

        // setLogin(LoggedUserInfo.getInstance().getUserName());
        return inflater.inflate(R.layout.fragment_logged, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (Communicator) getActivity();
        button4 = (Button) getActivity().findViewById(R.id.button4);
        button5= (Button) getActivity().findViewById(R.id.button5);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        login_text = (TextView) getActivity().findViewById(R.id.textView6);
    }
    private void setLogin(String login ){
        login_text.setText("Login: "+login);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.button4 :
                break;
            case R.id.button5 :
                comm.LogOutButton();
                break;


        }
    }



}