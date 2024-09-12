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

import java.util.HashMap;
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

import java.util.HashMap;
import java.util.Map;

public class HexagonalLandView extends View {
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 3.0f;
    private static final float ZOOM_FACTOR = 1.2f;

    private static final float TILE_WIDTH = 100f; // Adjust based on your image size
    private static final float TILE_HEIGHT = 100f; // Adjust based on your image size
    private static final float HORIZONTAL_SPACING = TILE_WIDTH * 0.79f;
    private static final float VERTICAL_SPACING = TILE_HEIGHT * 0.4f;
    private static final String TILES_KEY = "tiles";
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

    public HexagonalLandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tilesRef = database.getReference(TILES_KEY);

        Bitmap originalNormalTile = BitmapFactory.decodeResource(getResources(), R.drawable.piece);
        normalTile = Bitmap.createScaledBitmap(originalNormalTile, (int)TILE_WIDTH, (int)TILE_HEIGHT, true);
        originalNormalTile.recycle();

        Bitmap originalPlusTile = BitmapFactory.decodeResource(getResources(), R.drawable.plus_btn);
        plusTile = Bitmap.createScaledBitmap(originalPlusTile, (int)TILE_WIDTH, (int)TILE_HEIGHT, true);
        originalPlusTile.recycle();

        offsetX = offsetY = 0f;
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        loadTiles();
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

        for (Map.Entry<Coordinate, TileType> entry : tiles.entrySet()) {
            Coordinate coord = entry.getKey();
            TileType type = entry.getValue();

            float x = centerX + coord.q * HORIZONTAL_SPACING;
            float y = centerY + (coord.r * 2f + coord.q) * VERTICAL_SPACING;

            if (type == TileType.PLUS && !isPlantingMode) {
                continue;
            }

            Bitmap tileToDraw = (type == TileType.NORMAL) ? normalTile : plusTile;
            matrix.setTranslate(x - TILE_WIDTH / 2, y - TILE_HEIGHT / 2);
            canvas.drawBitmap(tileToDraw, matrix, null);
        }

        canvas.restore();
    }

    private void markUnsavedChanges() {
        hasUnsavedChanges = true;
    }

    // Method to save all tiles
    public void saveAllTiles() {
        Log.d("Hello", "hell");
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

        tilesRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    hasUnsavedChanges = false;
                    Log.d("HexagonalLandView", "Tiles saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("HexagonalLandView", "Failed to save tiles", e);
                });
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

        final int action = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = ev.getActionIndex();
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                lastTouchX = x;
                lastTouchY = y;
                activePointerId = ev.getPointerId(0);

                // Check for tile conversion
                float scaledX = (x - offsetX) / scaleFactor;
                float scaledY = (y - offsetY) / scaleFactor;
                Coordinate tappedCoord = pixelToHex(scaledX, scaledY);
                if (tiles.containsKey(tappedCoord)) {
                    if (tiles.get(tappedCoord) == TileType.PLUS) {
                        expandTile(tappedCoord);
                    } else if (tiles.get(tappedCoord) == TileType.NORMAL) {
                        convertToPlus(tappedCoord);
                    }
                    markUnsavedChanges();
                    invalidate();
                }
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

    private void expandTile(Coordinate coord) {
        tiles.put(coord, TileType.NORMAL);
        addSurroundingPlusTiles(coord);
    }

    private void convertToPlus(Coordinate coord) {
        tiles.put(coord, TileType.PLUS);
        removeSurroundingPlusTiles(coord);
    }

    private void removeSurroundingPlusTiles(Coordinate center) {
        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        for (int[] dir : directions) {
            Coordinate newCoord = new Coordinate(center.q + dir[0], center.r + dir[1]);
            if (tiles.containsKey(newCoord) && tiles.get(newCoord) == TileType.PLUS) {
                tiles.remove(newCoord);
            }
        }
    }

    private Coordinate pixelToHex(float x, float y) {
        float q = (x - getWidth() / 2f / scaleFactor) / HORIZONTAL_SPACING;
        float r = (y - getHeight() / 2f / scaleFactor) / VERTICAL_SPACING - q / 2f;
        return hexRound(q, r);
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

    private static class Coordinate {
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

        @Override
        public int hashCode() {
            return 31 * q + r;
        }
    }

    private enum TileType {
        NORMAL, PLUS
    }
}