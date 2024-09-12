package com.hfad.cs426_final_project.PlantingScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class HexagonalLandView extends View {
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 3.0f;
    private static final float ZOOM_FACTOR = 1.2f;

    private static final float TILE_SIZE = 100f;
    private static final float HORIZONTAL_SPACING = TILE_SIZE * 0.76f;
    private static final float VERTICAL_SPACING = TILE_SIZE * 0.41f;
    private static final float LAND_SPACING = 0.84f;
    private static final String TILES_KEY = "tiles-tiles";
    private boolean hasUnsavedChanges = false;
    private boolean isPlantingMode = true;

    private Map<Coordinate, TileType> tiles = new HashMap<>();
    private Bitmap normalTile, plusTile;
    private float offsetX, offsetY;
    private float scaleFactor = 1f;
    private Matrix matrix = new Matrix();

    private ScaleGestureDetector scaleDetector;
    private float lastTouchX, lastTouchY;
    private static final int INVALID_POINTER_ID = -1;
    private int activePointerId = INVALID_POINTER_ID;
    private DatabaseReference tilesRef;
    private float touchStartX, touchStartY;
    private static final float CLICK_TOLERANCE = 10f;

    private static final long ANIMATION_DURATION = 1000; // 1 second for full cycle
    private static final float MAX_BORDER_WIDTH = 8f; // Maximum border width in pixels
    private long animationStartTime;
    private Paint borderPaint;
    private Path hexagonPath;

    public HexagonalLandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tilesRef = database.getReference(TILES_KEY);

        Bitmap originalNormalTile = BitmapFactory.decodeResource(getResources(), R.drawable.piece);
        normalTile = Bitmap.createScaledBitmap(originalNormalTile, (int)TILE_SIZE, (int)TILE_SIZE, true);
        originalNormalTile.recycle();

        Bitmap originalPlusTile = BitmapFactory.decodeResource(getResources(), R.drawable.plus_btn);
        plusTile = Bitmap.createScaledBitmap(originalPlusTile, (int)TILE_SIZE, (int)TILE_SIZE, true);
        originalPlusTile.recycle();

        offsetX = offsetY = 0f;
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        loadTiles();

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(ContextCompat.getColor(context, R.color.primary_20));
        animationStartTime = System.currentTimeMillis();
        hexagonPath = new Path();

        // Start the animation
        postInvalidateOnAnimation();
    }

    private void loadTiles() {
        tilesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tiles.clear();
                for (DataSnapshot tileSnapshot : dataSnapshot.getChildren()) {
                    String[] coords = tileSnapshot.getKey().split(",");
                    if (coords.length == 2) {
                        int q = Integer.parseInt(coords[0]);
                        int r = Integer.parseInt(coords[1]);
                        Coordinate coord = new Coordinate(q, r);
                        Log.d("Bugga", "Loading tile at " + coord.q + "," + coord.r);
                        String tileTypeString = tileSnapshot.getValue(String.class);
                        TileType tileType = TileType.valueOf(tileTypeString);
                        tiles.put(coord, tileType);
                    }
                }
                if (tiles.isEmpty()) {
                    // Initialize with center tile if no saved state
                    Coordinate center = new Coordinate(0, 0);
                    tiles.put(center, TileType.NORMAL);
                    addSurroundingPlusTiles(center);
                }
                invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }


    public void setPlantingMode(boolean plantingMode) {
        isPlantingMode = plantingMode;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scaleFactor, scaleFactor);

        float centerX = getWidth() / 2f / scaleFactor;
        float centerY = getHeight() / 2f / scaleFactor;

        float animationProgress = (System.currentTimeMillis() - animationStartTime) % ANIMATION_DURATION / (float) ANIMATION_DURATION;
        float currentBorderWidth = MAX_BORDER_WIDTH * Math.abs(animationProgress - 0.5f) * 2;
        borderPaint.setStrokeWidth(currentBorderWidth / scaleFactor);

        // Sort coordinates from top-left to bottom-right
        List<Map.Entry<Coordinate, TileType>> sortedTiles = new ArrayList<>(tiles.entrySet());
        Collections.sort(sortedTiles, (a, b) -> {
            if (a.getKey().r != b.getKey().r) {
                return Integer.compare(a.getKey().r, b.getKey().r);
            }
            return Integer.compare(a.getKey().q, b.getKey().q);
        });

        for (Map.Entry<Coordinate, TileType> entry : sortedTiles) {
            Coordinate coord = entry.getKey();
            TileType type = entry.getValue();

            float x = centerX + coord.q * HORIZONTAL_SPACING;
            float y = centerY + (coord.r * 2f + coord.q) * VERTICAL_SPACING;

            if (type == TileType.PLUS && !isPlantingMode) {
                continue;
            }

            Bitmap tileToDraw = (type == TileType.NORMAL) ? normalTile : plusTile;
            matrix.setTranslate(x - TILE_SIZE / 2, y - TILE_SIZE / 2);
            canvas.drawBitmap(tileToDraw, matrix, null);

            if (type == TileType.PLUS && isPlantingMode) {
                drawHexagonBorder(canvas, x, y-6f, 50f, borderPaint);
            }
        }

        canvas.restore();
        postInvalidateOnAnimation();
    }

    private void drawHexagonBorder(Canvas canvas, float centerX, float centerY, float size, Paint paint) {
        hexagonPath.reset();
        for (int i = 0; i < 6; i++) {
            float angle_deg = 60 * i;
            float angle_rad = (float) (Math.PI / 180 * angle_deg);
            float x = (float) (centerX + size * Math.cos(angle_rad));
            float y = (float) (centerY + size * Math.sin(angle_rad));
            if (i == 0) {
                hexagonPath.moveTo(x, y);
            } else {
                hexagonPath.lineTo(x, y);
            }
        }
        hexagonPath.close();
        canvas.drawPath(hexagonPath, paint);
    }

    private void markUnsavedChanges() {
        hasUnsavedChanges = true;
    }

    // Method to save all tiles
    public void saveAllTiles() {
        Map<String, Object> updates = new HashMap<>();
        if (hasUnsavedChanges) {
            for (Map.Entry<Coordinate, TileType> entry : tiles.entrySet()) {
                Coordinate coord = entry.getKey();
                TileType tileType = entry.getValue();
                updates.put(coord.q + "," + coord.r, tileType.name());
            }
            tilesRef.updateChildren(updates);
            hasUnsavedChanges = false;
        }
    }

    private void addSurroundingPlusTiles(Coordinate center) {
        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        for (int[] dir : directions) {
            Coordinate newCoord = new Coordinate(center.q + dir[0], center.r + dir[1]);
            if (!tiles.containsKey(newCoord)) {
                tiles.put(newCoord, TileType.PLUS);
                markUnsavedChanges();
                saveAllTiles();
            }
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isPlantingMode) {
            return handlePanAndZoom(ev);
        }

        scaleDetector.onTouchEvent(ev);

        int action = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchStartX = ev.getX();
                touchStartY = ev.getY();
                lastTouchX = touchStartX;
                lastTouchY = touchStartY;
                activePointerId = ev.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                if (!scaleDetector.isInProgress()) {
                    final float dx = x - lastTouchX;
                    final float dy = y - lastTouchY;

                    offsetX += dx;
                    offsetY += dy;
                    invalidate();
                }

                lastTouchX = x;
                lastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP:
                if (isClick(ev.getX(), ev.getY())) {
                    handleTileConversion(ev.getX(), ev.getY());
                }
            case MotionEvent.ACTION_CANCEL:
                activePointerId = INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastTouchX = ev.getX(newPointerIndex);
                    lastTouchY = ev.getY(newPointerIndex);
                    activePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    private void handleTileConversion(float x, float y) {
        float scaledX = (x - offsetX) / scaleFactor;
        float scaledY = (y - offsetY) / scaleFactor;
        Coordinate tappedCoord = pixelToHex(scaledX, scaledY);
        Log.d("taggedCoord", "Tapped coord: " + tappedCoord.q + "," + tappedCoord.r);
        Log.d("taggedCoord", "state: " + offsetX + ", " + offsetY + ", " + scaleFactor);
        Log.d("taggedCoord", "position: " + scaledX + ", " + scaledY);
        if (tiles.containsKey(tappedCoord)) {
            if (tiles.get(tappedCoord) == TileType.PLUS) {
                expandTile(tappedCoord);
            } else if (tiles.get(tappedCoord) == TileType.NORMAL) {
                convertToPlus(tappedCoord);
            }
            markUnsavedChanges();
            invalidate();
        }
    }

    private Coordinate pixelToHex(float x, float y) {
        float centerX = getWidth() / (2f * scaleFactor);
        float centerY = getHeight() / (2f * scaleFactor);

        // Convert to HEX coordinates
        float q = (x - centerX) / HORIZONTAL_SPACING;
        float r = (y - centerY) / (TILE_SIZE*LAND_SPACING) - q/2f;
        Log.d("taggedCoord", "q: " + q + ", r: " + r);

        return hexRound(q, r);
    }

    private boolean handlePanAndZoom(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);

        final int action = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = ev.getActionIndex();
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                lastTouchX = x;
                lastTouchY = y;
                activePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                if (!scaleDetector.isInProgress()) {
                    final float dx = x - lastTouchX;
                    final float dy = y - lastTouchY;

                    offsetX += dx;
                    offsetY += dy;
                    invalidate();
                }

                lastTouchX = x;
                lastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                activePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastTouchX = ev.getX(newPointerIndex);
                    lastTouchY = ev.getY(newPointerIndex);
                    activePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    private boolean isClick(float x, float y) {
        float dx = Math.abs(x - touchStartX);
        float dy = Math.abs(y - touchStartY);
        return dx <= CLICK_TOLERANCE && dy <= CLICK_TOLERANCE;
    }
    private void expandTile(Coordinate coord) {
        tiles.put(coord, TileType.NORMAL);
        addSurroundingPlusTiles(coord);
    }

    private void convertToPlus(Coordinate coord) {
        tiles.put(coord, TileType.PLUS);
        updateSurroundingTiles(coord);
    }

    private void updateSurroundingTiles(Coordinate center) {
        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        for (int[] dir : directions) {
            Coordinate newCoord = new Coordinate(center.q + dir[0], center.r + dir[1]);
            if (tiles.containsKey(newCoord) && tiles.get(newCoord) == TileType.PLUS) {
                boolean hasNormalNeighbor = false;
                for (int[] checkDir : directions) {
                    Coordinate checkCoord = new Coordinate(newCoord.q + checkDir[0], newCoord.r + checkDir[1]);
                    if (tiles.containsKey(checkCoord) && tiles.get(checkCoord) == TileType.NORMAL && !checkCoord.equals(center)) {
                        hasNormalNeighbor = true;
                        break;
                    }
                }
                if (!hasNormalNeighbor) {
                    tiles.remove(newCoord);
                    markUnsavedChanges();
                }
            }
        }
    }

    private Coordinate hexRound(float q, float r) {
        float s = -q - r;
        int qi = Math.round(q);
        int ri = Math.round(r);
        int si = Math.round(s);

        float qDiff = Math.abs(qi - q);
        float rDiff = Math.abs(ri - r);
        float sDiff = Math.abs(si - s);

        if (qDiff > rDiff && qDiff > sDiff) {
            qi = -ri - si;
        } else if (rDiff > sDiff) {
            ri = -qi - si;
        }

        return new Coordinate(qi, ri);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }

    public void zoomIn() {
        scaleFactor *= ZOOM_FACTOR;
        if (scaleFactor > MAX_SCALE) {
            scaleFactor = MAX_SCALE;
        }
        invalidate();
    }

    public void zoomOut() {
        scaleFactor /= ZOOM_FACTOR;
        if (scaleFactor < MIN_SCALE) {
            scaleFactor = MIN_SCALE;
        }
        invalidate();
    }

    public void resetZoom() {
        scaleFactor = 1.0f;
        offsetX = 0;
        offsetY = 0;
        invalidate();
    }

    private static class Coordinate implements Comparable<Coordinate> {
        int q, r;

        Coordinate(int q, int r) {
            this.q = q;
            this.r = r;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return q == that.q && r == that.r;
        }

        public int compareTo(Coordinate other) {
            if (this.r != other.r) {
                return Integer.compare(this.r, other.r);
            }
            return Integer.compare(this.q, other.q);
        }

        @Override
        public int hashCode() {
            return 31 * q + r;
        }
    }

    private enum TileType {
        NORMAL, PLUS
    }
}