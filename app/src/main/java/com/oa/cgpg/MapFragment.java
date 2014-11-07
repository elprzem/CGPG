package com.oa.cgpg;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ZoomControls;

/** Fragment containing map
 *
 */
public class MapFragment extends Fragment {

    private static final int ZOOM = 1;
    private static final int DRAG = 2;
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

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeSourceMapBitmap();
        offset = new Point(0,0);
        scale =1.0f;
    }

    private void initializeSourceMapBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        sourceMapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.temp_pg_map, options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Log.i("viewDimensions", String.valueOf(view.getMeasuredWidth()) + " , " + String.valueOf(view.getMeasuredHeight()));

        initializeFragmentSize();

        if(getArguments() != null)//gdy fragment został otwarty w celu wyświetlenia pozycji na mapie wybranego rodzaju POI
        Log.i("type",String.valueOf(getArguments().getInt("type")));

        initializeVisibleBitmap();
        mapImageView = (ImageView) (view.findViewById(R.id.mapImageView));
        mapImageView.setOnTouchListener(new OnTouchMapListener());
//        mapImageView.setImageResource(R.drawable.image_from_wikimedia);

        mapImageView.setImageBitmap(visibleBitmap);

        ZoomControls zoomControls = (ZoomControls) view.findViewById(R.id.zoomControls);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scale *= ZOOM_FACTOR;
                Point newOffset = new Point(
                        (int) (offset.x + (fragmentSize.x * (ZOOM_FACTOR - 1)) / (2 * scale)),
                        (int) (offset.y + (fragmentSize.y * (ZOOM_FACTOR - 1)) / (2 * scale)));
                checkAndRedrawVisibleBitmap(newOffset);
            }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scale /= ZOOM_FACTOR;
                Point newOffset = new Point(
                        (int) (offset.y - ((fragmentSize.y * (ZOOM_FACTOR - 1)) / (2 * ZOOM_FACTOR * scale))),
                        (int) (offset.y - ((fragmentSize.y * (ZOOM_FACTOR - 1)) / (2 * ZOOM_FACTOR * scale))));
                checkAndRedrawVisibleBitmap(newOffset);
            }
        });

        return view;
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
        super.onResume();
    }

    private void initializeFragmentSize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int fragmentWidth  = displaymetrics.widthPixels;
        int fragmentHeight = displaymetrics.heightPixels;

        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        fragmentSize = new Point(fragmentWidth, fragmentHeight - actionBarSize);
    }

    private void initializeVisibleBitmap() {
        workingBitmap = Bitmap.createBitmap(
                sourceMapBitmap.getWidth(),
                sourceMapBitmap.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        workingBitmapCanvas = new Canvas(workingBitmap);
        workingBitmapCanvas.drawColor(Color.GREEN);
        workingBitmapCanvas.drawBitmap(sourceMapBitmap, new Matrix(), new Paint());

        //TODO whole stuff to draw on workingBitmap

        visibleBitmap = Bitmap.createBitmap(
                fragmentSize.x, fragmentSize.y,
                Bitmap.Config.ARGB_8888
        );
        visibleBitmapCanvas = new Canvas(visibleBitmap);
        visibleBitmapCanvas.drawColor(Color.BLUE);
        visibleBitmapCanvas.drawBitmap(
                workingBitmap,
                new Rect(offset.x, offset.y,
                        offset.x + (int) ((fragmentSize.x)/ scale),
                        offset.y + (int) ((fragmentSize.y)/ scale)),
                new Rect(0,0, fragmentSize.x, fragmentSize.y),
                new Paint()
        );
//        visibleBitmapCanvas.drawBitmap(workingBitmap,new Matrix(), new Paint());
    }

    private class OnTouchMapListener implements View.OnTouchListener{

        private PointF offsetDelta;
        private float oldDist;
        private View view;

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
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    offsetDelta = null;
                    offsetDelta = new PointF(event.getX(), event.getY());
                    Log.i("ACTION_DOWN", "offsetDelta: " + offsetDelta.toString());
                    return true;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    if(Math.abs(offsetDelta.x - event.getX()) < 5  && Math.abs(offsetDelta.y - event.getY()) < 5){
                        onClick((int) event.getX(), (int) event.getY());
                    }
                    else {
                        Point newOffset = new Point(
                                (int)(offset.x + (offsetDelta.x - event.getX()) / scale),
                                (int)(offset.y + (offsetDelta.y - event.getY()) / scale)
                        );
                        Log.i("ACTION_UP", "newoffset: " + newOffset.toString());
                        checkAndRedrawVisibleBitmap(newOffset);
                        Log.i("ACTION_UP", "offset: " + offset.toString());
                        offsetDelta = new PointF(event.getX(), event.getY());
                    }
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

    private void onClick(int x, int y) {
        Log.i("onClick", "x: " + x + " ,y: " + y);
        Integer placeId = whatIsHere(x, y);

        if(placeId == null)
            return;

//        PlaceDialog placeDialog = new
    }

    //TODO: I need data from database. Return type will be something like Place
    /// shape:
    //  __________
    //  |1       2|
    //  |         |
    //  |4       3|
    /// ----------
    private Integer whatIsHere(int x, int y) {
        /*
        for(Place place : places){
            if(x > place.getCoordinates().getFirstPoint().getX() &&
                    y < place.getCoordinates().getFirstPoint().getY() &&
                    x < place.getCoordinates().getSecondPoint().getX() &&
                    y < place.getCoordinates().getSecondPoint().getY() &&
                    x < place.getCoordinates().getThirdPoint().getX() &&
                    y > place.getCoordinates().getThirdPoint().getY() &&
                    x > place.getCoordinates().getFourthPoint().getX() &&
                    y > place.getCoordinates().getFourthPoint().getY()){
                return place.getId();
            }
        }
        */
        return null;
    }

    /**
     * Code below inspired by http://stackoverflow.com/questions/10630373/android-image-view-pinch-zooming
     */
    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * End of inspired code.
     */

    private void checkAndRedrawVisibleBitmap(Point newOffset) {
        if(newOffset.x <= 0)
            newOffset.x = 0;
        else if(newOffset.x > sourceMapBitmap.getWidth() - fragmentSize.x/scale){
            if(sourceMapBitmap.getWidth() > fragmentSize.x/scale){
                newOffset.x = (int) (sourceMapBitmap.getWidth() - fragmentSize.x/scale);
            }
            else {
                newOffset.x = 0;
            }
        }


        if(newOffset.y <= 0)
            newOffset.y = 0;
        else if(newOffset.y > sourceMapBitmap.getHeight() - fragmentSize.y/scale){
            if(sourceMapBitmap.getHeight() > fragmentSize.y/scale){
                newOffset.y = (int) (sourceMapBitmap.getHeight() - fragmentSize.y/scale);
            }
            else {
                newOffset.y = 0;
            }
        }

        offset = new Point(newOffset);

        redrawVisibleBitmap();
    }

    private void redrawVisibleBitmap() {
        visibleBitmapCanvas.drawColor(Color.BLUE);
        visibleBitmapCanvas.drawBitmap(
                workingBitmap,
                new Rect(offset.x, offset.y,
                        offset.x + (int) ((fragmentSize.x)/ scale),
                        offset.y + (int) ((fragmentSize.y)/ scale)),
                new Rect(0,0, fragmentSize.x, fragmentSize.y),
                new Paint()
        );
        Log.d("redrawVisibleBitmap", "scale: " + scale);
        mapImageView.setImageBitmap(visibleBitmap);
    }

}
