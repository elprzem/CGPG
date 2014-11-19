package com.oa.cgpg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLOpinionGetParsing;
import com.oa.cgpg.dataOperations.dbOps;
import com.oa.cgpg.models.opinionNetEntity;

import java.util.ArrayList;
import java.util.List;

public class OpinionsFragment extends Fragment implements AsyncResponse {
    private ArrayList<opinionNetEntity> opinions;
    private ArrayList<opinionNetEntity> opinionsPresented;
    private final int POSITIVE = 0;
    private final int NEGATIVE = 1;
    private final int ALL = 2;
    public ExpandableListView listViewOpinionTypes;
    public ListView listViewOpinions;
    private int ParentClickStatus=-1;
    private int ChildClickStatus=-1;
    private ArrayList<OpinionTypes> opinionTypes;
    private Button newOpinion;
    private int poiId;


    private OnOpinionsFragmentListener mListener;

    public OpinionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_opinions, container, false);
     //   setContentView(R.layout.activity_opinions);
     //   getActionBar().setDisplayHomeAsUpEnabled(true);

        XMLOpinionGetParsing opinionParser = new XMLOpinionGetParsing(getActivity(),1,5);
        opinionParser.delegate=this;
        opinionParser.execute();

        Bundle args = getArguments();
        String title = args.getString("poi");
        poiId = args.getInt("poiNr", 0);
        getActivity().setTitle(title);
        newOpinion = (Button) rootView.findViewById(R.id.newOpinion);
        newOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), NewOpinionActivity.class);
                intent.putExtra("poiNr",poiId);
                startActivity(intent);
            }
        });

        opinions = new ArrayList<opinionNetEntity>();
        opinionsPresented = new ArrayList<opinionNetEntity>();

        listViewOpinions = (ListView) rootView.findViewById(R.id.commList);
        loadOpinionsIntoAdapter();

        listViewOpinionTypes = (ExpandableListView) rootView.findViewById(R.id.typeListView);
        // Set ExpandableListView values
        listViewOpinionTypes.setGroupIndicator(null);
        listViewOpinionTypes.setDividerHeight(1);
        registerForContextMenu(listViewOpinionTypes);

        //collapse other expanded items
        listViewOpinionTypes.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    listViewOpinionTypes.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        listViewOpinionTypes.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ArrayList<opinionNetEntity> selectedComments = new ArrayList<opinionNetEntity>();
                switch (childPosition) {//selekcjonowanie komentarzy
                    case POSITIVE:
                        for (int i = 0; i < opinions.size(); i++) {
                            if (opinions.get(i).getOpinionType() == POSITIVE ) {
                                selectedComments.add(opinions.get(i));
                            }
                        }
                        break;
                    case NEGATIVE:
                        for (int i = 0; i < opinions.size(); i++) {
                            if (opinions.get(i).getOpinionType() == NEGATIVE) {
                                selectedComments.add(opinions.get(i));
                            }
                        }
                        break;
                    case ALL:
                        for (int i = 0; i < opinions.size(); i++) {
                            selectedComments.add(opinions.get(i));
                        }
                        break;
                }
                opinionsPresented.clear();
                opinionsPresented.addAll(selectedComments);
                ((OpinionsAdapter) listViewOpinions.getAdapter()).notifyDataSetChanged();
                String childTitle = opinionTypes.get(0).getTypes().get(childPosition).getDescription();
                opinionTypes.get(0).setTitle(childTitle);

                listViewOpinionTypes.collapseGroup(0);
                return true;
            }
        });
        //Creating static data in arraylist
        final ArrayList<OpinionTypes> types = getOpinionTypes();

        // Adding ArrayList data to ExpandableListView values
        loadOpinionTypesIntoAdapter(types);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnOpinionsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOpinionsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {
        for(opinionNetEntity op : list){
            Log.i("opinia: ", op.toString());
            opinions.add(op);
            opinionsPresented.add(op);
        }
        ((OpinionsAdapter) listViewOpinions.getAdapter()).notifyDataSetChanged();
    }
    @Override
    public void processFinish(String output) {

    }
    class OpinionsAdapter extends BaseAdapter {

        private LayoutInflater inflater = null;

        public OpinionsAdapter() {
            // Create Layout Inflater
            inflater = LayoutInflater.from(getActivity().getApplicationContext());
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return opinionsPresented.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return opinionsPresented.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {//setting opinion values in row
            // TODO Auto-generated method stub
            View rowView = convertView;
            if (rowView == null)
                rowView = inflater.inflate(R.layout.opinion_row, null);
            TextView opinion = (TextView) rowView.findViewById(R.id.opinionText);
            opinion.setText(opinionsPresented.get(position).getOpinionText());
            TextView username = (TextView) rowView.findViewById(R.id.usernameText);
            username.setText(opinionsPresented.get(position).getUsername());
            TextView date = (TextView) rowView.findViewById(R.id.dateText);
            DateFormat df = new android.text.format.DateFormat();
            date.setText( df.format("dd/MM/yy", opinionsPresented.get(position).getAddDate()));
            TextView pluses = (TextView) rowView.findViewById(R.id.plusText);
            pluses.setText(String.valueOf(opinionsPresented.get(position).getRatingPlus()));
            TextView minuses = (TextView) rowView.findViewById(R.id.minusText);
            minuses.setText(String.valueOf(opinionsPresented.get(position).getRatingMinus()));
            LinearLayout opinionLayout = (LinearLayout) rowView.findViewById(R.id.opinionLayout);

            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                if(opinionsPresented.get(position).getOpinionType() == POSITIVE){
                    opinionLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_layout_green));
                }else{
                    opinionLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_layout_red));
                }
            } else {
                if(opinionsPresented.get(position).getOpinionType() == POSITIVE){
                    opinionLayout.setBackground(getResources().getDrawable(R.drawable.rounded_layout_green));
                }else{
                    opinionLayout.setBackground(getResources().getDrawable(R.drawable.rounded_layout_red));
                }
            }
            return rowView;
        }
    }
    private ArrayList<OpinionTypes> getOpinionTypes()
    {
        // Creating ArrayList of type parent class to store parent class objects
        final ArrayList<OpinionTypes> list = new ArrayList<OpinionTypes>();
        OpinionTypes typesList = new OpinionTypes();
        typesList.setTitle("Wszystkie");
        typesList.setTypes(new ArrayList<OpinionType>());

        // Create Child class object
        final OpinionType type1 = new OpinionType();
        type1.setDescription("Pozytywne");

        //Add Child class object to parent class object
        typesList.getTypes().add(type1);
        // Create Child class object
        final OpinionType type2 = new OpinionType();
        type2.setDescription("Negatywne");

        //Add Child class object to parent class object
        typesList.getTypes().add(type2);

        // Create Child class object
        final OpinionType type3 = new OpinionType();
        type3.setDescription("Wszystkie");

        //Add Child class object to parent class object
        typesList.getTypes().add(type3);

        list.add(typesList);
        return list;
    }


    private void loadOpinionTypesIntoAdapter(final ArrayList<OpinionTypes> newTypesList)
    {
        if (newTypesList == null)
            return;

        opinionTypes = newTypesList;

        // Check for ExpandableListAdapter object
        if (listViewOpinionTypes.getExpandableListAdapter() == null)
        {
            //Create ExpandableListAdapter Object
            final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();

            // Set Adapter to ExpandableList Adapter
            listViewOpinionTypes.setAdapter(mAdapter);
        }
        else
        {
            // Refresh ExpandableListView data
            ((MyExpandableListAdapter) listViewOpinionTypes.getExpandableListAdapter()).notifyDataSetChanged();
        }
    }
    private void loadOpinionsIntoAdapter(){
        if (listViewOpinions.getAdapter() == null)
        {
            //Create ExpandableListAdapter Object
            final OpinionsAdapter mAdapter = new OpinionsAdapter();

            // Set Adapter to ExpandableList Adapter
            listViewOpinions.setAdapter(mAdapter);
        }
        else
        {
            // Refresh ExpandableListView data
            ((OpinionsAdapter) listViewOpinions.getAdapter()).notifyDataSetChanged();
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
            inflater = LayoutInflater.from(getActivity().getApplicationContext());
        }


        // This Function used to inflate parent rows view

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parentView)
        {
            final OpinionTypes typesList = opinionTypes.get(groupPosition);

            // Inflate poi_grouprow.xml.xml file for parent rows
            convertView = inflater.inflate(R.layout.opinion_grouprow, parentView, false);

            // Get poi_grouprow.xml.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.text1)).setText(typesList.getTitle());

            //Log.i("onCheckedChanged", "isChecked: "+parent.isChecked());

            return convertView;
        }


        // This Function used to inflate child rows view
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView)
        {
            final OpinionTypes typesList = opinionTypes.get(groupPosition);
            final OpinionType type = typesList.getTypes().get(childPosition);

            // Inflate poi_childrowdrow.xml file for child rows
            convertView = inflater.inflate(R.layout.opinion_childrow, parentView, false);

            // Get poi_childrowdrow.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.text1)).setText(type.getDescription());

            return convertView;
        }


        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
            return opinionTypes.get(groupPosition).getTypes().get(childPosition);
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
            return 3;
        }


        @Override
        public Object getGroup(int groupPosition)
        {
            //Log.i("Parent", groupPosition+"=  getGroup ");

            return opinionTypes.get(groupPosition);
        }

        @Override
        public int getGroupCount()
        {
            return opinionTypes.size();
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
            return ((opinionTypes == null) || opinionTypes.isEmpty());
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
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnOpinionsFragmentListener{
       // void startOpinionsFragment(Integer idPOI);
    }

}
