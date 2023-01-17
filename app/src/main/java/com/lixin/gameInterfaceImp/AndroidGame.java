package com.lixin.gameInterfaceImp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.lixin.gameInterface.Audio;
import com.lixin.gameInterface.FileIO;
import com.lixin.gameInterface.Game;
import com.lixin.gameInterface.Graphics;
import com.lixin.gameInterface.Input;
import com.lixin.gameInterface.Screen;
import com.lixin.view.MySurfaceView;

/**
 * Created by li on 2018/9/2.
 */

public abstract class AndroidGame extends Activity implements Game {
    MySurfaceView renderView;
    Graphics graphics;
    Input input;
    Audio audio;
    FileIO fileIO;
    Screen screen;
    PowerManager.WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //判断手机屏幕方向
        boolean isLandScape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandScape ? 480 : 320;
        int frameBufferHeight = isLandScape ? 320 : 480;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,frameBufferHeight, Bitmap.Config.RGB_565);
        float scaleX = frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();
         //  renderView = new AndroidFastRenderView(this,frameBuffer);
           renderView = new MySurfaceView(this);
           graphics = new AndroidGraphics(getAssets(),frameBuffer);
           fileIO = new AndroidFileIO(this);
           audio = new AndroidAudio(this);
           input = new AndroidInput(this,renderView,scaleX,scaleY);
           screen = getStartScreen();
           setContentView(renderView);
           PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
           wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,"GLGAME");
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
        renderView.resume();
        screen.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();
        if (isFinishing()){
            screen.dispose();
        }
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public void setScreen(Screen screen) {
        if (screen != null){
            this.screen.pause();
            this.screen.dispose();
            this.screen.resume();
            this.screen.update(0);
            this.screen = screen;
        }else {
            try {
                throw new IllegalAccessException("Screen must not be null!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Screen getCurrentScreen() {
        return screen;
    }
}
