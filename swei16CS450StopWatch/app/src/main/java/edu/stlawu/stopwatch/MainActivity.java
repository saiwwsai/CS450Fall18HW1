package edu.stlawu.stopwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Define variable for our views
    private TextView tv_min = null;
    private TextView tv_sec = null;
    private TextView tv_msec = null;

    private Button bt_start = null;
    private Button bt_reset = null;
    private Button bt_resume = null;
    private Button bt_stop = null;

    private Timer t = null;
    private Counter ctr = null;  // TimerTask
    private int count = 0;
    private int saveCount = 0;


    /*
        public AudioAttributes  aa = null;
        private SoundPool soundPool = null;
        private int bloopSound = 0;
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get access to count
       // this.count = 0;

        // initialize views
        this.tv_min = findViewById(R.id.tv_min);
        this.tv_sec = findViewById(R.id.tv_sec);
        this.tv_msec = findViewById(R.id.tv_msec);
        this.bt_start = findViewById(R.id.bt_start);
        this.bt_reset = findViewById(R.id.bt_reset);
        this.bt_resume = findViewById(R.id.bt_resume);
        this.bt_stop = findViewById(R.id.bt_stop);



        // start button
        this.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set counters to zero; in case starting with other numbers
                count = 0;
                saveCount = 0;

                // start timer and counter
                t = new Timer();
                ctr = new Counter();
                ctr.count = count;
                t.scheduleAtFixedRate(ctr,0,10);

                // enable start button
                bt_start.setEnabled(false);
                // resume can be pushed only when paused
                bt_resume.setEnabled(false);
                // show stop and reset button
                bt_stop.setEnabled(true);
                bt_reset.setEnabled(true);
            }
        });

        // stop button
        this.bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save counters for resume
                saveCount = ctr.count;
                // stop timer and counter
                t.cancel();
                ctr.cancel();

                // grid out stop
                bt_stop.setEnabled(false);
                // show resume
                bt_resume.setEnabled(true);
                bt_reset.setEnabled(true);
                // enable start - in case duplicate counters' adding up
                bt_start.setEnabled(false);
            }
        });

        // resume button
        this.bt_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set counters back to 0
                count = 0;
                saveCount = 0;
                // start new timer n counter
                t = new Timer();
                ctr = new Counter();
                // get the count save at stop
                ctr.count = saveCount;
                t.scheduleAtFixedRate(ctr, 0, 10);
                //show reset; stop
                bt_stop.setEnabled(true);
                bt_reset.setEnabled(true);
                // cannot have start and resume together
                bt_start.setEnabled(false);
                // cannot have resume since timer is running
                bt_resume.setEnabled(false);
            }
        });


        // reset button
        this.bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cancel current timer
                t.cancel();
                ctr.cancel();
                ctr.count = 0;

                // set everything to 0s
                tv_min.setText("00");
                tv_sec.setText("00");
                tv_msec.setText("0");

                // cannot have start
                bt_start.setEnabled(true);
                // call back stop
                bt_stop.setEnabled(true);
                // timer running, no resume allowed
                bt_resume.setEnabled(false);
            }
        });

/*
        this.aa = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(aa)
                .build();
        this.bloopSound = this.soundPool.load(
                this, R.raw.bloop, 1);

        this.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(bloopSound, 1f,
                        1f, 1, 0, 1f);
                Animator anim = AnimatorInflater
                        .loadAnimator(MainActivity.this,
                                       R.animator.counter);
                anim.setTarget(bt_start);
                anim.start();
            }
        });
*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        // releoad the count from a previous
        // run, if first time running, start at 0.
        /// preferences to share state

        count = getPreferences(MODE_PRIVATE).getInt("COUNT", 0);

        // factory method - design pattern
        Toast.makeText(this, "Welcome Back!",
                Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // save stage
        getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply();
    }
    /*
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        savedInstanceState.putInt("", count);
        savedInstanceState.putInt("", saveCount);
        savedInstanceState.putString("", tv_min.toString());
        savedInstanceState.putString("", tv_sec.toString());
        savedInstanceState.putString("", tv_msec.toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        String msec1 = savedInstanceState.getString(tv_msec.toString());
    } */

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save the count to the preferences
       getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    class Counter extends TimerTask {
        private int count = MainActivity.this.count;

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // generate count to time unit
                    int msec = (count % 100) / 10;
                    int sec = (count / 100) % 60;
                    int min = ((count / (100 * 60)) % 60);

                    // generate format for timer
                    if (min < 10)
                        MainActivity.this.tv_min.setText("0" + min);
                    else
                        MainActivity.this.tv_min.setText("" + min);
                    if (sec < 10)
                        MainActivity.this.tv_sec.setText("0" + sec);
                    else
                        MainActivity.this.tv_sec.setText("" + sec);

                    // cannot directly set integer as text
                    String msecString = Integer.toString(msec);
                    MainActivity.this.tv_msec.setText(msecString);

                    // Increase the count
                    count++;
                }

            });
        }
    }



}
