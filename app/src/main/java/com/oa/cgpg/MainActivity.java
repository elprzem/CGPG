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
import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLDatabaseInsert;
import com.oa.cgpg.dataOperations.XMLOpinionRateSend;

import com.oa.cgpg.dataOperations.createTestEntities;
import com.oa.cgpg.dataOperations.dataBaseHelper;
import com.oa.cgpg.dataOperations.dbOps;
import com.oa.cgpg.models.buildingEntity;
import com.oa.cgpg.models.opinionNetEntity;
import com.oa.cgpg.models.opinionRateNet;
import com.oa.cgpg.models.poiEntity;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends OrmLiteBaseActivity<dataBaseHelper>
        implements MapFragment.OnMapFragmentListener,
        POIFragment.OnPOIFragmentListener, OpinionsFragment.OnOpinionsFragmentListener, AsyncResponse {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuTitles;
    private String[] mPOItypes;
    private dbOps dbOps;
    private createTestEntities testEntities;
    private dataBaseHelper dbHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = getHelper();
        dbOps = new dbOps(dbHelper);
/*
        for(poiEntity e : dbOps.getPois()){
            Log.i("POILSALSDAL",e.toString());
        }*/
        //   dbOps.clearData();
       //  testEntities = new createTestEntities(dbOps);
       //  testEntities.generateTemplateEntities();

        //to już jest w NewOpinionsFragment
        /*List<opinionNetEntity> list = new ArrayList<opinionNetEntity>();
        list.add(new opinionNetEntity(1,"dsa","dsadsa",5,5,3,4,5,new Date(312312)));
        list.add(new opinionNetEntity(23,"dsa","dsadsa",5,5,3,4,5,new Date(312312)));

        XMLOpinionSendParsing XOS = new XMLOpinionSendParsing(this,list);
        Log.i("dsasd", "sending xml");
        XOS.execute();*/

       /* String x = null;
        XMLParsing xmlPars = new XMLParsing(this, x);
        xmlPars.delegate = this;
        xmlPars.execute();*/
/*
        try {
            userNetEntity uE = new userNetEntity("la2233213la","12345","tom@gm213al.com",this,this);
            uE.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        /*List<opinionRateNet> list = new ArrayList<opinionRateNet>();
        list.add(new opinionRateNet(1,1,1));
        XMLOpinionRateSend ORS = new XMLOpinionRateSend(list, this);
        ORS.execute();
*/
/*
        for(int i:dbOps.getPoiIdByTypeId(dbOps.getTypeIdByName("XERO"))){
            System.out.println("POI ID "+i);
        }*/
/*
        for(int i:dbOps.getPoiIdByBuildingId(18)){
            System.out.println("POI ID "+i);
        }
*/
/*
        for(buildingEntity b : dbOps.getBuildingsByTypePOI(dbOps.getTypeIdByName("XERO"))){
            System.out.println(b.toString());
        }
*/
        mTitle = mDrawerTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mPOItypes = getResources().getStringArray(R.array.poi_types);
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
        getMenuInflater().inflate(R.menu.main, menu);
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
                XMLDatabaseInsert DI = new XMLDatabaseInsert(this,dbOps);
                DI.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void processFinish(String output) {
        //this you will received result fired from async class of onPostExecute(result) method.
        Log.i("async response: ", output);
        // System.out.print(output);
    }

    @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {

    }

    @Override
    public void startPOIFragment(Integer nrOnList, Integer id, String key) {
        Fragment fragment = new POIFragment();
        ((POIFragment) fragment).setDbOps(dbOps);
        Bundle args = new Bundle();

        //TODO Gdzie przekazać buildingId?
        //TODO Jak wołać ten fragemnt? Bez, czy z setDatabaseRef?
        if(key.equals(Keys.BUILDING_ID)) {
            args.putInt(key, 18);
            fragment.setArguments(args);
        }else if(key.equals(Keys.TYPE_POI)){
            args.putInt(key, id);
            args.putInt(Keys.NR_ON_LIST, nrOnList);
            fragment.setArguments(args);
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void startMapFragment(Integer value, String mode) {
        Fragment fragment = new MapFragment();
        Bundle args = new Bundle();
        if (mode.equals(Keys.TYPE_POI)) {
            args.putInt(Keys.TYPE_POI, value);
            fragment.setArguments(args);
        } else if (mode.equals(Keys.BUILDING_ID)) {
            args.putInt(Keys.BUILDING_ID, value);
            fragment.setArguments(args);
        }
        ((MapFragment) fragment).setDatabaseRef(dbOps);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_poi").commit();
    }

    @Override
    public void startOpinionsFragment(Integer idPOI, String titlePOI) {
        Fragment fragment = new OpinionsFragment();
        Bundle args = new Bundle();
        args.putInt(Keys.POI_NUMBER, idPOI);
        args.putString(Keys.POI_TITLE, titlePOI);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_poi").commit();
    }

    @Override
    public void startNewOpinionsFragment(Integer idPOI) {
        Fragment fragment = new NewOpinionsFragment();
        Bundle args = new Bundle();
        args.putInt(Keys.POI_NUMBER, idPOI);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_opinions").commit();
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

        if (position == MenuItems.MAP) {
            startMapFragment(0, Keys.CLEAR);
        }
        // nowy fragment - widok typu punktu usługowego (lista wszystkich punktów danego typu)
        else if (position >= MenuItems.XERO && position <= MenuItems.BIKES) {
            startPOIFragment(position, dbOps.getTypeIdByName(mPOItypes[position-1]), Keys.TYPE_POI);
        } else if (position == MenuItems.LOGIN) {//aktywność logowania lub rejestracji - info można przechować w klasie singleton
            Fragment fragment = new LoginFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("login_fragment").commit();
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
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else{
           getFragmentManager().popBackStack();
        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
