package com.example.magda.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Star extends GameObject{

    private int score;
    public boolean visited;

    public Star (Bitmap res, int x, int y, int s){
        width = res.getWidth();
        height = res.getHeight();
        this.x = x;
        this.y = y;
        score = s;
        dx = GamePanel.MOVESPEED;
        visited = false;

        bitmap = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update(){
        x += (dx);
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
    }

}
