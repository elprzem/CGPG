package com.oa.cgpg;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LoggedFragment extends Fragment {

    private OnLoggedFragmentListener listener;

    public LoggedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnLoggedFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoggedFragmentListener");
        }


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_logged, container, false);
        getActivity().setTitle("Profil");
        TextView usernameText = (TextView) rootView.findViewById(R.id.loginText);
        TextView emailText = (TextView) rootView.findViewById(R.id.emailText);
        usernameText.setText(LoggedUserInfo.getInstance().getUserName());
        emailText.setText(LoggedUserInfo.getInstance().getEmail());
        Button editBtn = (Button) rootView.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.startEditUserFragment();
            }
        });
        return rootView;
    }



    public interface OnLoggedFragmentListener {
        public void startEditUserFragment();
    }
}