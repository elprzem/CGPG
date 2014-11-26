package com.oa.cgpg;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class Logged_fragment extends Fragment {

    private OnLOGGEDFragmentListener listener;
    private Button LogOut;


    public Logged_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnLOGGEDFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLOGINFragmentListener");
        }


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logged, container, false);
        getActivity().setTitle("Logowanie");
        LogOut = (Button) rootView.findViewById(R.id.logoutbt);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.startLoginFragment();

            }
        });
        return rootView;
    }



    public interface OnLOGGEDFragmentListener {

        void startLoginFragment();
    }
}