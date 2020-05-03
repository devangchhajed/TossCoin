package com.devc.tosscoin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.Random;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private static final String HIGH_SCORE = "high score";
    private static final String COIN_SIDE = "coin side";
    private static final String SCORE = "score";
    private static final String HIST = "hist";

    private ImageView coinImage;
    private TextView scoreText;
    private TextView hist;

    private Random r;
    private int coinSide;
    private MediaPlayer mp;
    private int curSide = R.drawable.heads;

    private String filename = "highScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        r = new Random();
        coinImage = (ImageView) findViewById(R.id.coin);
        hist = (TextView) findViewById(R.id.hist);



        if (savedInstanceState != null) {
            coinImage.setImageResource(Integer.parseInt(savedInstanceState.getCharSequence(COIN_SIDE).toString()));
            hist.setText(savedInstanceState.getCharSequence(HIST));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(COIN_SIDE, String.valueOf(curSide));
        outState.putCharSequence(HIST, hist.getText());
    }



    @Override
    public void onPause() {

        // Write the new high score to the highScore file

        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onPause();

    }



    private void setButtonsEnabled(boolean enabled) {
        coinImage.setEnabled(enabled);

    }

    private long animateCoin(boolean stayTheSame) {

        Rotate3dAnimation animation;

        if (curSide == R.drawable.heads) {
            animation = new Rotate3dAnimation(coinImage, R.drawable.heads, R.drawable.tails, 0, 180, 0, 0, 0, 0);
        } else {
            animation = new Rotate3dAnimation(coinImage, R.drawable.tails, R.drawable.heads, 0, 180, 0, 0, 0, 0);
        }
        if (stayTheSame) {
            animation.setRepeatCount(5); // must be odd (5+1 = 6 flips so the side will stay the same)
        } else {
            animation.setRepeatCount(6); // must be even (6+1 = 7 flips so the side will not stay the same)
        }

        animation.setDuration(110);
        animation.setInterpolator(new LinearInterpolator());



        coinImage.startAnimation(animation);


        setButtonsEnabled(false);

        return animation.getDuration() * (animation.getRepeatCount() + 1);
    }

    public void flipCoin(View v) {

        coinSide = r.nextInt(2);

        stopPlaying();
        mp = MediaPlayer.create(this, R.raw.coin_flip);
        mp.start();

        if (coinSide == 0) {  // We have Tails

            boolean stayTheSame = (curSide == R.drawable.tails);
            long timeOfAnimation = animateCoin(stayTheSame);
            curSide = R.drawable.tails;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                        hist.append(getResources().getString(R.string.tails_first_letter));
                    setButtonsEnabled(true);
                    hist.setVisibility(View.VISIBLE);

                }


            }, timeOfAnimation + 100);


        } else {  // We have Heads

            boolean stayTheSame = (curSide == R.drawable.heads);
            long timeOfAnimation = animateCoin(stayTheSame);
            curSide = R.drawable.heads;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                        hist.append(getResources().getString(R.string.heads_first_letter));


                    setButtonsEnabled(true);
                    hist.setVisibility(View.VISIBLE);

                }

            }, timeOfAnimation + 100);

        }


    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
