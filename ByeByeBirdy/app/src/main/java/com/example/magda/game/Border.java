package com.example.magda.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Border extends GameObject{

    public Border(Bitmap res, int x, int y){
        width = res.getWidth();
        height = res.getHeight();
        this.x = x;
        this.y = y;
        dx = GamePanel.MOVESPEED;

        bitmap = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update(){
        x += (dx);
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);

    }

}

