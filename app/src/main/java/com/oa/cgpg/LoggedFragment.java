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
        Button editBtn = (Button) rootView.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = (String) ((TextView)rootView.findViewById(R.id.loginText)).getText().toString();
                String email = (String) ((TextView)rootView.findViewById(R.id.emailText)).getText().toString();
                listener.startEditUserFragment(username, email);
            }
        });
        return rootView;
    }



    public interface OnLoggedFragmentListener {
        public void startEditUserFragment(String username, String email);
    }
}