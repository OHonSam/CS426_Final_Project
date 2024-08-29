package com.hfad.cs426_final_project.StatisticScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.List;

public class IsometricLandView extends View {
    private Paint landPaint;
    private Path landPath;
    private int width, height;
    private List<Tree> trees;
    private Bitmap treeBitmap;
    private static final int GRID_SIZE = 4; // 4x4 grid

    public IsometricLandView(Context context) {
        super(context);
        init(context);
    }

    public IsometricLandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        landPaint = new Paint();
        landPaint.setAntiAlias(true);
        landPath = new Path();
        trees = new ArrayList<>();
        treeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        setupLandPath();
        setupLandGradient();
        scaleTreeBitmap();
    }

    private void setupLandPath() {
        float centerX = width / 2f;
        float centerY = height / 2f;
        float size = Math.min(width, height) * 0.8f;

        landPath.reset();
        landPath.moveTo(centerX, centerY - size / 4);
        landPath.lineTo(centerX + size / 2, centerY);
        landPath.lineTo(centerX, centerY + size / 4);
        landPath.lineTo(centerX - size / 2, centerY);
        landPath.close();
    }

    private void setupLandGradient() {
        int lightGreen = Color.rgb(144, 238, 144);
        int darkGreen = Color.rgb(34, 139, 34);
        LinearGradient gradient = new LinearGradient(
                0, 0, width, height,
                new int[]{lightGreen, darkGreen},
                null, Shader.TileMode.CLAMP
        );
        landPaint.setShader(gradient);
    }

    private void scaleTreeBitmap() {
        int scaledWidth = (int)(width / (GRID_SIZE * 2f));
        int scaledHeight = (int)(scaledWidth * 1.5f); // Adjust this factor as needed
        treeBitmap = Bitmap.createScaledBitmap(treeBitmap, scaledWidth, scaledHeight, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(landPath, landPaint);
        drawTrees(canvas);
    }

    private void drawTrees(Canvas canvas) {
        for (Tree tree : trees) {
            float drawX = tree.x - treeBitmap.getWidth() / 2f;
            float drawY = tree.y - treeBitmap.getHeight();
            canvas.drawBitmap(treeBitmap, drawX, drawY, null);
        }
    }

    public void addTree(int gridX, int gridY) {
        if (gridX < 0 || gridX >= GRID_SIZE || gridY < 0 || gridY >= GRID_SIZE) {
            return; // Invalid grid position
        }

        PointF position = gridToScreenPosition(gridX, gridY);
        trees.add(new Tree(position.x, position.y, gridX, gridY));
        invalidate(); // Redraw the view
    }

    private PointF gridToScreenPosition(int gridX, int gridY) {
        float tileWidth = width / (GRID_SIZE * 2f);
        float tileHeight = height / (GRID_SIZE * 2f);
        float screenX = width / 2f + (gridX - gridY) * tileWidth;
        float screenY = height / 2f + (gridX + gridY) * tileHeight / 2; // Adjust this factor if needed
        return new PointF(screenX, screenY);
    }

    private static class Tree {
        float x, y;
        int gridX, gridY;
        Tree(float x, float y, int gridX, int gridY) {
            this.x = x;
            this.y = y;
            this.gridX = gridX;
            this.gridY = gridY;
        }
    }
}
