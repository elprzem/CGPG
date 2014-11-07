package com.oa.cgpg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OpinionsActivity extends Activity {

    private ArrayList<String> comments;//komentarze pobrane z bazy mają również użytkownika, datę itd
    private ArrayList<String> commentsOnScreen;
    private final int POSITIVE = 0;
    private final int NEGATIVE = 1;
    private final int ALL = 2;
    public ExpandableListView listViewCommTypes;
    public ListView listViewComm;
    private int ParentClickStatus=-1;
    private int ChildClickStatus=-1;
    private ArrayList<CommentTypes> commTypes;
    private Button newOpinion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinions);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title = intent.getStringExtra("poi");
        setTitle(title);
        newOpinion = (Button) findViewById(R.id.newOpinion);
        newOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewOpinionActivity.class);
                startActivity(intent);
            }
        });

        comments = new ArrayList<String>();
        comments.add("1");
        comments.add("2");
        comments.add("3");

        commentsOnScreen = new ArrayList<String>();
        commentsOnScreen.addAll(comments);

        listViewComm = (ListView) findViewById(R.id.commList);
        loadHosts();

        listViewCommTypes = (ExpandableListView) findViewById(R.id.typeListView);
        // Set ExpandableListView values
        listViewCommTypes.setGroupIndicator(null);
        listViewCommTypes.setDividerHeight(1);
        registerForContextMenu(listViewCommTypes);

        //collapse other expanded items
        listViewCommTypes.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    listViewCommTypes.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        listViewCommTypes.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ArrayList<String> selectedComments = new ArrayList<String>();
                switch(childPosition){//selekcjonowanie komentarzy
                    case POSITIVE:
                        for(int i = 0; i < comments.size(); i++){
                            if (comments.get(i).equals(new String("1"))){
                                selectedComments.add(comments.get(i));
                            }
                        }
                        break;
                    case NEGATIVE:
                        for(int i = 0; i < comments.size(); i++){
                            if (comments.get(i).equals(new String("2"))){
                                selectedComments.add(comments.get(i));
                            }
                        }
                        break;
                    case ALL:
                        for(int i = 0; i < comments.size(); i++){
                             selectedComments.add(comments.get(i));
                        }
                        break;
                }
                commentsOnScreen.clear();
                commentsOnScreen.addAll(selectedComments);
                ((OpinionsAdapter)listViewComm.getAdapter()).notifyDataSetChanged();
                String childTitle = commTypes.get(0).getTypes().get(childPosition).getDescription();
                commTypes.get(0).setTitle(childTitle);

               listViewCommTypes.collapseGroup(0);
                return true;
            }
        });
        //Creating static data in arraylist
        final ArrayList<CommentTypes> dummyList = buildDummyData();

        // Adding ArrayList data to ExpandableListView values
        loadHosts(dummyList);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.opinions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class OpinionsAdapter extends BaseAdapter {

        private LayoutInflater inflater = null;

        public OpinionsAdapter() {
            // Create Layout Inflater
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return commentsOnScreen.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return commentsOnScreen.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View rowView = convertView;
            if (rowView == null)
                rowView = inflater.inflate(R.layout.opinion_row, null);
            TextView opinion = (TextView) rowView.findViewById(R.id.opinionText);
            opinion.setText(commentsOnScreen.get(position));
            return rowView;
        }
    }

    private ArrayList<CommentTypes> buildDummyData()
    {
        // Creating ArrayList of type parent class to store parent class objects
        final ArrayList<CommentTypes> list = new ArrayList<CommentTypes>();
        CommentTypes typesList = new CommentTypes();
        typesList.setTitle("Wszystkie");
        typesList.setTypes(new ArrayList<CommentType>());

        // Create Child class object
        final CommentType type1 = new CommentType();
        type1.setDescription("Pozytywne");

        //Add Child class object to parent class object
        typesList.getTypes().add(type1);
        // Create Child class object
        final CommentType type2 = new CommentType();
        type2.setDescription("Negatywne");

        //Add Child class object to parent class object
        typesList.getTypes().add(type2);

        // Create Child class object
        final CommentType type3 = new CommentType();
        type3.setDescription("Wszystkie");

        //Add Child class object to parent class object
        typesList.getTypes().add(type3);

        list.add(typesList);
        return list;
    }


    private void loadHosts(final ArrayList<CommentTypes> newTypesList)
    {
        if (newTypesList == null)
            return;

        commTypes = newTypesList;

        // Check for ExpandableListAdapter object
        if (listViewCommTypes.getExpandableListAdapter() == null)
        {
            //Create ExpandableListAdapter Object
            final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();

            // Set Adapter to ExpandableList Adapter
            listViewCommTypes.setAdapter(mAdapter);
        }
        else
        {
            // Refresh ExpandableListView data
            ((MyExpandableListAdapter)listViewCommTypes.getExpandableListAdapter()).notifyDataSetChanged();
        }
    }
    private void loadHosts(){
        if (listViewComm.getAdapter() == null)
        {
            //Create ExpandableListAdapter Object
            final OpinionsAdapter mAdapter = new OpinionsAdapter();

            // Set Adapter to ExpandableList Adapter
            listViewComm.setAdapter(mAdapter);
        }
        else
        {
            // Refresh ExpandableListView data
            ((OpinionsAdapter)listViewComm.getAdapter()).notifyDataSetChanged();
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
            inflater = LayoutInflater.from(getApplicationContext());
        }


        // This Function used to inflate parent rows view

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parentView)
        {
            final CommentTypes typesList = commTypes.get(groupPosition);

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
            final CommentTypes typesList = commTypes.get(groupPosition);
            final CommentType type = typesList.getTypes().get(childPosition);

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
            return commTypes.get(groupPosition).getTypes().get(childPosition);
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

            return commTypes.get(groupPosition);
        }

        @Override
        public int getGroupCount()
        {
            return commTypes.size();
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
            return ((commTypes == null) || commTypes.isEmpty());
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
