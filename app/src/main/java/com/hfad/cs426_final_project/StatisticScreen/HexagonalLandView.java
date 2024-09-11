package com.hfad.cs426_final_project.StatisticScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hfad.cs426_final_project.R;

import java.util.ArrayList;
import java.util.List;

public class HexagonalLandView extends View {
    private List<HexTile> tiles;
    private Bitmap tileBitmap;
    private int viewWidth, viewHeight;
    private float tileWidth, tileHeight;
    private Paint paint;
    private final float offsetLand = 5/27f;
    private final float offsetWidth = 59/82f;
    private final float offsetHeight = 32/81f;
    private final int maxTile = 4;
    private final int minTile = 3;

    public HexagonalLandView(Context context) {
        super(context);
        init(context);
    }

    public HexagonalLandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        tiles = new ArrayList<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.piece);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    public void setTileSize(float tileSize) {
        this.tileWidth = tileSize;
        this.tileHeight = tileSize;
        generateLandscape();
    }

    private void generateDiagonalTile(int numTile, float x, float y) {
        for (int i = 0; i < numTile; i++) {
            tiles.add(new HexTile(x + i * tileWidth*offsetWidth, y + i * tileHeight*offsetHeight));
        }
    }

    private void generateLandscape() {
        tiles.clear();

        int rows = (int) (viewHeight / tileHeight);
        int cols = (int) (viewWidth / tileWidth);

        for (int i = 0; i <= rows / 2; i++) {
            int numTile = (i % 2 == 0) ? minTile : maxTile;
            generateDiagonalTile(numTile, (float) viewWidth / 2 - i*tileWidth*offsetWidth , 100 + i * tileHeight*offsetHeight);
        }

        for (int i = rows / 2 + 1; i < rows; i++) {
            int numTile = (i % 2 == 0) ? maxTile : minTile;
            generateDiagonalTile(numTile, (float) viewWidth / 2 - (float) (rows /2)*tileWidth*offsetWidth, 100 + i * tileHeight);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (HexTile tile : tiles) {
            drawTile(canvas, tile);
        }
    }

    private void drawTile(Canvas canvas, HexTile tile) {
        float scale = tileWidth / tileBitmap.getWidth();

        canvas.save();
        canvas.translate(tile.x, tile.y);

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(-tileWidth / 2, -tileHeight / 2);

        canvas.drawBitmap(tileBitmap, matrix, paint);
        canvas.restore();
    }

    private class HexTile {
        float x, y;

        HexTile(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}