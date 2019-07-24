package com.example.smartmusic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {

    ImageButton pause, next, prev;
    TextView songName;
    SeekBar seekBar;

    static MediaPlayer mediaPlayer;
    int position;

    ArrayList<File> mySongs;
    String csongName;
    Thread updateSeekbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        songName = findViewById(R.id.songName);
        seekBar = findViewById(R.id.seekBar);

        setUpToolbar();

        updateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("SONGLIST");
        csongName = mySongs.get(position).getName();

        String songname = intent.getStringExtra("SONG");
        songName.setText(songname);
        songName.setSelected(true);

        position = bundle.getInt("POS",0);
        Uri u = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        updateSeekbar.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setMax(mediaPlayer.getDuration());
                if(mediaPlayer.isPlaying()){
                    pause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    mediaPlayer.pause();
                }else{
                    pause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    mediaPlayer.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position = ((position + 1)%mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                songName.setText(mySongs.get(position).getName());

                mediaPlayer.start();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position = ((position - 1)<0)?(mySongs.size()-1):(position - 1);

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                songName.setText(mySongs.get(position).getName());

                mediaPlayer.start();
            }
        });
    }

    private void setUpToolbar(){
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
