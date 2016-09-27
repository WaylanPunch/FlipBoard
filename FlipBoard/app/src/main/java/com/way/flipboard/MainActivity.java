package com.way.flipboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.way.flipboardlibrary.FlipBoard;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();

    private LinearLayout rootView;
    FlipBoard fb_card, fb_sun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = (LinearLayout) findViewById(R.id.activity_main);
        fb_card = (FlipBoard) findViewById(R.id.fb_card);
        fb_sun = (FlipBoard) findViewById(R.id.fb_sun);

        fb_card.setOnFlipListener(new FlipBoard.OnFlipListener() {
            @Override
            public void onFlipStart(FlipBoard view) {
                boolean isFlipped = view.isFlipped();
                Log.d(TAG, "onFlipStart debug, isFlipped = " + isFlipped);
            }

            @Override
            public void onFlipEnd(FlipBoard view) {
                boolean isFlipped = view.isFlipped();
                Log.d(TAG, "onFlipEnd debug, isFlipped = " + isFlipped);
                if (isFlipped) {
                    Toast.makeText(MainActivity.this, "Back Side", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Front Side", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fb_sun.setOnFlipListener(new FlipBoard.OnFlipListener() {
            @Override
            public void onFlipStart(FlipBoard view) {
                boolean isFlipped = view.isFlipped();
                Log.d(TAG, "onFlipStart debug, isFlipped = " + isFlipped);
            }

            @Override
            public void onFlipEnd(FlipBoard view) {
                boolean isFlipped = view.isFlipped();
                Log.d(TAG, "onFlipEnd debug, isFlipped = " + isFlipped);
                if (isFlipped) {
                    Toast.makeText(MainActivity.this, "Back Side", Toast.LENGTH_SHORT).show();
                    rootView.setBackgroundResource(R.color.colorPrimary);
                } else {
                    Toast.makeText(MainActivity.this, "Front Side", Toast.LENGTH_SHORT).show();
                    rootView.setBackgroundResource(R.color.colorWhite);
                }
            }
        });
    }
}
