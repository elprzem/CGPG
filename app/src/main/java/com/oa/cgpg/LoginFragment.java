package com.oa.cgpg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.oa.cgpg.connectivity.Connectivity;
import com.oa.cgpg.customControls.NoConnectionDialog;
import com.oa.cgpg.customControls.NotValidDataDialog;
import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLUserClass;
import com.oa.cgpg.models.opinionNetEntity;
import com.oa.cgpg.models.userNetEntity;

import java.util.List;

public class LoginFragment extends Fragment implements AsyncResponse{

    private OnLoginFragmentListener listener;
    private boolean toOpinions;
    private Integer poiId;
    private String poiTitle;

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

        rootView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

        getActivity().setTitle("Logowanie");
        Bundle args = getArguments();
        if(args != null && args.containsKey(Keys.POI_TITLE) && args.containsKey(Keys.POI_NUMBER) && args.containsKey(Keys.TO_OPINIONS)){
            toOpinions = getArguments().getBoolean(Keys.TO_OPINIONS);
            poiId = getArguments().getInt(Keys.POI_NUMBER);
            poiTitle = getArguments().getString(Keys.POI_TITLE);
        }

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
                    try {
                        if(Connectivity.isNetworkAvailable(getActivity())) {
                            userNetEntity UNE = new userNetEntity(username.getText().toString(), pass.getText().toString(), getActivity(), (AsyncResponse) getFragmentManager().findFragmentById(R.id.content_frame));
                            UNE.login();
                        }else {
                            NoConnectionDialog ncDialog = new NoConnectionDialog();
                            ncDialog.setMessage("Brak połączenia z Internetem");
                            ncDialog.show(getFragmentManager(), "noConnection");
                        }
                    } catch (Exception e) {
                        Log.i("no connection", "tu");
                        e.printStackTrace();
                    }


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
        //TODO userEntity zamiast output
        Log.i("login response", output);
        if(Integer.parseInt(output) > -1) {
            LoggedUserInfo.getInstance().setLoggedIn(true);
            LoggedUserInfo.getInstance().setUserId(Integer.parseInt(output));
            if(toOpinions){
                listener.startOpinionsFragment(false, poiId, poiTitle);
            }
            else
                listener.startLoggedFragment();
        }else{
            NotValidDataDialog dialog = new NotValidDataDialog();
            dialog.setMessage("Nie znaleziono użytkownika");
            dialog.show(getFragmentManager(), "not_valid_data");
        }
    }

    @Override
    public void processFinish(userNetEntity output) {

    }

    @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {

    }

    public interface OnLoginFragmentListener {
        void startLoggedFragment();
        void startRegisterFragment();
        void startOpinionsFragment(boolean addToBackStack, Integer idPOI, String titlePOI);
    }
}