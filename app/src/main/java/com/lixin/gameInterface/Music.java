package com.lixin.gameInterface;

/**
 * Created by li on 2018/5/23.
 */

public interface Music {
    public void play();
    public void stop();
    public void pause();
    public void dispose();

    public void setLooping(boolean looping);
    public void setVolume(float volume);

    public boolean isPlaying();
    public boolean isStopped();
    public boolean isLooping();

}
