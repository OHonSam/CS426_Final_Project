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
import android.graphics.Region;
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
    private static final int GRID_SIZE = 5;
    private static final float offsetWidth = 1.5f;
    private static final float offsetHeight = 2.5f;
    private static final int offset3D = 50;

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
        float size = Math.min(width, height) * 0.95f;

//        landPath.reset();
//        landPath.moveTo(centerX, centerY - size / offsetHeight);
//        landPath.lineTo(centerX + size / offsetWidth, centerY);
//        landPath.lineTo(centerX, centerY + size / offsetHeight);
//        landPath.lineTo(centerX - size / offsetWidth, centerY);
//        landPath.lineTo(centerX - size /offsetWidth, centerY + offset3D);
//        landPath.lineTo(centerX, centerY + size / offsetHeight + offset3D);
//        landPath.lineTo(centerX + size / offsetWidth, centerY + offset3D);
//        landPath.lineTo(centerX + size / offsetWidth, centerY);
//        landPath.close();

        landPath.reset();
        landPath.moveTo(centerX, centerY - size / offsetHeight);
        landPath.lineTo(centerX + size / offsetWidth, centerY);
        landPath.lineTo(centerX, centerY + size / offsetHeight);
        landPath.lineTo(centerX - size / offsetWidth, centerY);
        landPath.lineTo(centerX, centerY - size / offsetHeight);
        landPath.close();
    }

    private void setupLandGradient() {
        int lightGreen = Color.rgb(144, 238, 144);
        int darkGreen = Color.rgb(34, 139, 34);
        LinearGradient gradient = new LinearGradient(
                0, 0, width, height,
                new int[]{darkGreen, lightGreen},
                null, Shader.TileMode.CLAMP
        );
        landPaint.setShader(gradient);
    }

    private void scaleTreeBitmap() {
        int scaledWidth = (int)(width / (GRID_SIZE * 1.5f));
        int scaledHeight = (int)(scaledWidth * 1.5f);
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
        float tileWidth = width / (GRID_SIZE * 1.5f);
        float tileHeight = height / (GRID_SIZE * 1.5f);
        float screenX = width / 2f + (gridX - gridY) * tileWidth * 0.5f;
        float screenY = height / 2f + (gridX + gridY) * tileHeight * 0.25f;
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
