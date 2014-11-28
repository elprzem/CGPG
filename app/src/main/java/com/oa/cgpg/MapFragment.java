package com.oa.cgpg;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.oa.cgpg.dataOperations.dbOps;
import com.oa.cgpg.models.buildingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment containing map
 */
public class MapFragment extends Fragment {

    private OnMapFragmentListener listener;

    private static final int ZOOM = 4;
    private static final int DRAG = 2;
    private static final String TEST_TAG = "testTag";
    private ImageView mapImageView;
    private Point fragmentSize;
    private Point offset;
    private Bitmap sourceMapBitmap;
    private Bitmap workingBitmap;
    private Bitmap visibleBitmap;
    private Canvas workingBitmapCanvas;
    private Canvas visibleBitmapCanvas;
    private float scale;//newSize = scale * oldSize
    private Matrix savedMatrix;
    private Matrix matrix;
    private PointF mid;
    private int mode;
    private final Float ZOOM_FACTOR = 1.2f;
    private dbOps database;
    private Dialog placeDialog;
    private Integer typePOI;
    private ArrayList<buildingEntity> buildingsList;

    private final int SIZE_OF_SQAURE = 60;
    private final int HEIGHT_OF_TRIANGLE = 40;
    //private final float RESCALE_OF_BITMAP = 1.f;//0.8920960698689956f;//0.8820960698689956f;
    private boolean tempTestVal;


