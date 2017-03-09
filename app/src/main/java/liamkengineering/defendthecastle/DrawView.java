package liamkengineering.defendthecastle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * This is a view for drawing to a canvas
 */
public class DrawView extends View {
    public int width;
    public  int height;
    private Bitmap mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;

    private void setPaint(Paint p) {
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(Color.BLACK); // default color
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(12);
    }


    public DrawView(Context c) {
        super(c);
        context=c;
        setFocusable(true);
        setFocusableInTouchMode(true);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        circlePaint = new Paint();
        circlePath = new Path();
        setPaint(circlePaint);
        setPaint(mPaint);
        setPaint(mBitmapPaint);
        setDrawingCacheEnabled(true);
    }


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusableInTouchMode(true);
        setFocusable(true);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        circlePaint = new Paint();
        circlePath = new Path();
        setPaint(circlePaint);
        setPaint(mPaint);
        setPaint(mBitmapPaint);
        setDrawingCacheEnabled(true);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusableInTouchMode(true);
        setFocusable(true);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        circlePaint = new Paint();
        circlePath = new Path();
        setPaint(circlePaint);
        setPaint(mPaint);
        setPaint(mBitmapPaint);
        setDrawingCacheEnabled(true);
    }
    public void setPaintColor(int col) {
        mPaint.setColor(col);
        circlePaint.setColor(col);
        mBitmapPaint.setColor(col);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath,  mPaint);
        canvas.drawPath(circlePath,  circlePaint);

    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
    // clear the canvas
    public void reset() {
        setDrawingCacheEnabled(false);
        onSizeChanged(width, height, width, height);
        invalidate();
        setDrawingCacheEnabled(true);
    }
}

