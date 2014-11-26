package com.oa.cgpg;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterFragment extends Fragment {

    private OnRegisterFragmentListener listener;
    private Button Back , Register;
    String login, password;
    TextView emailText , passText;
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnRegisterFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRegisterFragmentListener");
        }


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        getActivity().setTitle("Logowanie");
        emailText = (TextView) rootView.findViewById(R.id.emailTextreg);
        login = emailText.toString();
        passText = (TextView) rootView.findViewById(R.id.passwordTextreg);
        password = passText.toString();
        Back = (Button) rootView.findViewById(R.id.backbt);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.backLoginFragment();

            }
        });
        Register = (Button) rootView.findViewById(R.id.createacount);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.createaccount(login, password);

            }
        });
        return rootView;
    }



    public interface OnRegisterFragmentListener {

        void backLoginFragment();

        void createaccount(String login,String password);
    }
}