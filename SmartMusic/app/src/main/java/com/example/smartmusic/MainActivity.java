package com.example.smartmusic;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_main);
                init();
            }
        }, 3000);
    }

    public void showLoader(){
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
        findViewById(R.id.recyclerview).setVisibility(View.GONE);
    }

    public void hideLoader(){
        findViewById(R.id.loader).setVisibility(View.GONE);
        findViewById(R.id.recyclerview).setVisibility(View.VISIBLE);
    }

    private void runtimePermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        new FetchSongs().execute();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void init(){

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        runtimePermission();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

        }
        return true;
    }*/
    public ArrayList<File> getSongs(File file){
        ArrayList<File> filesArray = new ArrayList<>();

        File[] files = file.listFiles();
        for(File singleFile:files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                filesArray.addAll(getSongs(singleFile));
            }else{
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                    filesArray.add(singleFile);
                }
            }
        }
        return filesArray;
    }

    public class FetchSongs extends AsyncTask<Void, Void, ArrayList<File>>{

        @Override
        protected void onPreExecute() {
            showLoader();
        }

        @Override
        protected ArrayList<File> doInBackground(Void... voids) {
            ArrayList<File> files = getSongs(Environment.getExternalStorageDirectory());
            return files;
        }

        @Override
        protected void onPostExecute(ArrayList<File> files) {
            adapter = new MusicAdapter(MainActivity.this, files);
            recyclerView.setAdapter(adapter);
            hideLoader();
        }
    }
}
