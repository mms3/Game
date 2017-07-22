package com.example.magda.game;

import android.graphics.Canvas;
import android.provider.Settings;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

    //was 30
    private int fps = 50;
    private double averagefps;
    private SurfaceHolder surfaceholder;
    private GamePanel gamepanel;
    private boolean running;
    public static Canvas canvas;

    public MainThread(SurfaceHolder surfaceholder, GamePanel gamepanel) {
        super();
        this.surfaceholder = surfaceholder;
        this.gamepanel = gamepanel;
    }

    @Override
    public void run(){

        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        long frameCount = 0;
        long targetTime = 1000/fps;

        while (running){
            startTime = System.nanoTime();
            canvas = null;
            try {
                canvas = this.surfaceholder.lockCanvas();
                synchronized (surfaceholder) {
                    gamepanel.update();
                    gamepanel.draw(canvas);
                }
            } catch (Exception e) {
            }
            finally {
                if (canvas != null) {
                    try {
                        surfaceholder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                    }
                }
            }

            timeMillis = (System.nanoTime()-startTime)/1000000;
            waitTime = targetTime-timeMillis;

            try {
                this.sleep(waitTime);
            } catch (Exception e) {
            }

            totalTime += System.nanoTime()-startTime;
            frameCount++;
            if (frameCount == fps){
                averagefps = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    void setRunning(boolean running){
        this.running = running;
    }
}
