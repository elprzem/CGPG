package com.oa.cgpg;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link POIFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link POIFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public class POIFragment extends Fragment {
    public static String ARG_POI_NUMBER;

    public POIFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poi, container, false);
        int i = getArguments().getInt(ARG_POI_NUMBER);
        String title = getResources().getStringArray(R.array.menu_array)[i];

        getActivity().setTitle(title);
        return rootView;
    }
}