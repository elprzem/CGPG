package com.oa.cgpg;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.oa.cgpg.models.Communicator;

public class LoginFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    Button button,button2;
    EditText editText, editText2;
    String pass, login;
    Communicator comm;
    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (Communicator) getActivity();
        editText = (EditText) getActivity().findViewById(R.id.editText);
        pass = editText.getText().toString();
        editText2 = (EditText) getActivity().findViewById(R.id.editText2);
        login = editText.getText().toString();

        button = (Button) getActivity().findViewById(R.id.button);
        button2= (Button) getActivity().findViewById(R.id.button2);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.button :

                comm.LoginButton(login, pass);
                break;
            case R.id.button2 :
                comm.openRegister();
                break;


        }
    }
}