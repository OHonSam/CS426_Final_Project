package com.hfad.cs426_final_project.StatisticScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.List;

public class IsometricLandView extends View {
    private Paint landPaint, leftSidePaint, rightSidePaint, grassPaint;
    private Path landPath, leftPath, rightPath, grassPath;
    private int width, height;
    private List<Tree> trees;
    private Bitmap treeBitmap, grassTexture, sideTexture;
    private static final int GRID_SIZE = 10;
    private static final float OFFSET_WIDTH = 1.3f;
    private static final float OFFSET_HEIGHT = 2.5f;

    public IsometricLandView(Context context) {
        super(context);
        init(context);
    }

    public IsometricLandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        landPaint = createPaint();
        grassPaint = createPaint();
        leftSidePaint = createPaint();
        rightSidePaint = createPaint();

        landPath = new Path();
        leftPath = new Path();
        rightPath = new Path();
        grassPath = new Path();

        trees = new ArrayList<>();
        treeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree);
        grassTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.grasss);
        sideTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.ground);
    }

    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        return paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        setupLandPath();

        int colorLand = ContextCompat.getColor(getContext(), R.color.primary_30);
        landPaint.setColor(colorLand);
        scaleTreeBitmap();
        setupGrassPath();
        setupShader(grassPaint, grassTexture, grassPath);
        setupSideShader();
    }

    private void setupGrassPath() {
        float centerX = width / 2f;
        float centerY = height / 2f;
        float size = Math.min(width, height) * 0.8f;

        grassPath.reset();
        grassPath.moveTo(centerX, centerY - size / OFFSET_HEIGHT);
        grassPath.lineTo(centerX + size / OFFSET_WIDTH, centerY);
        grassPath.lineTo(centerX, centerY + size / OFFSET_HEIGHT);
        grassPath.lineTo(centerX - size / OFFSET_WIDTH, centerY);
        grassPath.close();
    }

    private void setupShader(Paint paint, Bitmap texture, Path path) {
        BitmapShader shader = new BitmapShader(texture, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        float scaleX = width / (float)(texture.getWidth() * GRID_SIZE);
        float scaleY = height / (float)(texture.getHeight() * GRID_SIZE);
        float scale = Math.max(scaleX, scaleY);

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        float translateX = (width - texture.getWidth() * scale) / 2f;
        float translateY = (height - texture.getHeight() * scale) / 2f;
        matrix.postTranslate(translateX, translateY);

        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
    }

    private void setupSideShader() {
        setupShaderForSide(leftSidePaint, 0, height / 4f);
        setupShaderForSide(rightSidePaint, width / 2f, height / 4f);
    }

    private void setupShaderForSide(Paint paint, float translateX, float translateY) {
        BitmapShader shader = new BitmapShader(sideTexture, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Matrix matrix = new Matrix();

        float sideWidth = width / 2f;
        float sideHeight = height / 2f;
        float scaleX = sideWidth / sideTexture.getWidth();
        float scaleY = sideHeight / sideTexture.getHeight();

        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(translateX, translateY);

        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
    }

    private void setupLandPath() {
        float centerX = width / 2f;
        float centerY = height / 2f;
        float size = Math.min(width, height) * 0.8f;
        float depth = size / 6f;

        setupTopFace(centerX, centerY, size);
        setupSideFace(centerX, centerY, size, depth, rightPath, OFFSET_WIDTH);
        setupSideFace(centerX, centerY, size, depth, leftPath, -OFFSET_WIDTH);
    }

    private void setupTopFace(float centerX, float centerY, float size) {
        landPath.reset();
        landPath.moveTo(centerX, centerY - size / OFFSET_HEIGHT);
        landPath.lineTo(centerX + size / OFFSET_WIDTH, centerY);
        landPath.lineTo(centerX, centerY + size / OFFSET_HEIGHT);
        landPath.lineTo(centerX - size / OFFSET_WIDTH, centerY);
        landPath.close();
    }

    private void setupSideFace(float centerX, float centerY, float size, float depth, Path path, float offsetX) {
        path.reset();
        path.moveTo(centerX + size / offsetX, centerY);
        path.lineTo(centerX, centerY + size / OFFSET_HEIGHT);
        path.lineTo(centerX, centerY + size / OFFSET_HEIGHT + depth);
        path.lineTo(centerX + size / offsetX, centerY + depth);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(landPath, landPaint);
        canvas.drawPath(grassPath, grassPaint);
        canvas.drawPath(leftPath, leftSidePaint);
        canvas.drawPath(rightPath, rightSidePaint);
        drawTrees(canvas);
    }

    private void scaleTreeBitmap() {
        int scaledWidth = (int)(width / (GRID_SIZE * 1.5f));
        int scaledHeight = (int)(scaledWidth * 1.5f);
        treeBitmap = Bitmap.createScaledBitmap(treeBitmap, scaledWidth, scaledHeight, true);
    }

    private void drawTrees(Canvas canvas) {
        for (Tree tree : trees) {
            float drawX = tree.x - treeBitmap.getWidth() / 2f;
            float drawY = tree.y - treeBitmap.getHeight();
            canvas.drawBitmap(treeBitmap, drawX, drawY, null);
        }
    }

    public void addTree(int gridX, int gridY) {
        if (isValidGridPosition(gridX, gridY)) {
            PointF position = gridToScreenPosition(gridX, gridY);
            trees.add(new Tree(position.x, position.y, gridX, gridY));
            invalidate(); // Redraw the view
        }
    }

    private boolean isValidGridPosition(int gridX, int gridY) {
        return gridX >= 0 && gridX < GRID_SIZE && gridY >= 0 && gridY < GRID_SIZE;
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
