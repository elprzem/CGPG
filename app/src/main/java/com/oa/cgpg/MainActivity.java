package com.oa.cgpg;
//Sobczak

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oa.cgpg.dataOperations.*;
import com.oa.cgpg.models.opinionNetEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends OrmLiteBaseActivity<dataBaseHelper>
    implements MapFragment.OnMapFragmentListener,
                POIFragment.OnPOIFragmentListener, OpinionsFragment.OnOpinionsFragmentListener{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuTitles;

    private dbOps dbOps;
    private createTestEntities testEntities;
    private dataBaseHelper dbHelper;

    private XMLParsing parser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = getHelper();
        dbOps = new dbOps(dbHelper);
     //   dbOps.clearData();
        testEntities = new createTestEntities(dbOps);
        testEntities.generateTemplateEntities();

        List<opinionNetEntity> list = new ArrayList<opinionNetEntity>();
        list.add(new opinionNetEntity(1,"dsa","dsadsa",5,5,3,4,5,new Date(312312)));
        list.add(new opinionNetEntity(23,"dsa","dsadsa",5,5,3,4,5,new Date(312312)));

        XMLOpinionSendParsing XOS = new XMLOpinionSendParsing(this,list);
        Log.i("dsasd", "sending xml");
        XOS.execute();

       /* String x = null;
        XMLParsing xmlPars = new XMLParsing(this, x);
        xmlPars.delegate = this;
        xmlPars.execute();*/

        mTitle = mDrawerTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_update:
                // TODO aktualizacja bazy

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void processFinish(String output){
        //this you will received result fired from async class of onPostExecute(result) method.
//        Log.i("async response: ","xml has come! length="+output.length());
    }

    @Override
    public void startPOIFragment(Integer buildingId) {
        Fragment fragment = new POIFragment();
        ((POIFragment)fragment).setDbOps(dbOps);
        Bundle args = new Bundle();

        //TODO Gdzie przekazać buildingId?
        //TODO Jak wołać ten fragemnt? Bez, czy z setDatabaseRef?
        args.putInt("buildingId", 1);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void startMapFragment(Integer typePOI) {
        Fragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt("type", typePOI);
        fragment.setArguments(args);
        ((MapFragment)fragment).setDatabaseRef(dbOps);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_poi").commit();
    }
    @Override
    public void startOpinionsFragment(Integer idPOI, String titlePOI){
        Fragment fragment = new OpinionsFragment();
        Bundle args = new Bundle();
        args.putInt("poiNr", idPOI);
        args.putString("poi", titlePOI);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_poi").commit();
    }
   /* @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {
        for(opinionNetEntity op : list){
            System.out.println(op.toString());
        }
    }*/

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    private void selectItem(int position) {
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

        if (position == MenuItems.MAP){
            Fragment fragment = new MapFragment();
            ((MapFragment)fragment).setDatabaseRef(dbOps);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
        // nowy fragment - widok typu punktu usługowego (lista wszystkich punktów danego typu)
        else if(position >= MenuItems.XERO && position <= MenuItems.BIKES){
            Fragment fragment = new POIFragment();
            ((POIFragment)fragment).setDbOps(dbOps);
            Bundle args = new Bundle();
            args.putInt("poiTypeId", position);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        }else if(position == MenuItems.LOGIN){//aktywność logowania lub rejestracji - info można przechować w klasie singleton
            Fragment fragment = new LoginFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
