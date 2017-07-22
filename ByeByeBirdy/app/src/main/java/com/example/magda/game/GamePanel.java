package com.example.magda.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static int WIDTH ;
    public static int HEIGHT;
    public static final int MOVESPEED = -16; // was -5/-10
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Enemy> enemies;
    private long enemiesStartTime;
    private Random rand = new Random();
    private ArrayList<Border> border;
    private long borderStartTime;
    private ArrayList<Star> stars;
    private long starsStartTime;
    private Bitmap pl;
    private Bitmap en;
    private Bitmap st;
    private int score;
    private boolean pause;
    public Game game;
    private boolean dead;
    MediaPlayer hitSound;
    MediaPlayer scoreSound;
    public static boolean isSound;

    public GamePanel(Context context, Game game){
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(),this);
        setFocusable(true);
        this.game = game;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.game_bg);
        WIDTH=image.getWidth();
        HEIGHT=image.getHeight();
        bg = new Background(image);

        hitSound = MediaPlayer.create(this.game, R.raw.hit);
        scoreSound = MediaPlayer.create(this.game, R.raw.bonus);
        scoreSound.setVolume(0.3f, 0.3f);

        pl = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        player = new Player(pl,pl.getWidth()/2,pl.getHeight(),2,hitSound);

        score = 0;
        pause = false;
        dead = false;
        isSound = true;

        border = new ArrayList<Border>();
        enemies = new ArrayList<Enemy>();
        stars = new ArrayList<Star>();

        borderStartTime = System.nanoTime();
        enemiesStartTime = System.nanoTime();
        starsStartTime = System.nanoTime();

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000) {
            counter++;
            try{
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            } catch (InterruptedException e){
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()== MotionEvent.ACTION_DOWN){
            if(!player.getPlaying()){
                player.setPlaying(true);
            }
            player.setUp(true);
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update(){
        if (!pause) {
            if (player.getPlaying()) {
                bg.update();
                player.update();

                if (player.isDead()) {
                    player.setPlaying(false);
                    Message msg = game.handler.obtainMessage();
                    msg.what = 0;
                    game.handler.sendMessage(msg);
                } else {

                    long borderElapsed = (System.nanoTime() - borderStartTime) / 1000000;
                    if (borderElapsed > (4000 - player.getScore() / 4)) {
                        Bitmap bord = BitmapFactory.decodeResource(getResources(), R.drawable.border);
                        border.add(new Border(bord, (int) ((WIDTH + 350) + (700 - 200) * rand.nextDouble()), 850));
                        borderStartTime = System.nanoTime();
                    }
                    for (int i = 0; i < border.size(); i++) {
                        border.get(i).update();

                        if (collision(border.get(i), player)) {
                            if (checkSound())
                                hitSound.start();
                            pauseGame();
                            player.setPlaying(false);
                            Message msg = game.handler.obtainMessage();
                            msg.what = 0;
                            game.handler.sendMessage(msg);
                            break;
                        }

                        if (border.get(i).getX() < -200) {
                            border.remove(i);
                            break;
                        }
                    }


                    long enemyElapsed = (System.nanoTime() - enemiesStartTime) / 1000000;
                    if (enemyElapsed > (1500 - player.getScore() / 4)) {
                        en = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
                        enemies.add(new Enemy(en, WIDTH + 350, (int) (rand.nextDouble() * ((HEIGHT-240)-10)+10), en.getWidth() / 2, en.getHeight(), player.getScore(), 2));
                        enemiesStartTime = System.nanoTime();
                    }
                    for (int i = 0; i < enemies.size(); i++) {
                        enemies.get(i).update();

                        if (isCollision(enemies.get(i), player)) {
                            if (checkSound())
                                hitSound.start();
                            pauseGame();
                            player.setPlaying(false);
                            Message msg = game.handler.obtainMessage();
                            msg.what = 0;
                            game.handler.sendMessage(msg);
                            break;
                        }

                        if (enemies.get(i).getX() < -100) {
                            enemies.remove(i);
                            break;
                        }
                    }


                    long starsElapsed = (System.nanoTime() - starsStartTime) / 1000000;
                    if (starsElapsed > (1500 - player.getScore() / 4)) {
                        st = BitmapFactory.decodeResource(getResources(), R.drawable.star);
                        stars.add(new Star(st, WIDTH + 350, (int) (rand.nextDouble() * ((HEIGHT-240)-10)+10), player.getScore()));
                        starsStartTime = System.nanoTime();
                    }

                    for (int i = 0; i < stars.size(); i++) {
                        stars.get(i).visited = false;

                        stars.get(i).update();

                        if (collision(stars.get(i),player))    {
                            if(checkSound())
                                scoreSound.start();
                            score++;
                            stars.remove(i);
                        }

                        if (stars.get(i).getX() < -100) {
                            stars.remove(i);
                            break;
                        }
                    }
                }

            } else {
                if (!dead) {

                } else {
                    Message msg = game.handler.obtainMessage();
                    msg.what = 0;
                    game.handler.sendMessage(msg);
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas){
        if (canvas != null){
            final int savedState = canvas.save();
            bg.draw(canvas);
            player.draw(canvas);
            canvas.restoreToCount(savedState);
            for (Star s: stars){
                s.draw(canvas);
            }
            for (Enemy e: enemies){
                e.draw(canvas);
            }
            for (Border b: border){
                b.draw(canvas);
            }
            drawText(canvas);
        }
    }

    public boolean collision(GameObject a, GameObject b){
        if (Rect.intersects(a.getRectangle(), b.getRectangle())){
            return true;
        }
        return false;
    }

    public void pauseGame(){
        pause = true;
    }

    public void continueGame(){
        pause = false;
    }

    public void newGame(){

        border.clear();
        enemies.clear();
        stars.clear();
        player.resetDY();
        player.resetScore();
        player.resetDeath();
        player.setY(HEIGHT/2);
        score = 0;
        pause = false;
    }

    public int getScore(){
        return score;
    }


    public void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        Typeface custom = Typeface.createFromAsset(getContext().getAssets(), "Birdy Game.ttf");
        paint.setTypeface(custom);
        canvas.drawText("SCORE: " + score, 100, 150, paint);
    }

    public boolean isCollision(GameObject a,  GameObject b){
        int x1 = a.getX();
        int y1 = a.getY();
        Bitmap bitmap1 = a.bitmap;
        int x2 = b.getX();
        int y2 = b.getY();
        Bitmap bitmap2 = b.bitmap;

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth(), y2+bitmap2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Rect getCollisionBounds(Rect rect1, Rect rect2) {
        int left = (int) Math.max(rect1.left, rect2.left);
        int top = (int) Math.max(rect1.top, rect2.top);
        int right = (int) Math.min(rect1.right, rect2.right);
        int bottom = (int) Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }

    private static boolean isFilled(int pixel) {
        return pixel != Color.TRANSPARENT;
    }

    public void setSound(boolean s){
        isSound = s;
    }

    public static boolean checkSound(){
        return isSound;
    }

}
