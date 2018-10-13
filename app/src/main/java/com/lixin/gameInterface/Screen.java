package com.lixin.gameInterface;

/**
 * Created by li on 2018/5/23.
 */

public  abstract class Screen {
 protected final Game game;

    protected Screen(Game game) {
        this.game = game;
    }
    public abstract void update(float deltaTime);
    public abstract void present(float deltaTime);
    public abstract void pause();
    public abstract void resume();
    public abstract void dispose();
}
