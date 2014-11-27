package com.oa.cgpg;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View rootView = inflater.inflate(R.layout.fragment_logged, container, false);
        getActivity().setTitle("Profil");

        return rootView;
    }



    public interface OnLoggedFragmentListener {

    }
}