package com.oa.cgpg;



import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EditUserFragment extends Fragment {
    private String userName;
    private String email;

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
        Bundle args = getArguments();
        userName = args.getString(Keys.USER_NAME);
        email = args.getString(Keys.EMAIL);
        TextView loginText = (TextView) rootView.findViewById(R.id.loginText);
        EditText emailText = (EditText) rootView.findViewById(R.id.emailText);
        loginText.setText(userName);
        emailText.setText(email);
        return rootView;
    }


}
