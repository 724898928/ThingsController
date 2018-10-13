package com.lixin.gameInterface;

/**
 * Created by li on 2018/5/23.
 */

public interface Pixmap {
    public int getWidth();
    public int getHeight();
    public Graphics.PixmapFormat getFormat();
    public void dispose();

}
