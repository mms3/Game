package com.example.magda.game;

import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class GameObject {
    protected int x, y, dx, dy, width, height;
    protected Bitmap bitmap;

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public Rect getRectangle(){
        return new Rect(x, y, x+width, y+height);
    }
}