    public void setDatabaseRef(dbOps database) {
        this.database = database;
        List<buildingEntity> list = database.getBuildings();
        Log.i("test", String.valueOf(list.size()));
        for(buildingEntity building : list) {
            Log.i("test", building.getDescription());
        }
    }

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TEST_TAG, "onAttach");
        try {
            listener = (OnMapFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMapFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TEST_TAG, "onCreate");

        tempTestVal = false;

        offset = new Point(0, 0);
        scale = 1.f;//0.4822530864197531f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TEST_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Log.i("viewDimensions", String.valueOf(view.getMeasuredWidth()) + " , " + String.valueOf(view.getMeasuredHeight()));

        initializeFragmentSize();

        typePOI = null;

        if (getArguments() != null) {
            //gdy fragment został otwarty w celu wyświetlenia pozycji na mapie wybranego rodzaju POI
            Log.i("type", String.valueOf(getArguments().getInt("type")));
            if (getArguments().containsKey(Keys.TYPE_POI)) {
                typePOI = getArguments().getInt(Keys.TYPE_POI);
                //TODO uncomment when method and data in database are prepared
                buildingsList = new ArrayList<buildingEntity>(database.getBuildingsByTypePOI(typePOI));
            } else if (getArguments().containsKey(Keys.BUILDING_ID)) {
                buildingsList = new ArrayList<buildingEntity>();
                buildingsList.add(database.getBuildingById(
                        getArguments().getInt(Keys.BUILDING_ID)
                ));
            }
        }else{
            getActivity().setTitle("Mapa kampusu");
        }

        initializeVisibleBitmap();
        mapImageView = (ImageView) (view.findViewById(R.id.mapImageView));
        mapImageView.setOnTouchListener(new OnTouchMapListener());
//        mapImageView.setImageResource(R.drawable.image_from_wikimedia);

        mapImageView.setImageBitmap(visibleBitmap);

        ZoomControls zoomControls = (ZoomControls) view.findViewById(R.id.zoomControls);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scale < 2){
                    scale *= ZOOM_FACTOR;
                    Point newOffset = new Point(
                            (int) (offset.x + (fragmentSize.x * (ZOOM_FACTOR - 1)) / (2 * scale)),
                            (int) (offset.y + (fragmentSize.y * (ZOOM_FACTOR - 1)) / (2 * scale)));
                    checkAndRedrawVisibleBitmap(newOffset);
                }
            }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scale > 0.3){
                    scale /= ZOOM_FACTOR;
                    Point newOffset = new Point(
                            (int) (offset.x - ((fragmentSize.x * (ZOOM_FACTOR - 1)) / (2 * ZOOM_FACTOR * scale))),
                            (int) (offset.y - ((fragmentSize.y * (ZOOM_FACTOR - 1)) / (2 * ZOOM_FACTOR * scale))));
                    checkAndRedrawVisibleBitmap(newOffset);
                }
            }
        });
        return view;
    }

    private void initializeFragmentSize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int fragmentWidth = displaymetrics.widthPixels;
        int fragmentHeight = displaymetrics.heightPixels;

        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        fragmentSize = new Point(fragmentWidth, fragmentHeight - actionBarSize);
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(android.os.Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TEST_TAG, "onResume");
        super.onResume();

        if(workingBitmap == null) {
            initializeVisibleBitmap();
            mapImageView = (ImageView) (getView().findViewById(R.id.mapImageView));
            mapImageView.setOnTouchListener(new OnTouchMapListener());

            mapImageView.setImageBitmap(visibleBitmap);
        }
    }

    private void initializeSourceMapBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inScaled = false;
        sourceMapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kampus, options);
    }

    private void initializeVisibleBitmap() {
        if(sourceMapBitmap == null){
            initializeSourceMapBitmap();
        }

        workingBitmap = Bitmap.createBitmap(
                sourceMapBitmap.getWidth(),
                sourceMapBitmap.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        workingBitmapCanvas = new Canvas(workingBitmap);
        //workingBitmapCanvas.drawColor(Color.GREEN);
        workingBitmapCanvas.drawBitmap(sourceMapBitmap, new Matrix(), new Paint());

        //TODO I wait for data in database
        //buildingsList = new ArrayList<buildingEntity>(database.getBuildings());
        /*buildingsList.add(new buildingEntity(0, "name", "description",
                50, 100, 150, 110,
                130, 250,
                30, 220,
                ""));*/
        //TODO whole stuff to draw on workingBitmap
        //drawPoints();
        drawMarks();

        visibleBitmap = Bitmap.createBitmap(
                fragmentSize.x, fragmentSize.y,
                Bitmap.Config.ARGB_8888
        );
        visibleBitmapCanvas = new Canvas(visibleBitmap);
        visibleBitmapCanvas.drawColor(Color.WHITE);
        visibleBitmapCanvas.drawBitmap(
                workingBitmap,
                new Rect(offset.x, offset.y,
                        offset.x + (int) ((fragmentSize.x) / scale),
                        offset.y + (int) ((fragmentSize.y) / scale)),
                new Rect(0, 0, fragmentSize.x, fragmentSize.y),
                new Paint()
        );
//        visibleBitmapCanvas.drawBitmap(workingBitmap,new Matrix(), new Paint());
    }

    private void drawPoints() {
        //if(buildingsList != null && buildingsList.size() > 0){
            for(buildingEntity building : database.getBuildings()){

            //buildingEntity building;
            //if((building = database.getBuildingById(20)) != null) {

                Path path = new Path();
                //path.setFillType(Path.FillType.WINDING);
                path.moveTo(building.getX1()/**scale*/, building.getY1()/**scale*/);
                path.lineTo(building.getX2()/**scale*/, building.getY2()/**scale*/);
                path.lineTo(building.getX3()/**scale*/, building.getY3()/**scale*/);
                path.lineTo(building.getX4()/**scale*/, building.getY4()/**scale*/);
                //path.close();

                Paint paint = new Paint();
                paint.setAlpha(130);
                paint.setStrokeWidth(5);

                paint.setColor(Color.GREEN);

                //workingBitmapCanvas.drawPath(path, paint);
                workingBitmapCanvas.drawPoint(building.getX1()/**scale*/, building.getY1()/**scale*/, paint);
                workingBitmapCanvas.drawPoint(building.getX2()/**scale*/, building.getY2()/**scale*/, paint);
                workingBitmapCanvas.drawPoint(building.getX3()/**scale*/, building.getY3()/**scale*/, paint);
                workingBitmapCanvas.drawPoint(building.getX4()/**scale*/, building.getY4()/**scale*/, paint);

                workingBitmapCanvas.drawLine(building.getX1()/**scale*/, building.getY1()/**scale*/,
                        building.getX2()/**scale*/, building.getY2()/**scale*/,
                        paint);
                workingBitmapCanvas.drawLine(building.getX2()/**scale*/, building.getY2()/**scale*/,
                        building.getX3()/**scale*/, building.getY3()/**scale*/,
                        paint);
                workingBitmapCanvas.drawLine(building.getX3()/**scale*/, building.getY3()/**scale*/,
                        building.getX4()/**scale*/, building.getY4()/**scale*/,
                        paint);
                workingBitmapCanvas.drawLine(building.getX4()/**scale*/, building.getY4()/**scale*/,
                        building.getX1()/**scale*/, building.getY1()/**scale*/,
                        paint);
            }
        //}
    }

    //TODO poprawic brak czulosci na zoom, offset, co wazne przy cofaniu...
    private void drawMarks() {
        if (typePOI != null || (buildingsList != null && buildingsList.size() > 0)) {
            Point middlePointOfBuilding = new Point();

            Paint paint = new Paint();//Paint.ANTI_ALIAS_FLAG);

            paint.setStrokeWidth(2);
            paint.setARGB(255, 255, 177, 6);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            for (buildingEntity building : database.getBuildings()) {
                middlePointOfBuilding.set(
                        arithmeticAverage(building.getX1(), building.getX2(),
                                building.getX3(), building.getX4()),
                        arithmeticAverage(building.getY1(), building.getY2(),
                                building.getY3(), building.getY4())
                );

                workingBitmapCanvas.drawRect(
                        middlePointOfBuilding.x - SIZE_OF_SQAURE / 2,
                        middlePointOfBuilding.y - SIZE_OF_SQAURE - HEIGHT_OF_TRIANGLE,
                        middlePointOfBuilding.x + SIZE_OF_SQAURE / 2,
                        middlePointOfBuilding.y - HEIGHT_OF_TRIANGLE,
                        paint
                );

                Point point1_draw = new Point(
                        middlePointOfBuilding.x - SIZE_OF_SQAURE / 2,
                        middlePointOfBuilding.y - HEIGHT_OF_TRIANGLE);
                Point point2_draw = new Point(
                        middlePointOfBuilding.x + SIZE_OF_SQAURE / 2,
                        middlePointOfBuilding.y - HEIGHT_OF_TRIANGLE
                );
                Point point3_draw = new Point(
                        middlePointOfBuilding.x,
                        middlePointOfBuilding.y
                );

                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(point1_draw.x, point1_draw.y);
                path.lineTo(point2_draw.x, point2_draw.y);
                path.lineTo(point3_draw.x, point3_draw.y);
                path.lineTo(point1_draw.x, point1_draw.y);
                path.close();

                workingBitmapCanvas.drawPath(path, paint);
/*
                Paint pointPaint = new Paint();
                pointPaint.setColor(Color.YELLOW);
                pointPaint.setStrokeWidth(10);
                workingBitmapCanvas.drawPoint(
                        middlePointOfBuilding.x, middlePointOfBuilding.y, pointPaint);

                workingBitmapCanvas.drawText(building.getName(),
                        middlePointOfBuilding.x,
                        middlePointOfBuilding.y,
                        new Paint());
                */
            }
        }
    }

    private int arithmeticAverage(int a, int b, int c, int d) {
        return (a + b + c + d) / 4;
    }

    private class OnTouchMapListener implements View.OnTouchListener {

        private PointF offsetDelta;
        private float oldDist;
        private View view;
        private PointF offsetDeltaForOnClick;

        /**
         * Called when a touch event is dispatched to a view. This allows listeners to
         * get a chance to respond before the target view.
         *
         * @param v     The view the touch event has been dispatched to.
         * @param event The MotionEvent object containing full information about
         *              the event.
         * @return True if the listener has consumed the event, false otherwise.
         */

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    offsetDelta = null;
                    offsetDelta = new PointF(event.getX(), event.getY());
                    offsetDeltaForOnClick = null;
                    offsetDeltaForOnClick = new PointF(event.getX(), event.getY());

                    Log.i("ACTION_DOWN", "offsetDelta: " + offsetDelta.toString());
                    return true;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(offsetDeltaForOnClick.x - event.getX()) < 7
                            && Math.abs(offsetDeltaForOnClick.y - event.getY()) < 7) {
                        Log.i("ACTION_UP", String.valueOf(offsetDeltaForOnClick.x - event.getX()));
                        onClick(event.getX(), event.getY());
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    Point newOffset = new Point(
                            (int) (offset.x + (offsetDelta.x - event.getX()) / scale),
                            (int) (offset.y + (offsetDelta.y - event.getY()) / scale)
                    );
                    Log.i("ACTION_MOVE", "newoffset: " + newOffset.toString());
                    checkAndRedrawVisibleBitmap(newOffset);
                    Log.i("ACTION_MOVE", "offset: " + offset.toString());
                    offsetDelta = new PointF(event.getX(), event.getY());
                    return true;

                ///
                ///Code below inspired by http://stackoverflow.com/questions/10630373/android-image-view-pinch-zooming
                ///

                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        //savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    Log.i("ACTION_POINTER_DOWN", "oldDist: " + oldDist);
                    return true;
                /*
                case MotionEvent.ACTION_MOVE:
                    if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            //matrix.set(savedMatrix);
                            scale = newDist / oldDist;
                            //matrix.postScale(scale, scale, mid.x, mid.y);
                            //mapImageView.setImageMatrix(matrix);
                        }
                        Log.i("ACTION_MOVE", "newDist: " + newDist);
                    }
                    return true;
                    */
                ///
                ///End. Next inspired code: spacing and midPoint
                ///

                default:
                    return false;
            }
        }
    }

    private void onClick(float x, float y) {
        if (database != null) {// && !placeDialog.isShowing()){
            Log.d(TEST_TAG + "_onClick", "x: " + x + " ,y: " + y);
            buildingEntity building = null;

            PointF cords = calculateCords(x, y);
            Log.d(TEST_TAG + "_onClick", "x = " + cords.x + "; y = " + cords.y);

            try {
                building = database.getBuildingById(database.getIdOfBuildingByCords((int) cords.x, (int) cords.y));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //TODO only temporary line below
            //building = buildingsList.get(0);

            if (building == null){
                building = whatIsHere(cords.x,cords.y);
                if(building == null)
                    return;
            }

            Log.d(TEST_TAG, building.toString());

            String buildingName = building.getName();
            String buildingDescription = building.getDescription();
            //        final PlaceDialog placeDialog = new PlaceDialog(getActivity(), buildingName, buildingDescription);

            placeDialog = new Dialog(getActivity());
            WindowManager.LayoutParams wmlp = placeDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.TOP | Gravity.START;
            wmlp.x = (int) x;   //x position
            wmlp.y = (int) y;   //y position
            placeDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            placeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ColorDrawable colorDrawable = new ColorDrawable(Color.WHITE);
            colorDrawable.setAlpha(130);
            placeDialog.getWindow().setBackgroundDrawable(colorDrawable);

            placeDialog.setContentView(R.layout.place_dialog);
            ((TextView) placeDialog.findViewById(R.id.txt_title)).setText(buildingName);
            ((TextView) placeDialog.findViewById(R.id.txtDescription)).setText(buildingDescription);

            final Integer finalPlaceId = building.getIdBuilding();
            final Button showListButton = (Button) placeDialog.findViewById(R.id.btn_show_list);
            showListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("finalPlaceId", String.valueOf(finalPlaceId));
                    // placeDialog.hide();
                    placeDialog.dismiss();
                    listener.startPOIFragment(0, finalPlaceId, Keys.BUILDING_ID);
                }
            });
            placeDialog.show();

        }
    }

    private PointF calculateCords(float x, float y) {
        x /= scale;
        x += offset.x;

        y /= scale;
        y += offset.y;

        return new PointF(x,y);
    }

    /// shape:
    //  __________
    //  |1       2|
    //  |         |
    //  |4       3|
    /// ----------
    private buildingEntity whatIsHere(float x, float y) {
        if (database != null) {
            for (buildingEntity building : database.getBuildings()) {

                tempTestVal = false;
                if(building.getName().equals("Budynek Wydziału Zarządzania i Ekonomii")){
                    Log.d(TEST_TAG, building.toString());
                    Log.d(TEST_TAG, "x, y: (" + x + ", " + y + ")");
                    tempTestVal = true;
                }

                //Log.d(TEST_TAG, building.toString());
                if(isPointBetweenLines(x, y,
                        building.getX1(), building.getY1(),
                        building.getX2(), building.getY2(),
                        building.getX3(), building.getY3())){
                    if(isPointBetweenLines(x, y,
                            building.getX2(), building.getY2(),
                            building.getX3(), building.getY3(),
                            building.getX4(), building.getY4())){
                        if(isPointBetweenLines(x, y,
                                building.getX3(), building.getY3(),
                                building.getX4(), building.getY4(),
                                building.getX1(), building.getY1())){
                            if(isPointBetweenLines(x, y,
                                    building.getX4(), building.getY4(),
                                    building.getX1(), building.getY1(),
                                    building.getX2(), building.getY2())){

                                tempTestVal = false;
                                return building;
                            }
                        }
                    }
                }
            }
        }
        tempTestVal = false;
        return null;
    }

    private boolean isPointBetweenLines(float x, float y, int x1, int y1, int x2, int y2, int x3, int y3) {
        Double a = null;
        Double b = null;

        if (x2 - x1 != 0) {
            a =  ((double) (y2 - y1)) / ((double) (x2 - x1));
            b = ((double) y1) - ((double) x1) * a;
            if(tempTestVal)
                Log.d(TEST_TAG, "a, b: " + "(" + a + ", " + b + ")");
        }
        else if (tempTestVal)
            Log.d(TEST_TAG, "a, b: null");

        //It returns true for situation when three points are in one line.
        if(a != null){
            if(y3 < a * ((double) x3) + b){
                if(y < a * ((double) x) + b)
                    return true;
            } else if(y3 > a * ((double) x3) + b) {
                if(y > a * ((double) x) + b)
                    return true;
            } else{
                return true;
            }
        } else {
            if(x3 < x1) {
                if (x < x1)
                    return true;
            } else if(x3 > x1){
                if (x > x1)
                    return true;
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Code below inspired by http://stackoverflow.com/questions/10630373/android-image-view-pinch-zooming
     */
    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * End of inspired code.
     */

    private void checkAndRedrawVisibleBitmap(Point newOffset) {
        if(sourceMapBitmap == null){
            initializeSourceMapBitmap();
        }

        if (newOffset.x <= 0)
            newOffset.x = 0;
        else if (newOffset.x > sourceMapBitmap.getWidth() - fragmentSize.x / scale) {
            if (sourceMapBitmap.getWidth() > fragmentSize.x / scale) {
                newOffset.x = (int) (sourceMapBitmap.getWidth() - fragmentSize.x / scale);
            } else {
                newOffset.x = 0;
            }
        }


        if (newOffset.y <= 0)
            newOffset.y = 0;
        else if (newOffset.y > sourceMapBitmap.getHeight() - fragmentSize.y / scale) {
            if (sourceMapBitmap.getHeight() > fragmentSize.y / scale) {
                newOffset.y = (int) (sourceMapBitmap.getHeight() - fragmentSize.y / scale);
            } else {
                newOffset.y = 0;
            }
        }

        offset = new Point(newOffset);

        redrawVisibleBitmap();
    }

    private void redrawVisibleBitmap() {
        visibleBitmapCanvas.drawColor(Color.WHITE);
        visibleBitmapCanvas.drawBitmap(
                workingBitmap,
                new Rect(offset.x, offset.y,
                        offset.x + (int) ((fragmentSize.x) / scale),
                        offset.y + (int) ((fragmentSize.y) / scale)),
                new Rect(0, 0, fragmentSize.x, fragmentSize.y),
                new Paint()
        );
//        Log.d("redrawVisibleBitmap", "scale: " + scale);
        mapImageView.setImageBitmap(visibleBitmap);
    }

    @Override
    public void onStop() {
        super.onStop();

        sourceMapBitmap.recycle();
        sourceMapBitmap = null;
        visibleBitmap.recycle();
        visibleBitmap = null;
        visibleBitmapCanvas = null;
        workingBitmap.recycle();
        workingBitmap = null;
        workingBitmapCanvas = null;

        Log.i(TEST_TAG, "onStop");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        listener = null;
        /*sourceMapBitmap.recycle();
        sourceMapBitmap = null;
        visibleBitmap.recycle();
        visibleBitmap = null;
        visibleBitmapCanvas = null;
        workingBitmap.recycle();
        workingBitmap = null;
        workingBitmapCanvas = null;*/
        database = null;

        Log.i(TEST_TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //listener = null;
    }

    public interface OnMapFragmentListener {
        void startPOIFragment(Integer nrOnList, Integer buildingId, String key);
    }

}
