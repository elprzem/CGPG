package com.oa.cgpg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oa.cgpg.connectivity.Connectivity;
import com.oa.cgpg.customControls.NoConnectionDialog;
import com.oa.cgpg.customControls.NotValidDataDialog;
import com.oa.cgpg.customControls.RegisterSuccessfulDialogFragment;
import com.oa.cgpg.customControls.RegisterUnsuccessfulDialogFragment;
import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLUserClass;
import com.oa.cgpg.models.opinionNetEntity;
import com.oa.cgpg.models.userNetEntity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment implements  AsyncResponse {

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        getActivity().setTitle("Rejestracja");
        final Button registerBtn = (Button) rootView.findViewById(R.id.registerBtn);
        final EditText username = (EditText) rootView.findViewById(R.id.loginText);
        final EditText pass = (EditText) rootView.findViewById(R.id.passText);
        final EditText passConfirm = (EditText) rootView.findViewById(R.id.passRepeatText);
        final EditText email = (EditText) rootView.findViewById(R.id.emailText);
        TextWatcher isFieldEmpty  =  new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (passConfirm.length() != 0 && username.getText().toString().length() != 0 && pass.getText().toString().length() != 0 && email.getText().toString().length() != 0) {
                    registerBtn.setEnabled(true);
                } else {
                    registerBtn.setEnabled(false);
                }
            }
        };
        passConfirm.addTextChangedListener(isFieldEmpty);
        pass.addTextChangedListener(isFieldEmpty);
        username.addTextChangedListener(isFieldEmpty);
        email.addTextChangedListener(isFieldEmpty);

        InputFilter filter= new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    String checkMe = String.valueOf(source.charAt(i));

                    Pattern pattern = Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789_.@]*");
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
        InputFilter filterPassword= new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    String checkMe = String.valueOf(source.charAt(i));

                    Pattern pattern = Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789[]]\n _:;/\"*+-@#)$%^&(!?><',.\\\\]*");
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


        passConfirm.setFilters(new InputFilter[]{filterPassword});
        email.setFilters(new InputFilter[]{filter});
        username.setFilters(new InputFilter[]{filter});
        pass.setFilters(new InputFilter[]{filterPassword});

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkUserInput(username.getText().toString(), email.getText().toString(), pass.getText().toString(), passConfirm.getText().toString())) {
                    try {
                        if(Connectivity.isNetworkAvailable(getActivity())) {
                            userNetEntity UNE = new userNetEntity(username.getText().toString(), pass.getText().toString(), email.getText().toString(), getActivity(), (AsyncResponse) getActivity().getFragmentManager().findFragmentById(R.id.content_frame));
                            UNE.register();
                        }else {
                            NoConnectionDialog ncDialog = new NoConnectionDialog();
                            ncDialog.setMessage("Brak połączenia z Internetem");
                            ncDialog.show(getFragmentManager(), "noConnection");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return rootView;
    }

    boolean checkUserInput(String username, String email, String pass1, String pass2){
        Log.i("username length",String.valueOf(username.length()));
        if(isValidEmail(email)){
            if(username.length() < 4 || username.length() > 10){
                NotValidDataDialog dialog = new NotValidDataDialog();
                dialog.setMessage("Login powinien mieć długość od 4 do 10 znaków");
                dialog.show(getFragmentManager(), "not_valid_data");
                return false;
            }
            else if(pass1.length() < 4 || pass1.length() > 10 | pass2.length() < 4 | pass2.length() > 10){
                NotValidDataDialog dialog = new NotValidDataDialog();
                dialog.setMessage("Hasło powinno mieć długość od 4 do 10 znaków");
                dialog.show(getFragmentManager(), "not_valid_data");
                return false;
            }else if(!pass1.equals(pass2)){
                NotValidDataDialog dialog = new NotValidDataDialog();
                dialog.setMessage("Potwierdź hasło");
                dialog.show(getFragmentManager(), "not_valid_data");
                return false;
            }
            return true;
        }else{
            NotValidDataDialog dialog = new NotValidDataDialog();
            dialog.setMessage("Nieprawidłowy format adresu email");
            dialog.show(getFragmentManager(), "not_valid_data");
            return false;
        }
    }
    @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {

    }

    @Override
    public void processFinish(String o) {
        if(o.equals("OK")) {
            RegisterSuccessfulDialogFragment dialog = new RegisterSuccessfulDialogFragment();
            dialog.show(getFragmentManager(), "register_successful");
        }else{
            RegisterUnsuccessfulDialogFragment dialog = new RegisterUnsuccessfulDialogFragment();
            dialog.show(getFragmentManager(), "register_unsuccessful");
        }
    }

    @Override
    public void processFinish(userNetEntity output) {

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}