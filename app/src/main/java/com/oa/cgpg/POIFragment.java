package com.oa.cgpg;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import android.widget.BaseExpandableListAdapter;

import com.oa.cgpg.dataOperations.dbOps;
import com.oa.cgpg.models.poiEntity;


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
    public ExpandableListView listView;
    private int typePOI;
    private int ParentClickStatus=-1;
    private int ChildClickStatus=-1;
    private ArrayList<POIItem> poiItems;
    private dbOps dbOps;
    public POIFragment() {
        // Empty constructor required for fragment subclasses
    }

    public void setDbOps(dbOps dbOps) {
        this.dbOps = dbOps;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poi, container, false);
        typePOI = getArguments().getInt(ARG_POI_NUMBER);
        String title = getResources().getStringArray(R.array.menu_array)[typePOI];

        getActivity().setTitle(title);

        Button seeOnMap = (Button) rootView.findViewById(R.id.seeOnMapButton);
        seeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MapFragment();
                Bundle args = new Bundle();
                args.putInt("type", typePOI);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_poi").commit();
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
                if(groupPosition != previousGroup)
                    listView.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
            }
        });

        //Creating static data in arraylist
        final ArrayList<POIItem> dummyList = buildDummyData();

        // Adding ArrayList data to ExpandableListView values
        loadHosts(dummyList);

        return rootView;
    }

    private ArrayList<POIItem> buildDummyData()
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
                //details.setPlusesCount
                //details.setMinusesCount
                //details.setImagePath

                //Add Child class object to parent class object
                poiItem.getDetails().add(details);
                //Adding Parent class object to ArrayList
                list.add(poiItem);
            }
        }
        return list;
    }


    private void loadHosts(final ArrayList<POIItem> newPoiItems)
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
            ((TextView) convertView.findViewById(R.id.text1)).setText(details.getDescription());
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
}