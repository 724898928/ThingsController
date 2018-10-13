package com.lixin.gameInterfaceImp;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import com.lixin.gameInterface.Audio;
import com.lixin.gameInterface.Music;
import com.lixin.gameInterface.Sound;

import java.io.IOException;

/**
 * Created by li on 2018/5/23.
 */

public class AndroidAudio implements Audio {

    private AssetManager assetManager;
    private SoundPool soundPool;

    public AndroidAudio(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.assetManager = activity.getAssets();
        this.soundPool = new SoundPool(20,AudioManager.STREAM_MUSIC,0);
    }

    @Override
    public Music newMusic(String fileName) {
        try {
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(fileName);
            return new AndroiMusic(assetFileDescriptor);
        } catch (IOException e) {
         throw new RuntimeException("Couldn't load music '"+fileName+"'");
        }
    }

    @Override
    public Sound newSound(String fileName) {
        try {
            AssetFileDescriptor assetFileDescriptor = assetManager.openFd(fileName);
            int soundID = soundPool.load(assetFileDescriptor,0);
            return new AndroidSound(soundPool,soundID);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load Sound '"+fileName+"'");
        }
    }
}
