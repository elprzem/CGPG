package com.oa.cgpg;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.oa.cgpg.connectivity.Connectivity;
import com.oa.cgpg.customControls.LogOutDialog;
import com.oa.cgpg.customControls.NoConnectionDialog;
import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLDatabaseInsert;

import com.oa.cgpg.dataOperations.createTestEntities;
import com.oa.cgpg.dataOperations.dataBaseHelper;
import com.oa.cgpg.dataOperations.dbOps;
import com.oa.cgpg.models.buildingEntity;
import com.oa.cgpg.models.opinionNetEntity;
import com.oa.cgpg.models.poiEntity;
import com.oa.cgpg.models.userNetEntity;

import java.util.List;

public class MainActivity extends OrmLiteBaseActivity<dataBaseHelper>
        implements MapFragment.OnMapFragmentListener,
        POIFragment.OnPOIFragmentListener, OpinionsFragment.OnOpinionsFragmentListener
        ,LoginFragment.OnLoginFragmentListener,LoggedFragment.OnLoggedFragmentListener, AsyncResponse {

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
        if (dbOps.getVersion().getVersionNumber() == 1){
            if (Connectivity.isNetworkAvailable(this)) {
                XMLDatabaseInsert DI = new XMLDatabaseInsert(this, dbOps);
                DI.execute();
            }else{
                NoConnectionDialog ncDialog = new NoConnectionDialog();
                ncDialog.setMessage("Wymagane jest połączenie z Internetem do pobrania danych");
                ncDialog.show(getFragmentManager(), "noConnection");
            }
        }

       /* try {
            userNetEntity user = new userNetEntity("tomuszom","8x2pqqjn",this,this);
            user.login();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

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
        if(LoggedUserInfo.getInstance().isLoggedIn())
            menu.getItem(menu.size()-1).setTitle("Wyloguj");
        else
            menu.getItem(menu.size()-1).setTitle("Zaloguj");
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
                if(Connectivity.isNetworkAvailable(this)) {
                    XMLDatabaseInsert DI = new XMLDatabaseInsert(this, dbOps);
                    DI.execute();
                }else{
                    NoConnectionDialog ncDialog = new NoConnectionDialog();
                    ncDialog.setMessage("Do aktualizacji danych wymagane jest połączenie z Interentem");
                    ncDialog.show(getFragmentManager(), "noConnection");
                }
                return true;
            case R.id.action_login:
                if(LoggedUserInfo.getInstance().isLoggedIn()) {
                    LogOutDialog dialog = new LogOutDialog();
                    dialog.show(getFragmentManager(), "log_out");
                }else {
                    startLoginFragment();
                }

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
    public void processFinish(userNetEntity output) {
        Log.i("LOGOWANIE ", output.toString());
    }

    @Override
    public void processFinishOpinion(List<opinionNetEntity> list) {

    }

    @Override
    public void startPOIFragment(Integer nrOnList, Integer id, String key) {
        Fragment fragment = new POIFragment();
        ((POIFragment) fragment).setDbOps(dbOps);
        Bundle args = new Bundle();
        FragmentManager fragmentManager = getFragmentManager();
        if (key.equals(Keys.BUILDING_ID)) {
            args.putInt(key, 18);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_poi").commit();
            fragment.setArguments(args);
        } else if (key.equals(Keys.TYPE_POI)) {
            args.putInt(key, id);
            args.putInt(Keys.NR_ON_LIST, nrOnList);
            fragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    @Override
    public void startMapFragment(Integer value, String mode) {
        Fragment fragment = new MapFragment();
        ((MapFragment) fragment).setDatabaseRef(dbOps);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        if (mode.equals(Keys.TYPE_POI)) {
            args.putInt(Keys.TYPE_POI, value);
            fragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_map").commit();
        } else if (mode.equals(Keys.BUILDING_ID)) {
            args.putInt(Keys.BUILDING_ID, value);
            fragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_map").commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }


    }

    @Override
    public void startOpinionsFragment(boolean addToBackStack, Integer idPOI, String titlePOI) {
        Fragment fragment = new OpinionsFragment();
        Bundle args = new Bundle();
        args.putInt(Keys.POI_NUMBER, idPOI);
        args.putString(Keys.POI_TITLE, titlePOI);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        if(addToBackStack)
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_poi").commit();
        else
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
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

    @Override
    public void startLoggedFragment(){
        mMenuTitles[MenuItems.LOGIN] = "Profil";
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        Fragment fragment = new LoggedFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void startRegisterFragment() {
        Fragment fragment = new RegisterFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_login").commit();
    }
    @Override
    public void startEditUserFragment(String username, String email){
        Fragment fragment = new EditUserFragment();
        Bundle args = new Bundle();
        args.putString(Keys.USER_NAME, username);
        args.putString(Keys.EMAIL, email);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("fragment_logged").commit();
    }
    public void startLoginFragment() {
        mMenuTitles[MenuItems.LOGIN] = "Logowanie";
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        LoggedUserInfo.getInstance().setLoggedIn(false);
        LoggedUserInfo.getInstance().setUserName("");
        Fragment fragment = new LoginFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
    public void startLoginFragment(boolean toOpinions, Integer poiId,String poiTitle){
        mMenuTitles[MenuItems.LOGIN] = "Logowanie";
        mDrawerList.setItemChecked(MenuItems.LOGIN, true);
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        LoggedUserInfo.getInstance().setLoggedIn(false);
        LoggedUserInfo.getInstance().setUserName("");
        Fragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putBoolean(Keys.TO_OPINIONS, toOpinions);
        args.putInt(Keys.POI_NUMBER, poiId);
        args.putString(Keys.POI_TITLE, poiTitle);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
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

        FragmentManager fm = getFragmentManager();
        int count = fm.getBackStackEntryCount();
        for(int i = 0; i < count; ++i) {
            fm.popBackStackImmediate();
        }

        if (position == MenuItems.MAP) {
            startMapFragment(0, Keys.CLEAR);
        }
        // nowy fragment - widok typu punktu usługowego (lista wszystkich punktów danego typu)
        else if (position >= MenuItems.XERO && position <= MenuItems.BIKES) {
            startPOIFragment(position, dbOps.getTypeIdByName(mPOItypes[position - 1]), Keys.TYPE_POI);
        } else if (position == MenuItems.LOGIN) {//aktywność logowania lub rejestracji - info można przechować w klasie singleton
            if(LoggedUserInfo.getInstance().isLoggedIn()){
               startLoggedFragment();

            }else {
                startLoginFragment();
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    public void highlightMenuItem(int position){
        mDrawerList.setItemChecked(position, true);
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
        Log.i("back stack entry", String.valueOf(getFragmentManager().getBackStackEntryCount()));
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (getFragmentManager().getBackStackEntryCount() == 0) {
            if(getFragmentManager().findFragmentById(R.id.content_frame) instanceof MapFragment){
                finish();
            }else{
                startMapFragment(0, Keys.CLEAR);
                highlightMenuItem(0);
            }
            Log.i("back", "from else if");
        } else {
            Log.i("back", "from else");
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
