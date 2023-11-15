package com.rivaldorendy.cardframecapture.cropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


public class CropOverlayView extends View {

    private int defaultMargin = 100;
    private int vertexSize = 30;
    private int gridSize = 3;

    private Bitmap bitmap;
    private Point topLeft, topRight, bottomLeft, bottomRight;

    private float touchDownX, touchDownY;
    private CropPosition cropPosition;

    private int currentWidth = 0;
    private int currentHeight = 0;

    private int minX, maxX, minY, maxY;

    public CropOverlayView(Context context) {
        super(context);
    }

    public CropOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        resetPoints();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() != currentWidth || getHeight() != currentHeight) {
            currentWidth = getWidth();
            currentHeight = getHeight();
            resetPoints();
        }

        drawBackground(canvas);
        drawVertex(canvas);
        drawEdge(canvas);
    }

    private void resetPoints() {
        float scaleX = bitmap.getWidth() * 1.0f / getWidth();
        float scaleY = bitmap.getHeight() * 1.0f / getHeight();
        float maxScale = Math.max(scaleX, scaleY);

        int minX = 0;
        int maxX = getWidth();
        int minY = 0;
        int maxY = getHeight();

        if (maxScale == scaleY) {
            int bitmapInCanvasWidth = (int) (bitmap.getWidth() / maxScale);
            minX = (getWidth() - bitmapInCanvasWidth) / 2;
            maxX = getWidth() - minX;
        } else {
            int bitmapInCanvasHeight = (int) (bitmap.getHeight() / maxScale);
            minY = (getHeight() - bitmapInCanvasHeight)/2;
            maxY = getHeight() - minY;
        }

        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        if (maxX - minX < defaultMargin || maxY - minY < defaultMargin)
            defaultMargin = 0;
        else
            defaultMargin = 30;

        topLeft = new Point(minX + defaultMargin, minY + defaultMargin);
        topRight = new Point(maxX - defaultMargin, minY + defaultMargin);
        bottomLeft = new Point(minX + defaultMargin, maxY - defaultMargin);
        bottomRight = new Point(maxX - defaultMargin, maxY - defaultMargin);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void drawBackground(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#66000000"));
        paint.setStyle(Paint.Style.FILL);

        Path path = new Path();
        path.moveTo(topLeft.x, topLeft.y);
        path.lineTo(topRight.x, topRight.y);
        path.lineTo(bottomRight.x, bottomRight.y);
        path.lineTo(bottomLeft.x, bottomLeft.y);
        path.close();

        canvas.save();
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#66000000"));
        canvas.restore();
    }

    private void drawVertex(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(topLeft.x, topLeft.y, vertexSize, paint);
        canvas.drawCircle(topRight.x, topRight.y, vertexSize, paint);
        canvas.drawCircle(bottomLeft.x, bottomLeft.y, vertexSize, paint);
        canvas.drawCircle(bottomRight.x, bottomRight.y, vertexSize, paint);
    }
    private void drawEdge(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);

        canvas.drawLine(topLeft.x, topLeft.y, topRight.x, topRight.y, paint);
        canvas.drawLine(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y, paint);
        canvas.drawLine(bottomRight.x, bottomRight.y, topRight.x, topRight.y, paint);
        canvas.drawLine(bottomRight.x, bottomRight.y, bottomLeft.x, bottomLeft.y, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(false);
                onActionDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                onActionMove(event);
                return true;
        }
        return false;
    }

    private void onActionDown(MotionEvent event) {
        touchDownX = event.getX();
        touchDownY = event.getY();
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());
        int minDistance = distance(touchPoint, topLeft);
        cropPosition = CropPosition.TOP_LEFT;
        if (minDistance > distance(touchPoint, topRight)) {
            minDistance = distance(touchPoint, topRight);
            cropPosition = CropPosition.TOP_RIGHT;
        }
        if (minDistance > distance(touchPoint, bottomLeft)) {
            minDistance = distance(touchPoint, bottomLeft);
            cropPosition = CropPosition.BOTTOM_LEFT;
        }
        if (minDistance > distance(touchPoint, bottomRight)) {
            minDistance = distance(touchPoint, bottomRight);
            cropPosition = CropPosition.BOTTOM_RIGHT;
        }
    }

    private int distance(Point src, Point dst) {
        return (int) Math.sqrt(Math.pow(src.x - dst.x, 2) + Math.pow(src.y - dst.y, 2));
    }

    private void onActionMove(MotionEvent event) {
        int deltaX = (int) (event.getX() - touchDownX);
        int deltaY = (int) (event.getY() - touchDownY);

        switch (cropPosition) {
            case TOP_LEFT:
                adjustTopLeft(deltaX, deltaY);
                invalidate();
                break;
            case TOP_RIGHT:
                adjustTopRight(deltaX, deltaY);
                invalidate();
                break;
            case BOTTOM_LEFT:
                adjustBottomLeft(deltaX, deltaY);
                invalidate();
                break;
            case BOTTOM_RIGHT:
                adjustBottomRight(deltaX, deltaY);
                invalidate();
                break;
        }
        touchDownX = event.getX();
        touchDownY = event.getY();
    }

    private void adjustTopLeft(int deltaX, int deltaY) {
        int newX = topLeft.x + deltaX;
        if (newX < minX) newX = minX;
        if (newX > maxX) newX = maxX;

        int newY = topLeft.y + deltaY;
        if (newY < minY) newY = minY;
        if (newY > maxY) newY = maxY;

        topLeft.set(newX, newY);
    }

    private void adjustTopRight(int deltaX, int deltaY) {
        int newX = topRight.x + deltaX;
        if (newX > maxX) newX = maxX;
        if (newX < minX) newX = minX;

        int newY = topRight.y + deltaY;
        if (newY < minY) newY = minY;
        if (newY > maxY) newY = maxY;

        topRight.set(newX, newY);
    }

    private void adjustBottomLeft(int deltaX, int deltaY) {
        int newX = bottomLeft.x + deltaX;
        if (newX < minX) newX = minX;
        if (newX > maxX) newX = maxX;

        int newY = bottomLeft.y + deltaY;
        if (newY > maxY) newY = maxY;
        if (newY < minY) newY = minY;

        bottomLeft.set(newX, newY);
    }

    private void adjustBottomRight(int deltaX, int deltaY) {
        int newX = bottomRight.x + deltaX;
        if (newX > maxX) newX = maxX;
        if (newX < minX) newX = minX;

        int newY = bottomRight.y + deltaY;
        if (newY > maxY) newY = maxY;
        if (newY < minY) newY = minY;

        bottomRight.set(newX, newY);
    }

    public void crop(CropListener cropListener, boolean needStretch) {
        if (topLeft == null) return;

        float scaleX = bitmap.getWidth() * 1.0f / getWidth();
        float scaleY = bitmap.getHeight() * 1.0f / getHeight();
        float maxScale = Math.max(scaleX, scaleY);

        Point bitmapTopLeft = new Point((int) ((topLeft.x - minX) * maxScale), (int) ((topLeft.y - minY) * maxScale));
        Point bitmapTopRight = new Point((int) ((topRight.x - minX) * maxScale), (int) ((topRight.y - minY) * maxScale));
        Point bitmapBottomLeft = new Point((int) ((bottomLeft.x - minX) * maxScale), (int) ((bottomLeft.y - minY) * maxScale));
        Point bitmapBottomRight = new Point((int) ((bottomRight.x - minX) * maxScale), (int) ((bottomRight.y - minY) * maxScale));

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth()+1, bitmap.getHeight()+1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Path path = new Path();
        path.moveTo(bitmapTopLeft.x, bitmapTopLeft.y);
        path.lineTo(bitmapTopRight.x, bitmapTopRight.y);
        path.lineTo(bitmapBottomRight.x, bitmapBottomRight.y);
        path.lineTo(bitmapBottomLeft.x, bitmapBottomLeft.y);
        path.close();
        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        Rect cropRect = new Rect(
                Math.min(bitmapTopLeft.x, bitmapBottomLeft.x),
                Math.min(bitmapTopLeft.y, bitmapTopRight.y),
                Math.max(bitmapBottomRight.x, bitmapTopRight.x),
                Math.max(bitmapBottomRight.y, bitmapBottomLeft.y));

        if(cropRect.width() <= 0 || cropRect.height() <= 0) {
            cropListener.onFinish(null);
            return;
        }
        Bitmap cut = Bitmap.createBitmap(
                output,
                cropRect.left,
                cropRect.top,
                cropRect.width(),
                cropRect.height()
        );

        if (!needStretch) {
            cropListener.onFinish(cut);
        } else {
            Point cutTopLeft = new Point();
            Point cutTopRight = new Point();
            Point cutBottomLeft = new Point();
            Point cutBottomRight = new Point();

            cutTopLeft.x = bitmapTopLeft.x > bitmapBottomLeft.x ? bitmapTopLeft.x - bitmapBottomLeft.x : 0;
            cutTopLeft.y = bitmapTopLeft.y > bitmapTopRight.y ? bitmapTopLeft.y - bitmapTopRight.y : 0;

            cutTopRight.x = bitmapTopRight.x > bitmapBottomRight.x ? cropRect.width() : cropRect.width() - Math.abs(bitmapBottomRight.x - bitmapTopRight.x);
            cutTopRight.y = bitmapTopLeft.y > bitmapTopRight.y ? 0 : Math.abs(bitmapTopLeft.y - bitmapTopRight.y);

            cutBottomLeft.x = bitmapTopLeft.x > bitmapBottomLeft.x ? 0 : Math.abs(bitmapTopLeft.x - bitmapBottomLeft.x);
            cutBottomLeft.y = bitmapBottomLeft.y > bitmapBottomRight.y ? cropRect.height() : cropRect.height() - Math.abs(bitmapBottomRight.y - bitmapBottomLeft.y);

            cutBottomRight.x = bitmapTopRight.x > bitmapBottomRight.x ? cropRect.width() - Math.abs(bitmapBottomRight.x - bitmapTopRight.x) : cropRect.width();
            cutBottomRight.y = bitmapBottomLeft.y > bitmapBottomRight.y ? cropRect.height() - Math.abs(bitmapBottomRight.y - bitmapBottomLeft.y) : cropRect.height();

            float width = cut.getWidth();
            float height = cut.getHeight();

            float[] src = new float[]{cutTopLeft.x, cutTopLeft.y, cutTopRight.x, cutTopRight.y, cutBottomRight.x, cutBottomRight.y, cutBottomLeft.x, cutBottomLeft.y};
            float[] dst = new float[]{0, 0, width, 0, width, height, 0, height};

            Matrix matrix = new Matrix();
            matrix.setPolyToPoly(src, 0, dst, 0, 4);
            Bitmap stretch = Bitmap.createBitmap(cut.getWidth(), cut.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas stretchCanvas = new Canvas(stretch);
            stretchCanvas.concat(matrix);
            stretchCanvas.drawBitmapMesh(cut, WIDTH_BLOCK, HEIGHT_BLOCK, generateVertices(cut.getWidth(), cut.getHeight()), 0, null, 0, null);

            cropListener.onFinish(stretch);
        }
    }

    private int WIDTH_BLOCK = 40;
    private int HEIGHT_BLOCK = 40;

    private float[] generateVertices(int widthBitmap, int heightBitmap) {

        float[] vertices=new float[(WIDTH_BLOCK+1)*(HEIGHT_BLOCK+1)*2];

        float widthBlock = (float)widthBitmap/WIDTH_BLOCK;
        float heightBlock = (float)heightBitmap/HEIGHT_BLOCK;

        for(int i=0;i<=HEIGHT_BLOCK;i++)
            for(int j=0;j<=WIDTH_BLOCK;j++) {
                vertices[i * ((HEIGHT_BLOCK+1)*2) + (j*2)] = j * widthBlock;
                vertices[i * ((HEIGHT_BLOCK+1)*2) + (j*2)+1] = i * heightBlock;
            }
        return vertices;
    }


}
