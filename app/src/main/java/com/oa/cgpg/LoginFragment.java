package com.oa.cgpg;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.oa.cgpg.customControls.NotValidDataDialog;
import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLUserClass;
import com.oa.cgpg.models.opinionNetEntity;

import java.util.List;

public class LoginFragment extends Fragment implements AsyncResponse{

    private OnLoginFragmentListener listener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnLoginFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginFragmentListener");
        }


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Logowanie");
        Button registerBtn = (Button) rootView.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.startRegisterFragment();
            }
        });
        final EditText username = (EditText) rootView.findViewById(R.id.loginText);
        final EditText pass = (EditText) rootView.findViewById(R.id.passText);
        Button loginBtn = (Button) rootView.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().length() > 3 && pass.getText().toString().length() > 3) {
                    XMLUserClass xmlUserClass = new XMLUserClass(getActivity(), (AsyncResponse) getFragmentManager().findFragmentById(R.id.content_frame), username.getText().toString(), pass.getText().toString());
                    xmlUserClass.execute();
                }
                else{
                    NotValidDataDialog dialog = new NotValidDataDialog();
                    dialog.setMessage("Wprowadź prawidłowe dane do logowania");
                    dialog.show(getFragmentManager(), "not_valid_data");
                }
            }
        });
        return rootView;
    }

    @Override
    public void processFinish(String output){
        //listener.startLoggedFragment();
        Log.i("login response", output);
        if(Integer.parseInt(output) > -1) {
            LoggedUserInfo.getInstance().setLoggedIn(true);
            LoggedUserInfo.getInstance().setUserId(Integer.parseInt(output));
            listener.startLoggedFragment();
        }else{
            NotValidDataDialog dialog = new NotValidDataDialog();
            dialog.setMessage("Nie znaleziono użytkownika");
            dialog.show(getFragmentManager(), "not_valid_data");
        }
    }
    @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {

    }

    public interface OnLoginFragmentListener {
        void startLoggedFragment();
        void startRegisterFragment();
    }
}