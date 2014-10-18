package com.oa.cgpg;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeSourceMapBitmap();
        offset = new Point(0,0);
        scale = 1.f;
    }

    private void initializeSourceMapBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        sourceMapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_from_wikimedia, options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initializeFragmentSize(view);
        initializeVisibleBitmap();

        mapImageView = (ImageView) view.findViewById(R.id.mapImageView);
        mapImageView.setOnTouchListener(new OnTouchMapListener());
//        mapImageView.setImageResource(R.drawable.image_from_wikimedia);

        mapImageView.setImageBitmap(visibleBitmap);

        return view;
    }

    private void initializeFragmentSize(View view) {
        int x = view.getWidth();
        int y = view.getHeight();
        fragmentSize = new Point(x,y);
    }

    private void initializeVisibleBitmap() {
        workingBitmap = Bitmap.createBitmap(
                sourceMapBitmap.getWidth(),
                sourceMapBitmap.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        workingBitmapCanvas = new Canvas(workingBitmap);
        workingBitmapCanvas.drawBitmap(sourceMapBitmap, new Matrix(), new Paint());

        //TODO whole stuff to draw on workingBitmap

        visibleBitmapCanvas = new Canvas(visibleBitmap);
        visibleBitmapCanvas.drawBitmap(
                workingBitmap,
                new Rect(offset.x, offset.y,
                        (int) (fragmentSize.x / scale), (int) (fragmentSize.y / scale)),
                new Rect(0,0, fragmentSize.x, fragmentSize.y),
                new Paint()
        );
    }

    private class OnTouchMapListener implements View.OnTouchListener{

        private PointF offsetDelta;
        private float oldDist;

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
                    return true;
                case MotionEvent.ACTION_UP:
                    Point newOffset = new Point(
                            (int)(offset.x + offsetDelta.x - event.getX()),
                            (int)(offset.y + offsetDelta.y - event.getY())
                    );
                    checkAndRedrawVisibleBitmap(newOffset);
                    return true;

                /**
                 * Code below inspired by http://stackoverflow.com/questions/10630373/android-image-view-pinch-zooming
                 */
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                            mapImageView.setImageMatrix(matrix);
                        }
                    }
                    return true;
                /**
                 * End. Next inspired code: spacing and midPoint
                 */

                default:
                    return false;
            }
        }
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
        if(newOffset.x < 0)
            newOffset.x = 0;
        else if(newOffset.x > fragmentSize.x)
            newOffset.x = fragmentSize.x;

        if(newOffset.y < 0)
            newOffset.y = 0;
        else if(newOffset.y > fragmentSize.y)
            newOffset.y = fragmentSize.y;

        offset = new Point(newOffset);

        redrawVisibleBitmap();
    }

    private void redrawVisibleBitmap() {
        visibleBitmapCanvas.drawBitmap(
                workingBitmap,
                new Rect(offset.x, offset.y,
                        (int) (fragmentSize.x / scale), (int) (fragmentSize.y / scale)),
                new Rect(0,0, fragmentSize.x, fragmentSize.y),
                new Paint()
        );
    }

}
