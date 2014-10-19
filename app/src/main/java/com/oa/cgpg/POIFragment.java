package com.oa.cgpg;

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
import android.widget.BaseExpandableListAdapter;


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
    private int ParentClickStatus=-1;
    private int ChildClickStatus=-1;
    private ArrayList<ExpandableListParent> parents;

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
        final ArrayList<ExpandableListParent> dummyList = buildDummyData();

        // Adding ArrayList data to ExpandableListView values
        loadHosts(dummyList);

        return rootView;
    }

    private ArrayList<ExpandableListParent> buildDummyData()
    {
        // Creating ArrayList of type parent class to store parent class objects
        final ArrayList<ExpandableListParent> list = new ArrayList<ExpandableListParent>();
        for (int i = 1; i < 4; i++)
        {
            //Create parent class object
            final ExpandableListParent parent = new ExpandableListParent();

            // Set values in parent class object
            if(i==1){
                parent.setTitle("punkt 1");
                parent.setChildren(new ArrayList<ExpandableListChild>());

                // Create Child class object
                final ExpandableListChild child = new ExpandableListChild();
                child.setText1("opis");

                //Add Child class object to parent class object
                parent.getChildren().add(child);
            }
            else if(i==2){
                parent.setTitle("punkt 2");
                parent.setChildren(new ArrayList<ExpandableListChild>());

                // Create Child class object
                final ExpandableListChild child = new ExpandableListChild();
                child.setText1("opis");

                //Add Child class object to parent class object
                parent.getChildren().add(child);
            }
            else if(i==3){
                parent.setTitle("punkt 3");
                parent.setChildren(new ArrayList<ExpandableListChild>());

                // Create Child class object
                final ExpandableListChild child = new ExpandableListChild();
                child.setText1("opis");

                //Add Child class object to parent class object
                parent.getChildren().add(child);
            }

            //Adding Parent class object to ArrayList
            list.add(parent);
        }
        return list;
    }


    private void loadHosts(final ArrayList<ExpandableListParent> newParents)
    {
        if (newParents == null)
            return;

        parents = newParents;

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
            final ExpandableListParent parent = parents.get(groupPosition);

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
            final ExpandableListParent parent = parents.get(groupPosition);
            final ExpandableListChild child = parent.getChildren().get(childPosition);

            // Inflate poi_childrowdrow.xml file for child rows
            convertView = inflater.inflate(R.layout.poi_childrow, parentView, false);

            // Get poi_childrowdrow.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.text1)).setText(child.getText1());
            ((Button) convertView.findViewById(R.id.button_opinions)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), OpinionsActivity.class);
                    intent.putExtra("poi", parents.get(groupPosition).getTitle());
                    startActivity(intent);
                }
            });
            return convertView;
        }


        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
            return parents.get(groupPosition).getChildren().get(childPosition);
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
            if(parents.get(groupPosition).getChildren()!=null)
                size = parents.get(groupPosition).getChildren().size();
            return size;
        }


        @Override
        public Object getGroup(int groupPosition)
        {
            //Log.i("Parent", groupPosition+"=  getGroup ");

            return parents.get(groupPosition);
        }

        @Override
        public int getGroupCount()
        {
            return parents.size();
        }

        //Call when parent row clicked
        @Override
        public long getGroupId(int groupPosition)
        {
           // Log.i("Parent", groupPosition+"=  getGroupId "+ParentClickStatus);

            if(groupPosition==2 && ParentClickStatus!=groupPosition){

            }

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
            return ((parents == null) || parents.isEmpty());
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