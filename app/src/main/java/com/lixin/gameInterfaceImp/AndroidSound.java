package com.lixin.gameInterfaceImp;

import android.media.SoundPool;

import com.lixin.gameInterface.Sound;

/**
 * Created by li on 2018/5/23.
 */

class AndroidSound implements Sound
{
    int soundID;
    SoundPool soundPool;
    public AndroidSound(SoundPool soundPool, int soundID)
    {
        this.soundID = soundID;
        this.soundPool = soundPool;
    }

    @Override
    public void play(float volume)
    {
        soundPool.play(soundID,volume,volume,0,0,1);
    }

    @Override
    public void dispose()
    {
        soundPool.unload(soundID);
    }


}

