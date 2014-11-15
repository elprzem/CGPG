package com.oa.cgpg;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.oa.cgpg.dataOperations.dbOps;
import com.oa.cgpg.models.poiEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


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

    private OnPOIFragmentListener listener;

    public static String ARG_POI_NUMBER;
    public ExpandableListView listView;
    private int typePOI;
    private int ParentClickStatus=-1;
    private int ChildClickStatus=-1;
    private ArrayList<POIItem> poiItems;
    private dbOps dbOps;
    private Button seeOnMap;
    public POIFragment() {
        // Empty constructor required for fragment subclasses
    }

    public void setDbOps(dbOps dbOps) {
        this.dbOps = dbOps;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnPOIFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPOIFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poi, container, false);
        typePOI = getArguments().getInt(ARG_POI_NUMBER);
        String title = getResources().getStringArray(R.array.menu_array)[typePOI];

        getActivity().setTitle(title);

        seeOnMap = (Button) rootView.findViewById(R.id.seeOnMapButton);
        seeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.startMapFragment(typePOI);
                //TODO to remove, now it is moved to MainActivity
/*                Fragment fragment = new MapFragment();
                Bundle args = new Bundle();
                args.putInt("type", typePOI);
                fragment.setDatabaseRef(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_poi").commit();*/
            }
        });
        listView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        // Set ExpandableListView values

        listView.setGroupIndicator(null);
        listView.setDividerHeight(1);
        registerForContextMenu(listView);

        //collapse other expanded items
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    listView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        //Creating static data in arraylist
        final ArrayList<POIItem> dataFromDB = getDataFromDB();

        // Adding ArrayList data to ExpandableListView values
        loadDataIntoAdapter(dataFromDB);

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        int seeOnMapButtonHeight = seeOnMap.getHeight();
        Log.i("seeOnMapButtonHeight:", String.valueOf(seeOnMapButtonHeight));
        int navBarHeight = getNavigationBarHeight(getActivity(), getActivity().getResources().getConfiguration().orientation);
        Log.i("navBarHeight:", String.valueOf(navBarHeight));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) listView.getLayoutParams();
        params.height = height - 2*navBarHeight - getStatusBarHeight();
        listView.setLayoutParams(params);
        Log.i("wysokosc:", String.valueOf(listView.getLayoutParams().height));
    }
    private int getNavigationBarHeight(Context context, int orientation) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(
                orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private ArrayList<POIItem> getDataFromDB()
    {
        // Creating ArrayList of type parent class to store parent class objects
        final ArrayList<POIItem> list = new ArrayList<POIItem>();
        List<poiEntity> pois = dbOps.getPois();
        for(poiEntity poi : pois)
        {
            //Create parent class object
            final POIItem poiItem = new POIItem();
            if(poi.getType().getIdType() == typePOI) {
                // Set values in parent class object
                poiItem.setTitle(poi.getName());
                poiItem.setDetails(new ArrayList<POIDetails>());
                // Create Child class object
                final POIDetails details = new POIDetails();
                details.setDescription(poi.getDescription());
                details.setImagePath(poi.getLinkToImage());
                details.setPlusesCount(poi.getRatingPlus());
                details.setMinusesCount(poi.getRatingMinus());

                //Add Child class object to parent class object
                poiItem.getDetails().add(details);
                //Adding Parent class object to ArrayList
                list.add(poiItem);
            }
        }
        return list;
    }


    private void loadDataIntoAdapter(final ArrayList<POIItem> newPoiItems)
    {
        if (newPoiItems == null)
            return;

        poiItems = newPoiItems;

        // Check for ExpandableListAdapter object
        if (listView.getExpandableListAdapter() == null)
        {
            //Create ExpandableListAdapter Object
            final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();

            // Set Adapter to ExpandableList Adapter
            listView.setAdapter(mAdapter);
        }
        else
        {
            // Refresh ExpandableListView data
            ((MyExpandableListAdapter)listView.getExpandableListAdapter()).notifyDataSetChanged();
        }
    }
    public Drawable getImageFromAsstes(String path) {
        try {
            // get input stream
            InputStream ims = getActivity().getAssets().open(path);
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
           return d;
        }
        catch(IOException ex) {
            return null;
        }

    }
    /**
     * A Custom adapter to create Parent view (Used poi_grouprowprow.xml) and Child View((Used poi_childrow.xml.xml).
     */
    private class MyExpandableListAdapter extends BaseExpandableListAdapter
    {


        private LayoutInflater inflater;

        public MyExpandableListAdapter()
        {
            // Create Layout Inflater
            inflater = LayoutInflater.from(getActivity());
        }


        // This Function used to inflate parent rows view

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parentView)
        {
            final POIItem parent = poiItems.get(groupPosition);

            // Inflate poi_grouprow.xml.xml file for parent rows
            convertView = inflater.inflate(R.layout.poi_grouprow, parentView, false);

            // Get poi_grouprow.xml.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.text1)).setText(parent.getTitle());

            //Log.i("onCheckedChanged", "isChecked: "+parent.isChecked());

            return convertView;
        }


        // This Function used to inflate child rows view
        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView)
        {
            final POIItem poiItem = poiItems.get(groupPosition);
            final POIDetails details = poiItem.getDetails().get(childPosition);

            // Inflate poi_childrowdrow.xml file for child rows
            convertView = inflater.inflate(R.layout.poi_childrow, parentView, false);

            // Get poi_childrowdrow.xml file elements and set values
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            ((TextView) convertView.findViewById(R.id.text1)).setText(details.getDescription());
            ((TextView) convertView.findViewById(R.id.text1)).setWidth(2*width/3);
            ((ImageView)convertView.findViewById(R.id.image)).setImageDrawable(getImageFromAsstes(details.getImagePath()));
            ((Button) convertView.findViewById(R.id.button_opinions)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), OpinionsActivity.class);
                    intent.putExtra("poi", poiItems.get(groupPosition).getTitle());
                    startActivity(intent);
                }
            });
            return convertView;
        }


        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
            return poiItems.get(groupPosition).getDetails().get(childPosition);
        }

        //Call when child row clicked
        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            /****** When Child row clicked then this function call *******/

            //Log.i("Noise", "parent == "+groupPosition+"=  child : =="+childPosition);
            if( ChildClickStatus!=childPosition)
            {
                ChildClickStatus = childPosition;
            }

            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition)
        {
            int size=0;
            if(poiItems.get(groupPosition).getDetails()!=null)
                size = poiItems.get(groupPosition).getDetails().size();
            return size;
        }


        @Override
        public Object getGroup(int groupPosition)
        {
            //Log.i("Parent", groupPosition+"=  getGroup ");

            return poiItems.get(groupPosition);
        }

        @Override
        public int getGroupCount()
        {
            return poiItems.size();
        }

        //Call when parent row clicked
        @Override
        public long getGroupId(int groupPosition)
        {
           // Log.i("Parent", groupPosition+"=  getGroupId "+ParentClickStatus);

            ParentClickStatus=groupPosition;
            if(ParentClickStatus==0)
                ParentClickStatus=-1;

            return groupPosition;
        }

        @Override
        public void notifyDataSetChanged()
        {
            // Refresh List rows
            super.notifyDataSetChanged();
        }

        @Override
        public boolean isEmpty()
        {
            return ((poiItems == null) || poiItems.isEmpty());
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled()
        {
            return true;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnPOIFragmentListener{
        void startMapFragment(Integer typePOI);
    }

}