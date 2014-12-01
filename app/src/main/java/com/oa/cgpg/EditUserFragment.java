package com.oa.cgpg;



import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oa.cgpg.connectivity.Connectivity;
import com.oa.cgpg.customControls.NoConnectionDialog;
import com.oa.cgpg.customControls.NotValidDataDialog;
import com.oa.cgpg.customControls.UpdateSuccessfulDialogFragment;
import com.oa.cgpg.customControls.UpdateUnsuccessfulDialogFragment;
import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.models.opinionNetEntity;
import com.oa.cgpg.models.userNetEntity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EditUserFragment extends Fragment implements AsyncResponse {
    private String userName;
    private String email;
    private Integer userId;
    private String newEmail;

    public EditUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_user, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
        getActivity().setTitle("Edytuj dane");
        userName = LoggedUserInfo.getInstance().getUserName();
        email = LoggedUserInfo.getInstance().getEmail();
        userId = LoggedUserInfo.getInstance().getUserId();
        TextView loginText = (TextView) rootView.findViewById(R.id.loginText);
        final EditText emailText = (EditText) rootView.findViewById(R.id.emailText);
        loginText.setText(userName);
        emailText.setText(email);
        final EditText oldPass = (EditText) rootView.findViewById(R.id.oldPassText);
        final EditText newPass = (EditText) rootView.findViewById(R.id.newPassText);
        final EditText newPassConf = (EditText) rootView.findViewById(R.id.newPassConfText);
        Button confirmChangesBtn = (Button) rootView.findViewById(R.id.confirmChangesBtn);
        confirmChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkUserInput(emailText.getText().toString(), oldPass.getText().toString(), newPass.getText().toString(), newPassConf.getText().toString())){
                    if(Connectivity.isNetworkAvailable(getActivity())) {
                        //edycja na serwerze
                        try {
                            newEmail = emailText.getText().toString();
                            userNetEntity UNE = new userNetEntity(userId, oldPass.getText().toString(), newPass.getText().toString(),newEmail, getActivity(), (AsyncResponse) getActivity().getFragmentManager().findFragmentById(R.id.content_frame) );
                            UNE.update();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        NoConnectionDialog ncDialog = new NoConnectionDialog();
                        ncDialog.setMessage("Brak połączenia z Internetem");
                        ncDialog.show(getFragmentManager(), "noConnection");
                    }
                }
            }
        });
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

        emailText.setFilters(new InputFilter[]{filter});
        oldPass.setFilters(new InputFilter[]{filter});
        newPass.setFilters(new InputFilter[]{filter});
        newPassConf.setFilters(new InputFilter[]{filter});
        return rootView;
    }

    boolean checkUserInput(String email, String pass1, String pass2, String pass3){
        Log.i("check", String.valueOf(pass1.length())+ " " +String.valueOf(pass2.length())+" "+String.valueOf(pass3.length()) );
        if(isValidEmail(email)){
            if(pass1.length() < 4 || pass1.length() > 10 ||( pass2.length() < 4 || pass2.length() > 10 || pass3.length() < 4 || pass3.length() > 10 )&&(pass2.length() != 0 && pass3.length() != 0)){
                NotValidDataDialog dialog = new NotValidDataDialog();
                dialog.setMessage("Hasło powinno mieć długość od 4 do 10 znaków");
                dialog.show(getFragmentManager(), "not_valid_data");
                return false;
            }else if(!pass2.equals(pass3)){
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
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {

    }

    @Override
    public void processFinish(String o) {
        if(o.equals("Pass does not match")){
            NotValidDataDialog dialog = new NotValidDataDialog();
            dialog.setMessage("Błędne hasło");
            dialog.show(getFragmentManager(), "not_valid_data");
        }else if(o.equals("OK")){
            UpdateSuccessfulDialogFragment dialog = new UpdateSuccessfulDialogFragment();
            dialog.show(getFragmentManager(), "update_successful");
            LoggedUserInfo.getInstance().setEmail(newEmail);
        }else{
            UpdateUnsuccessfulDialogFragment dialog = new UpdateUnsuccessfulDialogFragment();
            dialog.show(getFragmentManager(), "update_unsuccessful");
        }
    }

    @Override
    public void processFinish(userNetEntity output) {

    }
}
