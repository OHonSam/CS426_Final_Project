package com.hfad.cs426_final_project.PlantingScreen;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hfad.cs426_final_project.DataStorage.Block;
import com.hfad.cs426_final_project.DataStorage.BlockData;
import com.hfad.cs426_final_project.DataStorage.LandState;
import com.hfad.cs426_final_project.R;
import com.hfad.cs426_final_project.User;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HexagonalLandView extends View {
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;
    private static final float ZOOM_FACTOR = 1.2f;
    private OnBlockRestoredListener onBlockRestoredListener;

    private static final float TILE_SIZE = 100f;
    private static final float HORIZONTAL_SPACING = TILE_SIZE * 0.76f;
    private static final float VERTICAL_SPACING = TILE_SIZE * 0.41f;
    private static final float LAND_SPACING = 0.84f;
    private boolean hasUnsavedChanges = false;
    private boolean isPlantingMode = true;
    private Bitmap defaultTile;
    private User currentUser;

    private Map<Coordinate, TileType> tiles = new HashMap<>();
    private Bitmap plusTile;
    private float offsetX, offsetY;
    private float scaleFactor = 1f;
    private Matrix matrix = new Matrix();

    private Map<Coordinate, Block> blockTiles = new HashMap<>();
    private BlockData selectedBlockData;
    private List<BlockData> blockDataList;
    private OnBlockUsedListener onBlockUsedListener;
    private Map<String, Bitmap> bitmapCache = new HashMap<>();

    private ScaleGestureDetector scaleDetector;
    private float lastTouchX, lastTouchY;
    private static final int INVALID_POINTER_ID = -1;
    private int activePointerId = INVALID_POINTER_ID;
    private DatabaseReference tilesRef;
    private float touchStartX, touchStartY;
    private static final float CLICK_TOLERANCE = 10f;

    private static final long ANIMATION_DURATION = 1000;
    private static final float MAX_BORDER_WIDTH = 8f;
    private long animationStartTime;
    private Paint borderPaint;
    private Path hexagonPath;

    public HexagonalLandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Bitmap originalDefaultTile = BitmapFactory.decodeResource(getResources(), R.drawable.piece);
        defaultTile = Bitmap.createScaledBitmap(originalDefaultTile, (int)TILE_SIZE, (int)TILE_SIZE, true);
        originalDefaultTile.recycle();

        Bitmap originalPlusTile = BitmapFactory.decodeResource(getResources(), R.drawable.plus_btn);
        plusTile = Bitmap.createScaledBitmap(originalPlusTile, (int)TILE_SIZE, (int)TILE_SIZE, true);
        originalPlusTile.recycle();

        blockDataList = new ArrayList<>();

        offsetX = offsetY = 0f;
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(ContextCompat.getColor(context, R.color.primary_20));
        animationStartTime = System.currentTimeMillis();
        hexagonPath = new Path();

        preloadBitmaps();
        postInvalidateOnAnimation();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadLandState();
    }

    private void loadLandState() {
        if (currentUser != null && currentUser.getLandState() != null) {
            tiles.clear();
            blockTiles.clear();

            for (Map.Entry<String, LandState.TileInfo> entry : currentUser.getLandState().getTiles().entrySet()) {
                String[] coords = entry.getKey().split(",");
                int q = Integer.parseInt(coords[0]);
                int r = Integer.parseInt(coords[1]);
                Coordinate coord = new Coordinate(q, r);

                LandState.TileInfo tileInfo = entry.getValue();
                if ("NORMAL".equals(tileInfo.getType())) {
                    tiles.put(coord, TileType.NORMAL);
                    if (tileInfo.getBlockId() != null) {
                        Block block = findBlockById(tileInfo.getBlockId());
                        if (block != null) {
                            blockTiles.put(coord, block);
                        }
                    }
                } else if ("PLUS".equals(tileInfo.getType())) {
                    tiles.put(coord, TileType.PLUS);
                }
            }

            invalidate();
        } else {
            // Initialize with center tile if no saved state
            Coordinate center = new Coordinate(0, 0);
            tiles.put(center, TileType.PLUS);
        }
    }

    private Block findBlockById(int id) {
        for (BlockData blockData : currentUser.getOwnBlock()) {
            if (blockData.getBlock().getId() == id) {
                return blockData.getBlock();
            }
        }
        return null;
    }

    public void saveLandState() {
        if (currentUser != null) {
            LandState landState = new LandState();
            Map<String, LandState.TileInfo> tileMap = new HashMap<>();

            for (Map.Entry<Coordinate, TileType> entry : tiles.entrySet()) {
                Coordinate coord = entry.getKey();
                TileType tileType = entry.getValue();
                String key = coord.q + "," + coord.r;

                if (tileType == TileType.NORMAL) {
                    Block block = blockTiles.get(coord);
                    Integer blockId = block != null ? block.getId() : null;
                    tileMap.put(key, new LandState.TileInfo("NORMAL", blockId));
                } else if (tileType == TileType.PLUS) {
                    tileMap.put(key, new LandState.TileInfo("PLUS", null));
                }
            }

            landState.setTiles(tileMap);
            currentUser.setLandState(landState);
        }
    }

    private void preloadBitmaps() {
        for (Block block : blockTiles.values()) {
            if (block != null && block.getImgUri() != null && !block.getImgUri().isEmpty()) {
                loadBitmapFromBlock(block);
            }
        }
    }

    private void loadBitmapFromBlock(Block block) {
        if (block == null || block.getImgUri() == null || block.getImgUri().isEmpty()) {
            Log.d("HexagonalLandView", "Invalid block or image URI");
            return;
        }

        if (bitmapCache.containsKey(block.getImgUri())) {
            Log.d("HexagonalLandView", "Bitmap already in cache for " + block.getImgUri());
            invalidate();
            return;
        }

        Glide.with(getContext())
                .asBitmap()
                .load(Uri.parse(block.getImgUri()))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Log.d("HexagonalLandView", "Loaded bitmap for " + block.getImgUri());
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource, (int)TILE_SIZE, (int)TILE_SIZE, true);
                        bitmapCache.put(block.getImgUri(), scaledBitmap);
                        invalidate();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d("HexagonalLandView", "Cleared bitmap for " + block.getImgUri());
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Log.d("HexagonalLandView", "Failed to load bitmap for " + block.getImgUri());
                        bitmapCache.put(block.getImgUri(), defaultTile);
                        invalidate();
                    }
                });
    }

    public void setSelectedBlockData(BlockData blockData) {
        this.selectedBlockData = blockData;
    }

    public void setOnBlockUsedListener(OnBlockUsedListener listener) {
        this.onBlockUsedListener = listener;
    }

    public void setOnBlockRestoredListener(OnBlockRestoredListener listener) {
        this.onBlockRestoredListener = listener;
    }

    private BlockData getDefaultBlockData() {
        for (BlockData blockData : blockDataList) {
            if (blockData.getQuantity() > 0) {
                return blockData;
            }
        }
        return null;
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

            Bitmap tileToDraw = plusTile;
            if (type == TileType.PLUS) {
                matrix.setTranslate(x - TILE_SIZE / 2, y - TILE_SIZE / 2);
                canvas.drawBitmap(tileToDraw, matrix, null);
            } else {
                Block block = blockTiles.get(coord);
                if (block != null) {
                    Bitmap bitmap = bitmapCache.get(block.getImgUri());
                    if (bitmap != null) {
                        matrix.setTranslate(x - TILE_SIZE / 2, y - TILE_SIZE / 2);
                        canvas.drawBitmap(bitmap, matrix, null);
                    } else {
                        loadBitmapFromBlock(block);
                        matrix.setTranslate(x - TILE_SIZE / 2, y - TILE_SIZE / 2);
                        canvas.drawBitmap(defaultTile, matrix, null);
                    }
                } else {
                    tileToDraw = defaultTile;
                    matrix.setTranslate(x - TILE_SIZE / 2, y - TILE_SIZE / 2);
                    canvas.drawBitmap(tileToDraw, matrix, null);
                }
            }

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
                // saveAllTiles();
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
            case MotionEvent.ACTION_DOWN:
                touchStartX = ev.getX();
                touchStartY = ev.getY();
                lastTouchX = touchStartX;
                lastTouchY = touchStartY;
                break;

            case MotionEvent.ACTION_MOVE:
                final float x = ev.getX();
                final float y = ev.getY();

                final float dx = x - lastTouchX;
                final float dy = y - lastTouchY;

                offsetX += dx;
                offsetY += dy;
                invalidate();

                lastTouchX = x;
                lastTouchY = y;
                break;

            case MotionEvent.ACTION_UP:
                if (isClick(ev.getX(), ev.getY())) {
                    handleTileConversion(ev.getX(), ev.getY());
                }
                break;
        }
          return true;
    }

    private void handleTileConversion(float x, float y) {
        float scaledX = (x - offsetX) / scaleFactor;
        float scaledY = (y - offsetY) / scaleFactor;
        Coordinate tappedCoord = pixelToHex(scaledX, scaledY);

        if (tiles.containsKey(tappedCoord)) {
            if (tiles.get(tappedCoord) == TileType.PLUS) {
                if (selectedBlockData != null && selectedBlockData.getQuantity() > 0) {
                    Log.d("HexagonalLandView", "Expanding tile at " + tappedCoord.q + ", " + tappedCoord.r);
                    expandTile(tappedCoord);
                } else if (isAllTilesOutOfStock()) {
                    showOutOfStockDialog();
                } else {
                    Log.d("HexagonalLandView", "No more blocks of this type available");
                }
            } else {
                convertToPlus(tappedCoord);
            }
            markUnsavedChanges();
            removeIsolatedPlusTiles();
            invalidate();
        }
    }

    private boolean isAllTilesOutOfStock() {
        for (BlockData blockData : blockDataList) {
            if (blockData.getQuantity() > 0) {
                return false;
            }
        }
        return true;
    }

    private void showOutOfStockDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Out of Stock")
                .setMessage("Your tiles are out of stock! Please focus more to get more tiles.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void removeIsolatedPlusTiles() {
        Map<Coordinate, TileType> tilesToRemove = new HashMap<>();
        for (Map.Entry<Coordinate, TileType> entry : tiles.entrySet()) {
            if (entry.getValue() == TileType.PLUS && !hasNormalNeighbor(entry.getKey())) {
                tilesToRemove.put(entry.getKey(), entry.getValue());
            }
        }
        for (Coordinate coord : tilesToRemove.keySet()) {
            tiles.remove(coord);
        }

        // Check if the land is empty after removing isolated tiles
        if (tiles.isEmpty()) {
            // If empty, add a plus tile at the center (0,0)
            tiles.put(new Coordinate(0, 0), TileType.PLUS);
            markUnsavedChanges();
        }
    }

    private boolean hasNormalNeighbor(Coordinate coord) {
        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        for (int[] dir : directions) {
            Coordinate newCoord = new Coordinate(coord.q + dir[0], coord.r + dir[1]);
            if (tiles.containsKey(newCoord) && tiles.get(newCoord) == TileType.NORMAL && !newCoord.equals(coord)) {
                return true;
            }
        }
        return false;
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
            case MotionEvent.ACTION_DOWN:
                lastTouchX = ev.getX();
                lastTouchY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float x = ev.getX();
                final float y = ev.getY();

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

        return true;
    }

    private boolean isClick(float x, float y) {
        float dx = Math.abs(x - touchStartX);
        float dy = Math.abs(y - touchStartY);
        return dx <= CLICK_TOLERANCE && dy <= CLICK_TOLERANCE;
    }

    private void expandTile(Coordinate coord) {
        tiles.put(coord, TileType.NORMAL);

        BlockData blockToUse = selectedBlockData != null && selectedBlockData.getQuantity() > 0
                ? selectedBlockData
                : getDefaultBlockData();

        if (blockToUse != null && blockToUse.getQuantity() > 0) {
            blockTiles.put(coord, blockToUse.getBlock());
            loadBitmapFromBlock(blockToUse.getBlock());
            blockToUse.setQuantity(blockToUse.getQuantity() - 1);
            // Update the RecyclerView's dataset
            int position = blockDataList.indexOf(blockToUse);
            if (position != -1) {
                blockDataList.set(position, blockToUse);
            }

            if (onBlockUsedListener != null) {
                onBlockUsedListener.onBlockUsed(blockToUse);
            }
        } else {
            Log.d("HexagonalLandView", "No available blocks to place");
            return;
        }
        Log.d("HexagonalLandView", "Expanding tile at " + coord.q + ", " + coord.r);
        addSurroundingPlusTiles(coord);
    }



    private void convertToPlus(Coordinate coord) {
        if (tiles.get(coord) == TileType.NORMAL) {
            tiles.put(coord, TileType.PLUS);
            Block removedBlock = blockTiles.remove(coord);
            if (removedBlock != null && onBlockRestoredListener != null) {
                BlockData restoredBlockData = new BlockData(removedBlock, 1);
                onBlockRestoredListener.onBlockRestored(restoredBlockData);
            }
            updateSurroundingTiles(coord);
            markUnsavedChanges();
            invalidate();
        }
    }

    private void updateSurroundingTiles(Coordinate center) {
        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        for (int[] dir : directions) {
            Coordinate newCoord = new Coordinate(center.q + dir[0], center.r + dir[1]);
            if (tiles.containsKey(newCoord) && tiles.get(newCoord) == TileType.PLUS) {
                if (!hasNormalNeighbor(newCoord)) {
                    tiles.remove(newCoord);
                    markUnsavedChanges();
                }
            }
        }

        // Check if the land is empty after updating surrounding tiles
        if (tiles.isEmpty()) {
            // If empty, add a plus tile at the center (0,0)
            tiles.put(new Coordinate(0, 0), TileType.PLUS);
            markUnsavedChanges();
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
            scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));
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

    public interface OnBlockUsedListener {
        void onBlockUsed(BlockData blockData);
    }

    public interface OnBlockRestoredListener {
        void onBlockRestored(BlockData blockData);
    }
}