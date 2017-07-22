package com.example.magda.game;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Game extends Activity {

    RelativeLayout rel_main_game;
    ImageView pauseButton;
    ImageView soundButton;
    ImageView infoButton;
    View pauseMenu;
    View gameOver;
    View infoWindow;
    GamePanel gamepanel;
    Boolean isSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        rel_main_game = (RelativeLayout) findViewById(R.id.main_game);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        final int heightS = dm.heightPixels;
        final int widthS = dm.widthPixels;

        gamepanel = new GamePanel(getApplicationContext(), this);
        rel_main_game.addView(gamepanel);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setGravity(Gravity.CENTER);
        rel_main_game.addView(buttons,800,200);
        buttons.setX(widthS-900);
        buttons.setY(30);

        infoButton = new ImageView(this);
        infoButton.setImageResource(R.drawable.btn_info);
        infoButton.setPadding(0,0,50,0);
        infoButton.setOnClickListener(infoButtonClick);
        buttons.addView(infoButton);

        soundButton = new ImageView(this);
        soundButton.setImageResource(R.drawable.btn_sound);
        soundButton.setOnClickListener(soundButtonClick);
        buttons.addView(soundButton);

        pauseButton = new ImageView(this);
        pauseButton.setImageResource(R.drawable.btn_pause);
        pauseButton.setPadding(50,0,0,0);
        pauseButton.setOnClickListener(pauseButtonClick);
        buttons.addView(pauseButton);

        LayoutInflater myInflater = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        pauseMenu= myInflater.inflate(R.layout.pause, null, false);
        rel_main_game.addView(pauseMenu);
        pauseMenu.setVisibility(View.GONE);

        infoWindow= myInflater.inflate(R.layout.info, null, false);
        rel_main_game.addView(infoWindow);
        infoWindow.setVisibility(View.GONE);

        ImageView closeButton = (ImageView) findViewById(R.id.btn_close);
        closeButton.setOnClickListener(continueGame);

        ImageView continueButton = (ImageView) findViewById(R.id.btn_continue);
        ImageView quitButton = (ImageView) findViewById(R.id.btn_quit);
        continueButton.setOnClickListener(continueGame);
        quitButton.setOnClickListener(goToMenu);

        isSound = true;

        gameOver= myInflater.inflate(R.layout.lost, null, false);
        rel_main_game.addView(gameOver);
        gameOver.setVisibility(View.GONE);

        ImageView restartButton = (ImageView) findViewById(R.id.btn_restart);
        ImageView quitButton2 = (ImageView) findViewById(R.id.btn_quit2);
        restartButton.setOnClickListener(restartGame);
        quitButton2.setOnClickListener(goToMenu);

    }

    View.OnClickListener pauseButtonClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            gamepanel.pauseGame();
            pauseButton.setVisibility(View.GONE);
            infoButton.setVisibility(View.GONE);
            soundButton.setVisibility(View.GONE);
            pauseMenu.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener infoButtonClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            gamepanel.pauseGame();
            pauseButton.setVisibility(View.GONE);
            soundButton.setVisibility(View.GONE);
            infoButton.setVisibility(View.GONE);
            infoWindow.setVisibility(View.VISIBLE);
        }
    };

    View.OnClickListener soundButtonClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if (gamepanel.checkSound()){
                soundButton.setImageResource(R.drawable.btn_nosound);
                gamepanel.setSound(false);
            } else {
                soundButton.setImageResource(R.drawable.btn_sound);
                gamepanel.setSound(true);
            }
        }
    };

    View.OnClickListener continueGame = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pauseMenu.setVisibility(View.GONE);
            infoWindow.setVisibility(View.GONE);
            infoButton.setVisibility(View.VISIBLE);
            soundButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            gamepanel.continueGame();
        }
    };

    View.OnClickListener restartGame = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gameOver.setVisibility(View.GONE);
            infoButton.setVisibility(View.VISIBLE);
            soundButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            gamepanel.newGame();
        }
    };


    View.OnClickListener goToMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Game.this.finish();
        }
    };

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what==0){
                gameOver.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                soundButton.setVisibility(View.GONE);
                infoButton.setVisibility(View.GONE);
                gamepanel.pauseGame();

            }
            super.handleMessage(msg);
        }
    };
}
